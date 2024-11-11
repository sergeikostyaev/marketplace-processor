package bot.telegram.webclient.impl;

import bot.telegram.configuration.OzonClientConfiguration;
import bot.telegram.dto.OzonRqDto;
import bot.telegram.dto.OzonRsDto;
import bot.telegram.webclient.OzonWebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class OzonWebClient implements OzonWebClientService {

    private final OzonClientConfiguration ozonClientConfiguration;

    private static final String CLIENT_ID_HEADER = "Client-Id";

    private static final String AUTHORIZATION_HEADER = "Api-Key";

    @Override
    public OzonRsDto call(LocalDate period) {
        WebClient webClient = WebClient.builder().build();

        String uri = ozonClientConfiguration.getUri();

        OzonRsDto result = null;

        try {
            log.info("Ozon client call with endpoint: {}", uri);

            result = webClient.post()
                    .uri(uri)
                    .body(BodyInserters.fromValue(OzonRqDto.builder()
                            .dir("ASC")
                            .filter(OzonRqDto.Filter.builder().since(period + "T00:00:00.000Z").build())
                            .limit(100)
                            .offset(0)
                            .with(OzonRqDto.With.builder()
                                    .analytics_data(true)
                                    .barcodes(false)
                                    .financial_data(true)
                                    .translit(true)
                                    .build())
                            .build()))
                    .header(AUTHORIZATION_HEADER, ozonClientConfiguration.getToken())
                    .header(CLIENT_ID_HEADER, ozonClientConfiguration.getClientId())
                    .retrieve()
                    .bodyToMono(OzonRsDto.class)
                    .block();

        } catch (Exception e) {
            log.error("Ozon client call error: {}", e.getMessage() + " " + e.getCause());
        }
        log.info("Ozon client call success");

        return result;
    }

}
