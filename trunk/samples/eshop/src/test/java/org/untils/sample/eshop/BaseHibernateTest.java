package org.untils.sample.eshop;

import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit3;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@SpringApplicationContext("classpath:/eshop-config.xml")
public abstract class BaseHibernateTest extends UnitilsJUnit3 {

    @SpringApplicationContext
    ApplicationContext springApplicationContext;

}
