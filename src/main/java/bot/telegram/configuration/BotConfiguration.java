package bot.telegram.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class BotConfiguration {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

}
