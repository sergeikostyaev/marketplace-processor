package bot.telegram.controller;

import bot.telegram.dto.WebsiteRqDto;
import bot.telegram.service.WebsiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BotRestController {

    private final WebsiteService websiteService;

    @PostMapping("/api/order")
    public void getWebsiteRequest(@RequestBody WebsiteRqDto request) {
        websiteService.addMessage(request);
    }

}
