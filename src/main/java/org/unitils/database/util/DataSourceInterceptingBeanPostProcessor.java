package org.unitils.database.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.unitils.database.UnitilsDataSource;


public class DataSourceInterceptingBeanPostProcessor implements BeanPostProcessor {

    /**
     * Simply passes through all beans before they are initialized.
     *
     * @param bean     The new bean instance
     * @param beanName The name of the bean
     * @return The given bean
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        
        if (bean instanceof UnitilsDataSource) {
            return ((UnitilsDataSource) bean).getTargetDataSource();
        }
        return bean;
    }


}
