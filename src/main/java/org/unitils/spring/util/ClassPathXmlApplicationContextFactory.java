package org.unitils.spring.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.util.List;

/**
 * {@link ApplicationContextFactory} that creates an instance of the type <code>ClassPathXmlApplicationContext</code>.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ClassPathXmlApplicationContextFactory implements ApplicationContextFactory {

    /**
     * todo javadoc
     * @param locations
     * @return
     */
    public ConfigurableApplicationContext createApplicationContext(List<String> locations) {

        // create application context
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext((String[])locations.toArray(), false);

        return applicationContext;
    }
}
