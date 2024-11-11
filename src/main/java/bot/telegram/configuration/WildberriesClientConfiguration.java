package bot.telegram.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class WildberriesClientConfiguration {

    @Value("${marketplace.wildberries.endpoint}")
    private String uri;

    @Value("${marketplace.wildberries.token}")
    private String token;

}
