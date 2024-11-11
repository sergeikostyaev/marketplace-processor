package bot.telegram.service;

import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;

public interface MarketplaceService {

    void initServiceProcessing(BlockingQueue<String> messageQueue);

    Integer countPeriodIncome(LocalDate period);

    Integer getBufferCount();

}
