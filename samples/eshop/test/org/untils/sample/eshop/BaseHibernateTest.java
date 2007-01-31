package org.untils.sample.eshop;

import org.unitils.UnitilsJUnit3;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class BaseHibernateTest extends UnitilsJUnit3 {

    @HibernateConfiguration
    private Configuration createHibernateConfiguration() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/eshop-config.xml");
        LocalSessionFactoryBean sessionFactoryBean = (LocalSessionFactoryBean)
                applicationContext.getBean("&sessionFactoryBean");
        return sessionFactoryBean.getConfiguration();
    }
}
