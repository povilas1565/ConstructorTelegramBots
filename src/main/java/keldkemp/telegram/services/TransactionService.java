package keldkemp.telegram.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

public interface TransactionService {

    /**
     * Call the method in transaction like {@link Transactional}.
     *
     * @param callable method
     * @param <V>      casted result
     * @return result of the method
     */
    <V> V doInTransactionAnnotation(Callable<V> callable);

    /**
     * Call the method in transaction like {@link Transactional}.
     *
     * @param runnable method
     */
    void doInTransactionAnnotation(Runnable runnable);
}
