package org.untils.sample.eshop;

import org.unitils.spring.annotation.SpringApplicationContext;
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
@SpringApplicationContext("classpath:/eshop-config.xml")
public class BaseHibernateTest extends UnitilsJUnit3 {

    @SpringApplicationContext
    ApplicationContext springApplicationContext;

}
