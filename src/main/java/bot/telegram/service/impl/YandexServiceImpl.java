package bot.telegram.service.impl;

import bot.telegram.configuration.YandexClientConfiguration;
import bot.telegram.dao.entity.GoodIdentifier;
import bot.telegram.dao.entity.Purchase;
import bot.telegram.dao.repository.GoodIdRepository;
import bot.telegram.dao.repository.PurchaseRepository;
import bot.telegram.dto.YandexItemRsDto;
import bot.telegram.dto.YandexRsDto;
import bot.telegram.service.MarketplaceService;
import bot.telegram.service.RegionService;
import bot.telegram.webclient.YandexWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static bot.telegram.common.MarketplaceCode.YANDEX;
import static java.util.Objects.isNull;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class YandexServiceImpl implements MarketplaceService {

    private final YandexWebClientService yandexWebClientService;

    private final RegionService regionService;

    private final PurchaseRepository purchaseRepository;

    private final GoodIdRepository goodIdRepository;

    private final YandexClientConfiguration yandexClientConfiguration;

    private final AtomicReference<Integer> orderNumber = new AtomicReference<>(1);

    private final Map<Long, YandexRsDto.Order> orderBuffer = new HashMap<>();

    @Override
    public void initServiceProcessing(BlockingQueue<String> messageQueue) {

        ScheduledExecutorService yandexClientExecutorService = Executors.newSingleThreadScheduledExecutor();
        yandexClientExecutorService.scheduleAtFixedRate(new Thread(() -> {
            Thread.currentThread().setName("Yandex service thread");
            log.info("Registered thread: {}", Thread.currentThread().getName());


            YandexRsDto response = yandexWebClientService.call();
            boolean responseIsRelevant = Objects.nonNull(response) && Objects.nonNull(response.getOrders()) && !response.getOrders().isEmpty();
            log.info("Called Yandex service. Is relevant: {}", responseIsRelevant);

            if (responseIsRelevant) {
                List<YandexRsDto.Order> orders = response.getOrders();

                orders.stream().filter(order -> !orderBuffer.containsKey(order.getId()))
                        .sorted(Comparator.comparing(item -> LocalDateTime.parse(item.getCreationDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))))
                        .forEach(order -> {
                            messageQueue.add(mapYandexResponseItemAndSavePurchase(orderNumber.get(), order));
                            orderBuffer.put(order.getId(), order);
                            orderNumber.getAndSet(orderNumber.get() + 1);
                        });

            }

            orderBuffer.entrySet().removeIf(entry -> {
                LocalDateTime orderDate = LocalDateTime.parse(entry.getValue().getCreationDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
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
        var yandexOrders = yandexWebClientService.call(period);

        int totalSum = 0;

        if (Objects.nonNull(yandexOrders) && !yandexOrders.getOrders().isEmpty()) {
            for (YandexRsDto.Order order : yandexOrders.getOrders()) {
                totalSum += order.getBuyerTotal();
            }
        }

        log.info("Counted yandex income for period: {}", period);

        return totalSum;
    }

    @Override
    public Integer getBufferCount() {
        return orderBuffer.size();
    }

    private String mapYandexResponseItemAndSavePurchase(Integer orderNumber, YandexRsDto.Order order) {
        StringBuilder result = new StringBuilder(orderNumber + " заказ на YandexMarket от " +
                order.getCreationDate().substring(0, order.getCreationDate().indexOf(' ')) + "\n");
        result.append("Время: " + order.getCreationDate().substring(order.getCreationDate().indexOf(' '), order.getCreationDate().length()) + "\n\n");

        String link2 = "https://market.yandex.ru/product/%s?sku=%s&businessId=%s";

        String country = "";
        String countryDistrict = "";
        String republic = "";

        YandexRsDto.Region regionPart = order.getDelivery().getRegion();

        while (Objects.nonNull(regionPart)) {

            switch (regionPart.getType()) {
                case "COUNTRY": {
                    country = regionPart.getName();
                    regionPart = regionPart.getParent();
                    break;
                }
                case "COUNTRY_DISTRICT": {
                    countryDistrict = regionPart.getName();
                    regionPart = regionPart.getParent();
                    break;
                }
                case "REPUBLIC": {
                    republic = regionPart.getName();
                    regionPart = regionPart.getParent();
                    break;
                }
                default: {
                    regionPart = regionPart.getParent();
                    break;
                }
            }
        }

        String finalRepublic = republic;
        order.getItems().forEach(item -> {

            YandexItemRsDto itemInfo = yandexWebClientService.call(item.getOfferId());

            result.append("Бренд: " + itemInfo.getResult().getOfferMappings().get(0).getOffer().getVendor() + "\n");
            result.append("Тип: " + itemInfo.getResult().getOfferMappings().get(0).getOffer().getCategory() + "\n");
            result.append("Артикул YandexMarket: " + itemInfo.getResult().getOfferMappings().get(0).getMapping().getMarketModelId() + "\n\n");

            String productId = String.valueOf(itemInfo.getResult().getOfferMappings().get(0).getMapping().getMarketModelId());
            String productSku = String.valueOf(itemInfo.getResult().getOfferMappings().get(0).getMapping().getMarketSku());


            result.append("Товар: [" + item.getOfferName() + "](" + String.format(link2, productId, productSku, yandexClientConfiguration.getBusienssId()) + ")");
            result.append("\nКоличество: " + item.getCount() + "\n\n");

            savePurchase(itemInfo, item.getCount(), item.getOfferName(), finalRepublic,
                    item.getBuyerPrice().toString().substring(0, item.getBuyerPrice().toString().indexOf(".")), item.getOfferId(),
                    itemInfo.getResult().getOfferMappings().get(0).getOffer().getVendorCode()
            );
        });


        result.append("Страна: " + country + "\n");
        result.append("Округ: " + countryDistrict + "\n");
        result.append("Регион: " + republic + "\n\n");
        result.append("Конечная цена: " + order.getBuyerTotal() + " руб\n");


        return result.toString();
    }

    private void savePurchase(YandexItemRsDto itemInfo, Integer count, String name, String region, String price, String offerId, String innerId) {
        try {

            String yandexLink = null;

            GoodIdentifier goodIdentifier = goodIdRepository.findByYandexLink(offerId);
            yandexLink = offerId;

            if (isNull(goodIdentifier)) {
                goodIdentifier = goodIdRepository.findByInnerId(innerId);
            }

            for (int i = 0; i < count; i++) {
                purchaseRepository.save(Purchase.builder()
                        .innerId(isNull(goodIdentifier) ? null : goodIdentifier.getInnerId())
                        .yandexLink(yandexLink)
                        .yandexId(isNull(goodIdentifier) ? null : goodIdentifier.getYandexId())
                        .ozonId(isNull(goodIdentifier) ? null : goodIdentifier.getOzonId())
                        .websiteId(isNull(goodIdentifier) ? null : goodIdentifier.getWebsiteId())
                        .wildberriesId(isNull(goodIdentifier) ? null : goodIdentifier.getWbId())
                        .marketplaceCode(YANDEX)
                        .date(LocalDateTime.now(ZoneId.of("Europe/Moscow")))
                        .name(name)
                        .region(regionService.getRegion(region))
                        .price(price)
                        .build());
            }
        } catch (Exception e) {
            log.info("Error saving purchase in YandexService");
        }
    }
}

