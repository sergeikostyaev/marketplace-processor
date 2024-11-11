package bot.telegram.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class YandexClientConfiguration {

    @Value("${marketplace.yandex.endpoint}")
    private String uri;

    @Value("${marketplace.yandex.itemEndpoint}")
    private String itemUri;

    @Value("${marketplace.yandex.token}")
    private String token;

    @Value("${marketplace.yandex.businessId}")
    private String busienssId;

}
