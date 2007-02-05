package org.untils.sample.eshop;

import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.unitils.UnitilsJUnit3;
import org.unitils.hibernate.annotation.HibernateSessionFactory;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class BaseHibernateTest extends UnitilsJUnit3 {

    @HibernateSessionFactory
    private Configuration createHibernateConfiguration() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/eshop-config.xml");
        LocalSessionFactoryBean sessionFactoryBean = (LocalSessionFactoryBean) applicationContext.getBean("&sessionFactoryBean");
        return sessionFactoryBean.getConfiguration();
    }
}
