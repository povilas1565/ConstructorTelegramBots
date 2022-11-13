package keldkemp.telegram.services.impl;

import keldkemp.telegram.services.LockService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class LockServiceImpl implements LockService {

    private final Logger logger = LoggerFactory.getLogger(LockServiceImpl.class);
    private static final String TRY_LOCK_MSG = "Try to lock (name = {}, uuid = {})";
    private static final String CAPTURE_LOCK_MSG = "Lock captured (name = {}, uuid = {}, wait time = {} seconds)";
    private static final String UNLOCK_LOCK_MSG = "Lock unlocked (name = {}, uuid = {}, wait+exec time = {} seconds)";
    private static final int WARN_CAPTURE_TIME_SECONDS = 10;
    private static final String WARN_CAPTURE_TIME_MSG = "Lock capture time {} seconds. Stacktrace: {}";

    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    @Override
    public Lock getLock(String lockName) {
        return locks.computeIfAbsent(lockName, key -> new ReentrantLock());
    }

    @Override
    public void doInLock(String name, Runnable runnable) {
        Lock cacheLock = getLock(name);
        int seconds = getCurrentSeconds();
        String uuid = UUID.randomUUID().toString();
        try {
            logger.debug(TRY_LOCK_MSG, name, uuid);
            cacheLock.lock();
            logger.debug(CAPTURE_LOCK_MSG, name, uuid, getCurrentSeconds() - seconds);
            warnLongCaptureTime(getCurrentSeconds() - seconds);
            runnable.run();
        } finally {
            cacheLock.unlock();
            logger.debug(UNLOCK_LOCK_MSG, name, uuid, getCurrentSeconds() - seconds);
        }
    }

    @Override
    public <V> V doInLock(String name, Callable<V> callable) {
        Lock cacheLock = getLock(name);
        int seconds = getCurrentSeconds();
        String uuid = UUID.randomUUID().toString();
        try {
            logger.debug(TRY_LOCK_MSG, name, uuid);
            cacheLock.lock();
            logger.debug(CAPTURE_LOCK_MSG, name, uuid, getCurrentSeconds() - seconds);
            warnLongCaptureTime(getCurrentSeconds() - seconds);
            try {
                return callable.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                } else {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            cacheLock.unlock();
            logger.debug(UNLOCK_LOCK_MSG, name, uuid, getCurrentSeconds() - seconds);
        }
    }

    private int getCurrentSeconds() {
        return LocalTime.now().toSecondOfDay();
    }

    private void warnLongCaptureTime(int seconds) {
        if (seconds >= WARN_CAPTURE_TIME_SECONDS) {
            logger.warn(WARN_CAPTURE_TIME_MSG, seconds, stackTraceStr());
        }
    }

    private String stackTraceStr() {
        return StringUtils.join(Thread.currentThread().getStackTrace(), " <- ");
    }
}
