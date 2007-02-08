package org.unitils.spring.util;

import org.springframework.context.ApplicationContext;
import org.unitils.core.UnitilsException;
import org.unitils.util.ReflectionUtils;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringBeanProxyManager {

    private ApplicationContext applicationContext;

    private Map<SpringBeanProxy, Object> originalSpringBeans = new HashMap<SpringBeanProxy, Object>();

    public SpringBeanProxyManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void replaceSpringBeanByName(String springBeanName, Object replacingObject) {

        Object springBean = applicationContext.getBean(springBeanName);
        if (!(springBean instanceof SpringBeanProxy)) {
            throw new UnitilsException("No proxy has been created for Spring bean referenced by " +
                    springBeanName + ". Verify if the bean class has a none private parameterless constructor");
        }

        SpringBeanProxy springBeanProxy = (SpringBeanProxy) springBean;
        originalSpringBeans.put(springBeanProxy, springBeanProxy.getProxiedSpringBean());
        springBeanProxy.setProxiedSpringBean(replacingObject);
    }

    public void replaceSpringBeanByType(Class springBeanType, Object replacingObject) {

        Map<String,Object> beansOfType = applicationContext.getBeansOfType(springBeanType);
        if (beansOfType.size() == 0) {
             throw new UnitilsException("No beans of type " + springBeanType + " have been found in the ApplicationContext");
        }
        if (beansOfType.size() > 1) {
            throw new UnitilsException("Multiple beans of type " + springBeanType + " have been found in the ApplicationContext");
        }

        String springBeanName = beansOfType.keySet().iterator().next();
        Object springBean = beansOfType.get(springBeanName);
        if (!(springBean instanceof SpringBeanProxy)) {
            throw new UnitilsException("No proxy has been created for Spring bean referenced by " +
                    springBeanName + ". Verify if the bean class has a none private parameterless constructor");
        }

        SpringBeanProxy springBeanProxy = (SpringBeanProxy) springBean;
        originalSpringBeans.put(springBeanProxy, springBeanProxy.getProxiedSpringBean());
        springBeanProxy.setProxiedSpringBean(replacingObject);
    }
}
