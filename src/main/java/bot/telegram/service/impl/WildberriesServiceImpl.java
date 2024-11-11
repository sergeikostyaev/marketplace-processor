package bot.telegram.service.impl;

import bot.telegram.dao.entity.GoodIdentifier;
import bot.telegram.dao.entity.Purchase;
import bot.telegram.dao.repository.GoodIdRepository;
import bot.telegram.dao.repository.PurchaseRepository;
import bot.telegram.dao.repository.WildberriesGoodRepository;
import bot.telegram.dto.WildberriesRsDto;
import bot.telegram.service.MarketplaceService;
import bot.telegram.service.RegionService;
import bot.telegram.webclient.WildberriesWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static bot.telegram.common.Constants.WILDBERRIES_UID_PATTERN;
import static bot.telegram.common.MarketplaceCode.WILDBERRIES;
import static java.util.Objects.isNull;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class WildberriesServiceImpl implements MarketplaceService {
    private final static List<String> BAD_ORDER_TYPES = List.of("АвтоВозврат МП", "Принудительный возврат");

    private final WildberriesWebClientService wildberriesWebClientService;

    private final RegionService regionService;

    private final PurchaseRepository purchaseRepository;

    private final WildberriesGoodRepository wildberriesGoodRepository;

    private final GoodIdRepository goodIdRepository;

    private final AtomicReference<Integer> orderNumber = new AtomicReference<>(1);

    private final Map<String, WildberriesRsDto> orderBuffer = new HashMap<>();

    @Override
    public void initServiceProcessing(BlockingQueue<String> messageQueue) {

        ScheduledExecutorService wildberriesClientExecutorService = Executors.newSingleThreadScheduledExecutor();
        wildberriesClientExecutorService.scheduleAtFixedRate(new Thread(() -> {
            Thread.currentThread().setName("Wildberries service thread");
            log.info("Registered thread: {}", Thread.currentThread().getName());


            List<WildberriesRsDto> boughtItems = wildberriesWebClientService.call(LocalDate.now(), true);
            if (Objects.nonNull(boughtItems) && !boughtItems.isEmpty()) {
                log.info("Called wildberries service with last return: {}", boughtItems.get(boughtItems.size() - 1));

                boughtItems = boughtItems.stream().peek(item -> {
                    String srid = item.getSrid();
                    String identifier = srid.contains(".") ? srid.substring(0, srid.lastIndexOf('.', srid.lastIndexOf('.') - 1)) : srid;
                    item.setIdentifier(identifier);
                }).toList();

                Map<String, Long> count = boughtItems.stream()
                        .collect(Collectors.groupingBy(item -> item.getIdentifier() + item.getSupplierArticle(), Collectors.counting()));

                var processedItems = boughtItems.stream().filter(order -> !BAD_ORDER_TYPES.contains(order.getOrderType()))
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        item -> item.getIdentifier() + item.getSupplierArticle(),
                                        item -> item,
                                        (existing, replacement) -> {

                                            if (!orderBuffer.containsKey(replacement.getSrid())) {
                                                orderBuffer.put(replacement.getSrid(), replacement);
                                                mapWildberriesResponseItemAndSavePurchase(orderNumber.get(), replacement);
                                            }

                                            return existing;
                                        }
                                ),
                                map -> map.values().stream()
                                        .peek(item -> {
                                            int number = Math.toIntExact(count.get(item.getIdentifier() + item.getSupplierArticle()));
                                            item.setNumber(number);
                                            item.setTotalPrice(item.getFinishedPrice() * number);
                                        })
                                        .sorted(Comparator.comparing(WildberriesRsDto::getDate))
                                        .collect(Collectors.toList())
                        ));

                processedItems.stream().filter(item -> !orderBuffer.containsKey(item.getSrid())).forEach(item -> {
                    messageQueue.add(mapWildberriesResponseItemAndSavePurchase(orderNumber.get(), item));
                    orderBuffer.put(item.getSrid(), item);
                    orderNumber.getAndSet(orderNumber.get() + 1);
                });

            } else {
                log.info("Called Wildberries service null or empty");
            }


            orderBuffer.entrySet().removeIf(entry -> {
                LocalDateTime orderDate = entry.getValue().getDate();
                return orderDate.isBefore(LocalDateTime.now().minusDays(1));
            });

        }), 5, 70, TimeUnit.SECONDS);
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void processOrderNumber() {
        orderNumber.set(1);
    }

    @Override
    public Integer countPeriodIncome(LocalDate period) {
        var boughtItems = wildberriesWebClientService.call(period, false);

        int totalSum = 0;

        if (Objects.nonNull(boughtItems) && !boughtItems.isEmpty()) {
            for (WildberriesRsDto item : boughtItems) {
                totalSum += item.getFinishedPrice();
            }
        }

        log.info("Counted wildberries income for period: {}", period);

        return totalSum;
    }

    @Override
    public Integer getBufferCount() {
        return orderBuffer.size();
    }

    private String mapWildberriesResponseItemAndSavePurchase(Integer orderNumber, WildberriesRsDto item) {
        String name = getItemName(item.getNmId());
        savePurchase(item, name);
        StringBuilder result = new StringBuilder(orderNumber + " заказ на Wildberries от " +
                item.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\n");
        result.append("Время: ").append(item.getDate().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n\n");

        String link = "https://www.wildberries.ru/catalog/" + item.getNmId() + "/detail.aspx?targetUrl=GP";

        result.append("Бренд: ").append(item.getBrand()).append("\n");
        result.append("Тип: ").append(item.getSubject()).append("\n");
        result.append("Артикул Wildberries: ").append(item.getNmId()).append("\n\n");
        result.append("Товар: [").append(name).append("](").append(link).append(")\n");
        result.append("Количество: ").append(item.getNumber()).append("\n\n");
        result.append("Страна: ").append(capitalizeFirstLetter(item.getCountryName())).append("\n");
        result.append("Округ: ").append(capitalizeFirstLetter(item.getOblastOkrugName())).append("\n");
        result.append("Регион: ").append(capitalizeFirstLetter(item.getRegionName())).append("\n\n");
        result.append("Конечная цена: ").append(item.getTotalPrice()).append(" руб\n");

        return result.toString();
    }

    private String capitalizeFirstLetter(String line) {
        if (line == null || line.isEmpty()) {
            return line;
        }

        char firstLetter = Character.toUpperCase(line.charAt(0));
        String remainingString = line.substring(1);
        return firstLetter + remainingString;
    }

    private void savePurchase(WildberriesRsDto item, String name) {

        GoodIdentifier goodIdentifier = goodIdRepository.findByWbId(item.getNmId());

        try {
            purchaseRepository.save(Purchase.builder()
                    .innerId(isNull(goodIdentifier) ? null : goodIdentifier.getInnerId())
                    .wildberriesId(item.getNmId())
                    .yandexId(isNull(goodIdentifier) ? null : goodIdentifier.getYandexId())
                    .ozonId(isNull(goodIdentifier) ? null : goodIdentifier.getOzonId())
                    .websiteId(isNull(goodIdentifier) ? null : goodIdentifier.getWebsiteId())
                    .yandexLink(isNull(goodIdentifier) ? null : goodIdentifier.getYandexLink())
                    .marketplaceCode(WILDBERRIES)
                    .name(name)
                    .uid(WILDBERRIES_UID_PATTERN.concat(item.getSrid()))
                    .date(item.getDate())
                    .region(regionService.getRegion(capitalizeFirstLetter(item.getRegionName())))
                    .price(item.getFinishedPrice().toString())
                    .build());

        } catch (Exception e) {
            log.info("Error saving purchase in WildberriesService");
        }
    }

    private String getItemName(String wbId) {
        String result = null;
        try {
            result = wildberriesGoodRepository.findByWbId(wbId).getWbName();

            if (isNull(result)) {
                result = goodIdRepository.findByWbId(wbId).getGoodName();
            }
        } catch (Exception e) {
            log.info("Error finding WildberriesGoodRepository");
        }
        return result;
    }
}