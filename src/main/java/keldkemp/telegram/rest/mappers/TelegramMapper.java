package keldkemp.telegram.rest.mappers;

import keldkemp.telegram.models.*;
import keldkemp.telegram.rest.dto.telegram.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TelegramMapper {

    public abstract List<TelegramBotDto> toTelegramBotsDtoFromPo(List<TelegramBots> bot);

    public abstract TelegramBotDto toTelegramBotDtoFromPo(TelegramBots bot);

    public abstract List<TelegramBots> toTelegramBotsPoFromDto(List<TelegramBotDto> botDto);

    public abstract TelegramBots toTelegramBotPoFromDto(TelegramBotDto botDto);

    public abstract List<TelegramStageDto> toTelegramStagesDtoFromPo(List<TelegramStages> stages);

    public abstract List<TelegramStages> toTelegramStagesPoFromDto(List<TelegramStageDto> stagesDto);

    @Mapping(target = "telegramBot", ignore = true)
    @Mapping(target = "fictiveId", ignore = true)
    public abstract TelegramStageDto toTelegramStageDtoFromPo(TelegramStages stage);

    public abstract TelegramStages toTelegramStagePoFromDto(TelegramStageDto stageDto);

    public abstract List<TelegramButtonDto> toTelegramButtonsDtoFromPo(List<TelegramButtons> buttons);

    public abstract List<TelegramButtons> toTelegramButtonsPoFromDto(List<TelegramButtonDto> buttonsDto);

    public TelegramButtonDto toTelegramButtonDtoFromPo(TelegramButtons button) {
        if ( button == null ) {
            return null;
        }

        TelegramButtonDto telegramButtonDto = new TelegramButtonDto();
        TelegramStageDto telegramStageDto = new TelegramStageDto();
        if (button.getCallbackData() != null) {
            telegramStageDto.setId(button.getCallbackData().getId());
        }

        telegramButtonDto.setId( button.getId() );
        telegramButtonDto.setButtonText( button.getButtonText() );
        telegramButtonDto.setButtonLink( button.getButtonLink() );
        telegramButtonDto.setButtonOrd( button.getButtonOrd() );
        telegramButtonDto.setCallbackData( telegramStageDto );
        telegramButtonDto.setFrontNodeId( button.getFrontNodeId() );

        return telegramButtonDto;
    }

    public abstract TelegramButtons toTelegramButtonPoFromDto(TelegramButtonDto buttonDto);

    public abstract List<TelegramMessageDto> toTelegramMessagesDtoFromPo(List<TelegramMessages> messages);

    public abstract List<TelegramMessages> toTelegramMessagesPoFromDto(List<TelegramMessageDto> messagesDto);

    public abstract List<TelegramKeyboardDto> toTelegramKeyboardsDtoFromPo(List<TelegramKeyboards> keyboards);

    public abstract List<TelegramKeyboards> toTelegramKeyboardsPoFromDto(List<TelegramKeyboardDto> keyboardsDto);

    public abstract List<TelegramKeyboardRowDto> toTelegramRowsDtoFromPo(List<TelegramKeyboardRows> rows);

    public abstract List<TelegramKeyboardRows> toTelegramRowsPoFromDto(List<TelegramKeyboardRowDto> rowsDto);

    public abstract List<TelegramKeyboardTypeDto> toTelegramKeyboardTypesDtoFromPo(List<TelegramKeyboardTypes> types);

    public abstract List<TelegramKeyboardTypes> toTelegramKeyboardTypesPoFromDto(List<TelegramKeyboardTypeDto> typesDto);
}
