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

import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Wraps every bean instantiated by spring in a proxy of type {@link SpringBeanProxy}. By default this operation has no
 * effect since these proxies simply delegate to the original spring bean. However, this enables other parties to register
 * interceptors or even swith the spring bean with another object
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ProxyingBeanPostProcessor implements BeanPostProcessor {

    /**
     * Simply passes through all beans before they are initialized.
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Wraps the spring bean with a {@link SpringBeanProxy} before passing through.
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        SpringBeanProxy proxy = wrapInSpringBeanProxy(bean);
        return proxy;
    }

    /**
     * Wraps the given bean in a {@link SpringBeanProxy}
     * @param bean the bean that should be wrapped
     * @return The given bean wrapped in a {@link SpringBeanProxy}
     */
    private SpringBeanProxy wrapInSpringBeanProxy(Object bean) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(bean.getClass());
        enhancer.setCallback(new SpringBeanProxy(bean));
        return (SpringBeanProxy) enhancer.create();
    }

}
