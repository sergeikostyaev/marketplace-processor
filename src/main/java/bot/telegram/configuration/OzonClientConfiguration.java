package bot.telegram.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class OzonClientConfiguration {


    @Value("${marketplace.ozon.endpoint}")
    private String uri;

    @Value("${marketplace.ozon.clientId}")
    private String clientId;

    @Value("${marketplace.ozon.token}")
    private String token;

}
