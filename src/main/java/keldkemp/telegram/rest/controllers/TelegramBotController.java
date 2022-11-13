package keldkemp.telegram.rest.controllers;

import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.rest.dto.telegram.TelegramBotDto;
import keldkemp.telegram.rest.dto.telegram.TelegramStageTransferDto;
import keldkemp.telegram.rest.mappers.TelegramMapper;
import keldkemp.telegram.services.TelegramBotService;
import keldkemp.telegram.util.ResponseEntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/telegram/bot")
public class TelegramBotController {

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private TelegramMapper telegramMapper;

    @GetMapping("/list")
    public List<TelegramBotDto> getTelegramBotsList() {
        return telegramMapper.toTelegramBotsDtoFromPo(telegramBotService.getBots());
    }

    @GetMapping("/{id}")
    public TelegramBotDto getTelegramBot(@PathVariable Long id) {
        return telegramMapper.toTelegramBotDtoFromPo(telegramBotService.getBot(id));
    }

    @PostMapping()
    public TelegramBotDto saveTelegramBot(@RequestBody TelegramBotDto telegramBotDto) {
        return telegramMapper.toTelegramBotDtoFromPo(telegramBotService.save(telegramMapper.toTelegramBotPoFromDto(telegramBotDto)));
    }

    @GetMapping("/{id}/stages")
    public TelegramStageTransferDto getStages(@PathVariable Long id) {
        TelegramStageTransferDto transferDto = new TelegramStageTransferDto();
        List<TelegramStages> stages = telegramBotService.getStages(id);
        transferDto.setTelegramStages(telegramMapper.toTelegramStagesDtoFromPo(stages));
        if (stages.size() > 0) {
            transferDto.setFrontOptions(stages.get(0).getTelegramBot().getFrontOptions());
        }
        return transferDto;
    }

    @PostMapping("/{id}/stages")
    public TelegramStageTransferDto saveStages(@PathVariable Long id, @RequestBody TelegramStageTransferDto telegramStageTransferDto) {
        return telegramBotService.saveStages(telegramStageTransferDto, id);
    }

    @DeleteMapping("/{id}/stages")
    public ResponseEntity<?> deleteStages(@PathVariable Long id) {
        telegramBotService.deleteStages(id);
        return ResponseEntityUtils.okRequest();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTelegramBot(@PathVariable Long id) {
        telegramBotService.delete(id);
        return ResponseEntityUtils.okRequest();
    }
}
