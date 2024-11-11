package bot.telegram.webclient;

import bot.telegram.dto.OzonRsDto;

import java.time.LocalDate;

public interface OzonWebClientService {

    OzonRsDto call(LocalDate period);

}
