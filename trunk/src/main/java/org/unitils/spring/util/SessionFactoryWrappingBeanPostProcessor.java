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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.unitils.hibernate.util.SessionInterceptingSessionFactory;
import org.unitils.core.UnitilsException;

import java.util.HashMap;
import java.util.Map;

/**
 * A <code>BeanPostProcessor</code> that checks wether beans are created in spring's <code>ApplicationContext</code>
 * of type <code>SessionFactory</code>. If such a bean is created, it is wrapped in a {@link SessionInterceptingSessionFactory},
 * to make sure all hibernate <code>Session</code>s that are created are intercepted and to be able to implement features
 * like flushing and closing these <code>Session</code>s.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SessionFactoryWrappingBeanPostProcessor implements BeanPostProcessor {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SessionFactoryWrappingBeanPostProcessor.class);

    /**
     * The wrapped SessionFactory that was intercepted by this BeanPostProcessor, if any
     */
    protected SessionInterceptingSessionFactory sessionFactory;

    /**
     * The hibernate Configuration that was intercepted by this BeanPostProcessor, if any
     */
    protected Configuration configuration;

    /**
     * The name of the last bean processed, that was either a SessionFactory or a Configuration
     */
    protected String processedBeanName;


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
     * Intercepts all <code>SessionFactory</code> and <code>LocalSessionFactoryBean</code> beans.
     * The session factories are wrapped in a {@link SessionInterceptingSessionFactory} instance. The factory beans
     * are intercepted to be able to get to the Hibernate configuration that was used to create the session factories.
     *
     * @param bean     The new bean instance
     * @param beanName The name of the bean
     * @return The given bean or a wrapped session factory if its a session factory
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // if it's a session factory bean, wrap the session factory and store it
        if (bean instanceof SessionFactory && !(bean instanceof SessionInterceptingSessionFactory)) {
            if (processedBeanName != null && !processedBeanName.equals(beanName)) {
                throw new UnitilsException("More than one SessionFactory is configured in the spring configuration. This " +
                        "is not supported in Unitils");
            }
            processedBeanName = beanName;

            sessionFactory = wrapSessionFactory((SessionFactory) bean);
            return sessionFactory;
        }
        // if it's a session factory factory bean, get and store configuration
        if (bean instanceof LocalSessionFactoryBean) {
            if (processedBeanName != null && !processedBeanName.equals(beanName)) {
                throw new UnitilsException("More than one SessionFactory is configured in the spring configuration. This " +
                        "is not supported in Unitils");
            }
            processedBeanName = beanName;
            configuration = ((LocalSessionFactoryBean) bean).getConfiguration();
        }
        return bean;
    }

    /**
     * @return The <code>SessionFactory</code> that was intercepted by this <code>BeanPostProcessor</code>, wrapped in a
     * {@link SessionFactoryWrappingBeanPostProcessor}, if any
     */
    public SessionInterceptingSessionFactory getInterceptedSessionFactory() {
        return sessionFactory;
    }

    /**
     * @return The <code>Configuration</code> that was intercepted by this <code>BeanPostProcessor</code>, if any
     */
    public Configuration getInterceptedHibernateConfiguration() {
        return configuration;
    }


    /**
     * Wraps the given <code>SessionFactory</code> in a {@link SessionInterceptingSessionFactory}
     *
     * @param sessionFactory The session factory to wrap, not null
     * @return A {@link SessionInterceptingSessionFactory} wrapping the given <code>SessionFactory</code>
     */
    protected SessionInterceptingSessionFactory wrapSessionFactory(SessionFactory sessionFactory) {
        return new SessionInterceptingSessionFactory(sessionFactory);
    }

}
