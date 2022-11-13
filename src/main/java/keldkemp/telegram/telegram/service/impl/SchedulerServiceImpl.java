package keldkemp.telegram.telegram.service.impl;

import keldkemp.telegram.models.TelegramStages;
import keldkemp.telegram.services.BeanFactoryService;
import keldkemp.telegram.telegram.config.WebHookBot;
import keldkemp.telegram.telegram.handler.MessageHandler;
import keldkemp.telegram.telegram.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private final static String THREAD_NAME = "tg-scheduler-";

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    @Qualifier("telegramBotBeanService")
    private BeanFactoryService telegramBotsBean;

    private ThreadPoolTaskScheduler executorService;

    private final ConcurrentMap<String, ScheduledFuture<?>> tStagesSchedule = new ConcurrentReferenceHashMap<>();

    @Override
    public void cancelStageSchedule(TelegramStages telegramStages) {
        String pkApp = "stage_schedule_" + telegramStages.getId();
        tStagesSchedule.computeIfPresent(pkApp, (k, v) -> {
            v.cancel(true);
            return null;
        });

        logger.debug(String.format("Cancel stage scheduler pk: {%s}", pkApp));
    }

    @Override
    public void handleStageSchedule(TelegramStages telegramStages) {
        initThread();
        String pkApp = "stage_schedule_" + telegramStages.getId();

        tStagesSchedule.computeIfPresent(pkApp, (k, v) -> {
            v.cancel(true);
            return null;
        });

        if (telegramStages.getScheduleCron() != null ||
                telegramStages.getScheduleDateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() - System.currentTimeMillis() >= 0) {
            tStagesSchedule.put(pkApp, scheduleStage(telegramStages));
            logger.debug(String.format("Set schedule for stage pk: {%s}", pkApp));
        }
    }

    private ScheduledFuture<?> scheduleStage(TelegramStages stages) {
        String pkApp = "stage_schedule_" + stages.getId();
        String token = stages.getTelegramBot().getBotToken();

        if (stages.getScheduleCron() != null) {
            CronTrigger cronTrigger = new CronTrigger(stages.getScheduleCron());
            return executorService.schedule(() -> {
                logger.debug(String.format("Run cron schedule stage pk: {%s}", pkApp));
                getTgBot(token).execute(messageHandler.handleSchedule(stages.getId()));
                logger.debug(String.format("End run cron schedule stage pk: {%s}", pkApp));
            }, cronTrigger);
        } else {
            long millis = stages.getScheduleDateTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() - System.currentTimeMillis();
            if (millis >= 0) {
                PeriodicTrigger periodicTrigger = new PeriodicTrigger(millis, TimeUnit.MILLISECONDS);
                return executorService.schedule(() -> {
                    logger.debug(String.format("Run date schedule stage pk: {%s}", pkApp));
                    getTgBot(token).execute(messageHandler.handleSchedule(stages.getId()));
                    logger.debug(String.format("End run date schedule stage pk: {%s}", pkApp));
                }, periodicTrigger);
            }
            return executorService.schedule(() -> {}, new Date());
        }
    }

    private void initThread() {
        if (executorService == null) {
            executorService = new ThreadPoolTaskScheduler();
            executorService.setPoolSize(10);
            executorService.setThreadNamePrefix(THREAD_NAME);
            executorService.initialize();
        }
    }

    private WebHookBot getTgBot(String token) {
        return telegramBotsBean.getBean(token);
    }
}
