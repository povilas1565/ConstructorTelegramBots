package keldkemp.telegram.rest.dto.telegram;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TelegramButtonDto {
    private Long id;
    private String buttonText;
    private String buttonLink;
    private Long buttonOrd;
    private TelegramStageDto callbackData;
    private String frontNodeId;
}
