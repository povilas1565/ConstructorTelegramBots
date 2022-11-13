package keldkemp.telegram.rest.controllers;

import keldkemp.telegram.services.BeanFactoryService;
import keldkemp.telegram.telegram.config.WebHookBot;
import keldkemp.telegram.util.ResponseEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class TelegramController {

    @Autowired
    @Qualifier("telegramBotBeanService")
    private BeanFactoryService telegramBotsBean;

    @PostMapping("/webhook/{token}")
    public ResponseEntity<?> onUpdateReceived(HttpServletRequest request, @RequestBody Update update, @PathVariable String token) {
        //TODO: За прокси надо подумать как сделать
        /*
        try {
            if (!checkDomainName(request.getRequestURL().toString()) || !checkDomainName(request.getHeader("x-forwarded-proto"))) {
                return ResponseEntityUtils.badRequest();
            }
        } catch (Exception ignored) {}
         */
        WebHookBot bot = telegramBotsBean.getBean(token);
        bot.onWebhookUpdateReceived(update);
        return null;
    }

    private boolean checkDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.endsWith("ngrok.io") || domain.endsWith("telegram.org");
    }
}
