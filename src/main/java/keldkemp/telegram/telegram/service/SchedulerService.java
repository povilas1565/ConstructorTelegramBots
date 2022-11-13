package keldkemp.telegram.telegram.service;

import keldkemp.telegram.models.TelegramStages;

public interface SchedulerService {

    void cancelStageSchedule(TelegramStages telegramStages);

    void handleStageSchedule(TelegramStages telegramStages);
}
