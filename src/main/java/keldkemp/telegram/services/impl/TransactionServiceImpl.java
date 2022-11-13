package keldkemp.telegram.services.impl;

import keldkemp.telegram.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionManager transactionManager;


    @Transactional
    @Override
    public <V> V doInTransactionAnnotation(Callable<V> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }

    @Transactional
    @Override
    public void doInTransactionAnnotation(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("", e);
        }
    }
}
