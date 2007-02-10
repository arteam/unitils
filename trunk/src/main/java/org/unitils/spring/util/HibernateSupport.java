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

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * A support class containing Hibernate related actions for the spring module.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface HibernateSupport {


    /**
     * Registers all intercepted session factories for the given test.
     *
     * @param testObject The test instance, not null
     */
    void registerHibernateSessionFactories(Object testObject);


    /**
     * Unregisters all intercepted session factories for the given test.
     *
     * @param testObject The test instance, not null
     */
    void unregisterHibernateSessionFactories(Object testObject);


    /**
     * Gets the bean post processor that will intercept the session factory creation.
     *
     * @return The post processor, not null
     */
    BeanPostProcessor getSessionFactoryBeanPostProcessor();

}
