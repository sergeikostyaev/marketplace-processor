package bot.telegram.webclient.impl;

import bot.telegram.configuration.WildberriesClientConfiguration;
import bot.telegram.dto.WildberriesRsDto;
import bot.telegram.webclient.WildberriesWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WildberriesWebClient implements WildberriesWebClientService {

    private final WildberriesClientConfiguration wildberriesClientConfiguration;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String FLAG = "&flag=1";

    @Override
    public List<WildberriesRsDto> call(LocalDate period, boolean isFlag) {

        WebClient webClient;

        if (!isFlag) {
            final int size = 1024 * 1024;
            final ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                    .build();

            webClient = WebClient.builder().exchangeStrategies(strategies).build();
        } else {
            webClient = WebClient.builder().build();
        }


        String uri = wildberriesClientConfiguration.getUri().concat(period.toString()).concat(isFlag ? FLAG : "");

        WildberriesRsDto[] result = null;

        try {
            log.info("Wildberries client call with endpoint: {}", uri);

            result = webClient.get()
                    .uri(uri)
                    .header(AUTHORIZATION_HEADER, wildberriesClientConfiguration.getToken())
                    .retrieve()
                    .bodyToMono(WildberriesRsDto[].class)
                    .block();

        } catch (Exception e) {
            log.error("Wildberries client call error: {}", e.getMessage() + " " + e.getCause());
        }
        log.info("Wildberries client call success");

        return Objects.isNull(result) ? Collections.emptyList() : Arrays.stream(result).toList();
    }

}
