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
package org.unitils.orm.jpa.util.spring;

import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.unitils.core.Unitils;
import org.unitils.orm.common.spring.OrmSpringSupport;
import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;
import org.unitils.orm.jpa.JpaModule;
import org.unitils.spring.SpringModule;

import javax.persistence.EntityManagerFactory;
import java.util.Collection;


/**
 * Implementation of {@link OrmSpringSupport} for JPA. Enables retrieving a JPA <code>EntityManagerFactory</code>
 * that was configured in a spring <code>ApplicationContext</code>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaSpringSupport implements OrmSpringSupport<EntityManagerFactory, Object> {


    public boolean isPersistenceUnitConfiguredInSpring(Object testObject) {
        return getEntityManagerFactoryBean(testObject) != null;
    }


    public ConfiguredOrmPersistenceUnit<EntityManagerFactory, Object> getConfiguredPersistenceUnit(Object testObject) {
        AbstractEntityManagerFactoryBean factoryBean = getEntityManagerFactoryBean(testObject);

        EntityManagerFactory entityManagerFactory = factoryBean.getObject();
        Object providerSpecificConfigurationObject = getJpaModule().getJpaProviderSupport().getProviderSpecificConfigurationObject(factoryBean.getPersistenceProvider());
        return new ConfiguredOrmPersistenceUnit<EntityManagerFactory, Object>(entityManagerFactory, providerSpecificConfigurationObject);
    }


    /**
     * @param testObject The test instance, not null
     * @return Instance of {@link LocalSessionFactoryBean} that wraps the configuration of hibernate in spring
     */
    protected AbstractEntityManagerFactoryBean getEntityManagerFactoryBean(Object testObject) {
        if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return null;
        }
        Collection<?> entityManagerFactoryBeans = getSpringModule().getApplicationContext(testObject).getBeansOfType(AbstractEntityManagerFactoryBean.class).values();
        if (entityManagerFactoryBeans.size() == 0) {
            return null;
        }
        return (AbstractEntityManagerFactoryBean) entityManagerFactoryBeans.iterator().next();
    }


    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }


    protected JpaModule getJpaModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(JpaModule.class);
    }
}
