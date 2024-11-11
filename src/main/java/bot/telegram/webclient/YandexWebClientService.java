package bot.telegram.webclient;

import bot.telegram.dto.YandexItemRsDto;
import bot.telegram.dto.YandexRsDto;

import java.time.LocalDate;

public interface YandexWebClientService {

    YandexRsDto call();

    YandexRsDto call(LocalDate period);

    YandexItemRsDto call(String itemId);
}
