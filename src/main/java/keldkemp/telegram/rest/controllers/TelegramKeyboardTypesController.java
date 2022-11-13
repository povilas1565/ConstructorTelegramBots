package keldkemp.telegram.rest.controllers;

import keldkemp.telegram.rest.dto.telegram.TelegramKeyboardTypeDto;
import keldkemp.telegram.rest.mappers.TelegramMapper;
import keldkemp.telegram.services.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/telegram/keyboard")
public class TelegramKeyboardTypesController {

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private TelegramMapper telegramMapper;

    @GetMapping("/type")
    public List<TelegramKeyboardTypeDto> getTelegramKeyboardTypes() {
        return telegramMapper.toTelegramKeyboardTypesDtoFromPo(telegramBotService.getKeyboardTypes());
    }
}
