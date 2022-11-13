package keldkemp.telegram.rest.dto.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TelegramStageDto {
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long fictiveId;
    private String name;
    private Long previousStage;
    private List<TelegramMessageDto> telegramMessages;
    private List<TelegramKeyboardDto> telegramKeyboards;
    private TelegramBotDto telegramBot;
    private String frontPrefixReplace;
    private String frontNodeId;
    private Boolean isScheduleActive;
    private String scheduleCron;
    private LocalDateTime scheduleDateTime;
}
