/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.hibernate.util;

import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.unitils.core.Unitils;
import org.unitils.hibernate.HibernateModule;
import org.unitils.spring.SpringModule;

/**
 * A support class containing Hibernate and {@link HibernateModule} related actions for the spring module.
 * <p/>
 * By encapsulating these operations, we can remove the strong dependency to Hibernate and the HibernateModule from
 * the SpringModule. This way, the SpringModule will still function if Hibernate is not used.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateSpringSupportImpl implements HibernateSpringSupport {


    /**
     * Creates a new support instance.
     */
    public HibernateSpringSupportImpl() {
        // Make sure Spring is in the classpath
        LocalSessionFactoryBean.class.getName();

        // Register the BeanPostProcessor that intercepts SessionFactory creation
        getSpringModule().registerBeanPostProcessorType(SessionFactoryInterceptingBeanPostProcessor.class);
    }


    /**
     * Returns the hibernate <code>SessionFactory</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>SessionFactory</code> configured in spring for the given testObject, null if no such
     *         <code>SessionFactory</code> was configured.
     */
    public SessionInterceptingSessionFactory getSessionFactory(Object testObject) {
        SessionFactoryInterceptingBeanPostProcessor beanPostProcessor = getSessionFactoryWrappingBeanPostProcessor(testObject);
        if (beanPostProcessor == null) {
            return null;
        }
        return beanPostProcessor.getInterceptedSessionFactory();
    }


    /**
     * Returns the hibernate <code>Configuration</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>Configuration</code> configured in spring for the given testObject, null if no such
     *         <code>Configuration</code> was configured.
     */
    public Configuration getConfiguration(Object testObject) {
        SessionFactoryInterceptingBeanPostProcessor beanPostProcessor = getSessionFactoryWrappingBeanPostProcessor(testObject);
        if (beanPostProcessor == null) {
            return null;
        }
        return beanPostProcessor.getInterceptedHibernateConfiguration();
    }


    /**
     * Gets the registered SessionFactoryInterceptingBeanPostProcessor.
     *
     * @param testObject The test instance, not null
     * @return The session factory bean post processor, null if not found
     */
    protected SessionFactoryInterceptingBeanPostProcessor getSessionFactoryWrappingBeanPostProcessor(Object testObject) {
        BeanPostProcessor beanPostProcessor = getSpringModule().getBeanPostProcessor(testObject,
                SessionFactoryInterceptingBeanPostProcessor.class);
        return (SessionFactoryInterceptingBeanPostProcessor) beanPostProcessor;
    }


    /**
     * Ensures that the spring application context is loaded. This could be not the case since the application context
     * is lazily loaded
     *
     * @param testObject The test inst null not null
     */
    protected void ensureApplicationContextLoaded(Object testObject) {
        getSpringModule().getApplicationContext(testObject);
    }


    /**
     * Looks up the Spring module instance.
     *
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }
}
