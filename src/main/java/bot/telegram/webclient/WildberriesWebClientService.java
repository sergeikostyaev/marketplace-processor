package bot.telegram.webclient;

import bot.telegram.dto.WildberriesRsDto;

import java.time.LocalDate;
import java.util.List;

public interface WildberriesWebClientService {
    List<WildberriesRsDto> call(LocalDate period, boolean isFlag);

}
