package keldkemp.telegram.services.impl;

import keldkemp.telegram.services.BeanFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public abstract class BeanFactoryServiceImpl implements BeanFactoryService {

    @Autowired
    protected ConfigurableApplicationContext applicationContext;

    @Override
    public <T> T getBean(String name, Class<T> tClass) {
        return applicationContext.getBean(name, tClass);
    }

    @Override
    public Object getBean(String name) {
        return getBean(name, Object.class);
    }

    @Override
    public void deleteBean(String name) {
        ((DefaultListableBeanFactory) applicationContext.getBeanFactory()).destroySingleton(name);
    }

    @Override
    public boolean checkBean(String name) {
        return applicationContext.containsBean(name);
    }

    protected abstract void createBean(String name);

    public abstract void deleteBean(Object object);
}
