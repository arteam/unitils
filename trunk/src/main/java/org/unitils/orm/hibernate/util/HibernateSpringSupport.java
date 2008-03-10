/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.orm.hibernate.util;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.unitils.core.Unitils;
import org.unitils.orm.common.spring.OrmSpringSupport;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;
import org.unitils.orm.hibernate.HibernateModule;
import org.unitils.spring.SpringModule;

/**
 * A support class containing Hibernate and {@link HibernateModule} related actions for the {@link SpringModule}.
 * <p/>
 * This support class is only loaded if both the {@link HibernateModule} and {@link SpringModule} are loaded.
 * By encapsulating these operations, we remove the strong dependency to spring and the {@link SpringModule} from
 * the {@link HibernateModule}. This way, the {@link HibernateModule} will still function if spring is not used.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateSpringSupport implements OrmSpringSupport<SessionFactory, Configuration> {


	public boolean isPersistenceUnitConfiguredInSpring(Object testObject) {
		return getSessionFactoryBean(testObject) != null;
	}
	
	
	@Override
	public ConfiguredOrmPersistenceUnit<SessionFactory, Configuration> getConfiguredPersistenceUnit(Object testObject) {
		LocalSessionFactoryBean factoryBean = getSessionFactoryBean(testObject);
		SessionFactory entityManagerFactory = (SessionFactory) factoryBean.getObject();
		Configuration hibernateConfiguration = factoryBean.getConfiguration();
		
		return new ConfiguredOrmPersistenceUnit<SessionFactory, Configuration>(entityManagerFactory, hibernateConfiguration);
	}

    
    /**
     * @param testObject
     * @return Instance of {@link LocalSessionFactoryBean} that wraps the configuration of hibernate in spring
     */
    protected LocalSessionFactoryBean getSessionFactoryBean(Object testObject) {
        if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return null;
        }
        Collection<?> entityManagerFactoryBeans = getSpringModule().getApplicationContext(testObject).getBeansOfType(
        		LocalSessionFactoryBean.class).values();
        if (entityManagerFactoryBeans.size() == 0) {
            return null;
        }
        return (LocalSessionFactoryBean) entityManagerFactoryBeans.iterator().next();
    }
    

    /**
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }
    
}
