package keldkemp.telegram.services;

public interface BeanFactoryService {

    String LOCK_NAME = "BEAN_FACTORY_DEFAULT_LOCK";

    /**
     * Get bean by name and cast to tClass.
     * @param name bean name
     * @param tClass return class type
     * @return bean cast to tClass
     */
    <T> T getBean(String name, Class<T> tClass);

    /**
     * Get bean by name and cast to Object.
     * @param name bean name
     * @return bean cast to Object
     */
    <T> T getBean(String name);

    /**
     * Remove bean.
     * @param name bean name
     */
    void deleteBean(String name);

    /**
     * Remove bean by object
     * @param object object
     */
    void deleteBean(Object object);

    /**
     * Check bean exist
     * @param name bean name
     */
    boolean checkBean(String name);
}
