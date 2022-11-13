package keldkemp.telegram.services;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public interface LockService {

    Lock getLock(String lockName);

    void doInLock(String name, Runnable runnable);

    <V> V doInLock(String name, Callable<V> callable);
}
