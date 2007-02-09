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
package org.unitils.spring.util;

import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.unitils.core.Unitils;
import org.unitils.hibernate.HibernateModule;
import org.unitils.hibernate.util.SessionFactoryManager;
import org.unitils.hibernate.util.SessionInterceptingSessionFactory;

import java.util.Map;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateSupportImpl implements HibernateSupport {


    private HibernateSessionFactoryWrappingBeanPostProcessor hibernateSessionFactoryWrappingBeanPostProcessor;


    public HibernateSupportImpl() {
        hibernateSessionFactoryWrappingBeanPostProcessor = new HibernateSessionFactoryWrappingBeanPostProcessor();
    }


    public void registerHibernateSessionFactories(Object testObject) {
        Class<?> testClass = testObject.getClass();
        SessionFactoryManager sessionFactoryManager = getSessionFactoryManager();

        // register all session factories
        Map<SessionInterceptingSessionFactory, Configuration> sessionFactories = hibernateSessionFactoryWrappingBeanPostProcessor.getSessionFactories();
        for (Map.Entry<SessionInterceptingSessionFactory, Configuration> sessionFactory : sessionFactories.entrySet()) {
            sessionFactoryManager.registerSessionFactory(testClass, sessionFactory.getKey(), sessionFactory.getValue());
        }
    }


    public void unregisterHibernateSessionFactories(Object testObject) {
        Class<?> testClass = testObject.getClass();
        getSessionFactoryManager().invalidateSessionFactory(testClass);
    }


    public BeanPostProcessor getSessionFactoryBeanPostProcessor() {
        return hibernateSessionFactoryWrappingBeanPostProcessor;
    }


    protected SessionFactoryManager getSessionFactoryManager() {
        HibernateModule hibernateModule = Unitils.getInstance().getModulesRepository().getModuleOfType(HibernateModule.class);
        return hibernateModule.getSessionFactoryManager();
    }
}
