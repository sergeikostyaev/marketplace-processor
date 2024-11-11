package bot.telegram.webclient.impl;

import bot.telegram.configuration.YandexClientConfiguration;
import bot.telegram.dto.YandexItemRqDto;
import bot.telegram.dto.YandexItemRsDto;
import bot.telegram.dto.YandexRsDto;
import bot.telegram.webclient.YandexWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexWebClient implements YandexWebClientService {

    private final YandexClientConfiguration yandexClientConfiguration;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_FLAG = "Bearer ";

    @Override
    public YandexRsDto call() {
        WebClient webClient = WebClient.builder().build();

        String uri = yandexClientConfiguration.getUri() + LocalDate.now();

        YandexRsDto result = null;

        try {
            log.info("Yandex client call with endpoint: {}", uri);

            result = webClient.get()
                    .uri(uri)
                    .header(AUTHORIZATION_HEADER, AUTHORIZATION_FLAG + yandexClientConfiguration.getToken())
                    .retrieve()
                    .bodyToMono(YandexRsDto.class)
                    .block();

        } catch (Exception e) {
            log.error("Yandex client call error: {}", e.getMessage() + " " + e.getCause());
        }
        log.info("Yandex client call success");

        return result;
    }

    @Override
    public YandexRsDto call(LocalDate period) {
        WebClient webClient = WebClient.builder().build();

        String uri = yandexClientConfiguration.getUri() + period;

        YandexRsDto result = null;

        try {
            log.info("Yandex client call with endpoint: {}", uri);

            result = webClient.get()
                    .uri(uri)
                    .header(AUTHORIZATION_HEADER, AUTHORIZATION_FLAG + yandexClientConfiguration.getToken())
                    .retrieve()
                    .bodyToMono(YandexRsDto.class)
                    .block();

        } catch (Exception e) {
            log.error("Yandex client call error: {}", e.getMessage() + " " + e.getCause());
        }
        log.info("Yandex client call success");

        return result;
    }

    @Override
    public YandexItemRsDto call(String itemId) {
        WebClient webClient = WebClient.builder().build();

        String uri = yandexClientConfiguration.getItemUri();

        YandexItemRsDto result = null;

        try {
            log.info("Yandex item client call with endpoint: {}", uri);

            result = webClient.post()
                    .uri(uri)
                    .body(BodyInserters.fromValue(YandexItemRqDto.builder().offerIds(List.of(itemId)).build()))
                    .header(AUTHORIZATION_HEADER, AUTHORIZATION_FLAG + yandexClientConfiguration.getToken())
                    .retrieve()
                    .bodyToMono(YandexItemRsDto.class)
                    .block();

        } catch (Exception e) {
            log.error("Yandex item client call error: {}", e.getMessage() + " " + e.getCause());
        }
        log.info("Yandex item client call success");

        return result;
    }

}
