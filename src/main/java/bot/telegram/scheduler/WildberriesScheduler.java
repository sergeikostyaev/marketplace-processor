package bot.telegram.scheduler;

import bot.telegram.dao.entity.GoodIdentifier;
import bot.telegram.dao.entity.Purchase;
import bot.telegram.dao.repository.GoodIdRepository;
import bot.telegram.dao.repository.PurchaseRepository;
import bot.telegram.dto.WildberriesRsDto;
import bot.telegram.service.RegionService;
import bot.telegram.webclient.WildberriesWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static bot.telegram.common.Constants.CANCELED_PURCHASE_STATUS;
import static bot.telegram.common.Constants.WILDBERRIES_UID_PATTERN;
import static bot.telegram.common.MarketplaceCode.WILDBERRIES;
import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class WildberriesScheduler {

    private final PurchaseRepository purchaseRepository;

    private final GoodIdRepository goodIdRepository;

    private final WildberriesWebClientService wildberriesWebClientService;

    private final RegionService regionService;


    @Scheduled(cron = "0 0 7,10,18 * * *")
    public void processWildberriesCancels() {
        try {
            List<WildberriesRsDto> purchases = wildberriesWebClientService.call(LocalDate.now().minusDays(10), false);

            List<String> canceledUids = purchases.stream().filter(p -> Boolean.TRUE.equals(p.getIsCancel()))
                    .map(p -> WILDBERRIES_UID_PATTERN.concat(p.getSrid())).toList();
            log.info("Wildberries cancel processing started, cancelled uids: {}", canceledUids.size());

            if (!canceledUids.isEmpty()) {
                List<Purchase> cancelledPurchases = purchaseRepository.findAllByUid(canceledUids).stream().peek(p -> p.setStatus(CANCELED_PURCHASE_STATUS)).toList();

                purchaseRepository.saveAll(cancelledPurchases);
                log.info("Wildberries cancel processing performed, canceled purchases: {}", cancelledPurchases.size());
            }

        } catch (Exception e) {
            log.error("Wildberries cancel processing call error: {}", e.getMessage() + " " + e.getCause());
        }
    }

    @Scheduled(cron = "0 0 10,21,22,23 * * *")
    public void processWildberriesLostPurchases() {
        try {

            LocalDate yesterday = LocalDate.now().minusDays(1);

            List<WildberriesRsDto> wildberriesRsDtoList = wildberriesWebClientService.call(yesterday, true);
            List<Purchase> purchases = purchaseRepository.findAllByDate(yesterday).stream()
                    .filter(purchase -> WILDBERRIES.equals(purchase.getMarketplaceCode())).toList();
            log.info("Wildberries lost purchases processing started, wbrs: {}, purchases: {}", wildberriesRsDtoList.size(), purchases.size());

            if (wildberriesRsDtoList.isEmpty()) {
                wildberriesRsDtoList = wildberriesWebClientService.call(yesterday, true);
            }

            if (wildberriesRsDtoList.size() > purchases.size()) {
                List<String> purchaseIds = purchases.stream().map(Purchase::getUid).toList();
                List<WildberriesRsDto> lostWbPurchases = wildberriesRsDtoList.stream()
                        .filter(wbRsDto -> !purchaseIds.contains(WILDBERRIES_UID_PATTERN.concat(wbRsDto.getSrid()))).toList();

                log.info("Wildberries lost purchases found: {}", lostWbPurchases.stream().map(p -> p.getSrid().concat("-").concat(p.getDate().toString())));

                lostWbPurchases.forEach(lostWbPurchase -> {
                    GoodIdentifier goodIdentifier = goodIdRepository.findByWbId(lostWbPurchase.getNmId());

                    try {
                        purchaseRepository.save(Purchase.builder()
                                .innerId(isNull(goodIdentifier) ? null : goodIdentifier.getInnerId())
                                .wildberriesId(lostWbPurchase.getNmId())
                                .yandexId(isNull(goodIdentifier) ? null : goodIdentifier.getYandexId())
                                .ozonId(isNull(goodIdentifier) ? null : goodIdentifier.getOzonId())
                                .websiteId(isNull(goodIdentifier) ? null : goodIdentifier.getWebsiteId())
                                .yandexLink(isNull(goodIdentifier) ? null : goodIdentifier.getYandexLink())
                                .marketplaceCode(WILDBERRIES)
                                .name(getItemName(lostWbPurchase.getNmId()))
                                .uid(WILDBERRIES_UID_PATTERN.concat(lostWbPurchase.getSrid()))
                                .date(lostWbPurchase.getDate())
                                .region(regionService.getRegion(capitalizeFirstLetter(lostWbPurchase.getRegionName())))
                                .price(lostWbPurchase.getFinishedPrice().toString())
                                .build());

                    } catch (Exception e) {
                        log.info("Error saving purchase in WildberriesScheduler");
                    }
                });

            }
        } catch (Exception e) {
            log.error("Wildberries lost purchases processing error: {}", e.getMessage() + " " + e.getCause());
        }
    }

    private String getItemName(String wbId) {
        String result = null;
        try {
            result = goodIdRepository.findByWbId(wbId).getGoodName();
        } catch (Exception e) {
            log.info("Error finding WildberriesGoodRepository");
        }
        return result;
    }

    private String capitalizeFirstLetter(String line) {
        if (line == null || line.isEmpty()) {
            return line;
        }

        char firstLetter = Character.toUpperCase(line.charAt(0));
        String remainingString = line.substring(1);
        return firstLetter + remainingString;
    }

}
