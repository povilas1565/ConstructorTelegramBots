package keldkemp.telegram.rest.dto.telegram;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TelegramBotDto {
    private Long id;
    private String botName;
    private String botToken;
    private Boolean isActive;
}
