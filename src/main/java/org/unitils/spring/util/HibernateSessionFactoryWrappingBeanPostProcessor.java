/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitils.spring.util;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.unitils.core.Unitils;
import org.unitils.hibernate.HibernateModule;
import org.unitils.hibernate.util.SessionInterceptingSessionFactory;

/**
 * todo watch out: loading this class causes a link of the SpringModule with the HibernateModule and Hibernate
 * <p/>
 * <code>BeanPostProcessor</code> that checks wether beans are created in spring's <code>ApplicationContext</code>
 * of type <code>SessionFactory</code>. If such a bean is created, it is wrapped in a {@link SessionInterceptingSessionFactory},
 * to make sure all hibernate <code>Session</code>s that are created are intercepted, to be able to implement features
 * like flushing and closing these <code>Session</code>s.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class HibernateSessionFactoryWrappingBeanPostProcessor implements BeanPostProcessor {


    /**
     * Simply passes through all beans before they are initialized.
     *
     * @param bean     The new bean instance
     * @param beanName The name of the bean
     * @return The given bean
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    /**
     * Simply passes through all beans, except for objects of type <code>SessionFactory</code>. Such objects are wrapped
     * in a {@link SessionInterceptingSessionFactory}
     *
     * @param bean     The new bean instance
     * @param beanName The name of the bean
     * @return The given bean or a wrapped session factory if its a session factory
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // if its a session factory bean, wrap the session factory
        if (bean instanceof SessionFactory) {
            SessionInterceptingSessionFactory wrappedSessionFactory = wrapHibernateSessionFactory((SessionFactory) bean);
            registerSessionFactoryWithHibernateModule(wrappedSessionFactory);
            return wrappedSessionFactory;
        }
        return bean;
    }


    /**
     * Wraps the given <code>SessionFactory</code> in a {@link SessionInterceptingSessionFactory}
     *
     * @param sessionFactory The session factory to wrap, not null
     * @return A {@link SessionInterceptingSessionFactory} wrapping the given <code>SessionFactory</code>
     */
    protected SessionInterceptingSessionFactory wrapHibernateSessionFactory(SessionFactory sessionFactory) {
        return new SessionInterceptingSessionFactory(sessionFactory);
    }


    /**
     * Registers the given session factory in the Hibernate module.
     *
     * @param sessionFactory The session factory, not null
     */
    protected void registerSessionFactoryWithHibernateModule(SessionInterceptingSessionFactory sessionFactory) {
        HibernateModule hibernateModule = Unitils.getInstance().getModulesRepository().getModuleOfType(HibernateModule.class);
        hibernateModule.getSessionFactoryManager().registerSessionFactory(sessionFactory, null);
    }
}
