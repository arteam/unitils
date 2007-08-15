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

import static java.util.Arrays.asList;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.AnnotatedInstanceManager;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * A class for managing and creating Spring application contexts.
 * <p/>
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ApplicationContextManager extends AnnotatedInstanceManager<ApplicationContext, SpringApplicationContext> {

    /**
     * Factory for creating ApplicationContexts
     */
    protected ApplicationContextFactory applicationContextFactory;

    /**
     * List of registered BeanPostProcessor types. For each ApplicationContext that is created, a BeanPostProcessor of
     * each of these types is associated with the ApplicationContext
     */
    protected List<Class<? extends BeanPostProcessor>> beanPostProcessorTypes;

    // todo javadoc
    protected Map<ApplicationContext, Map<Class<? extends BeanPostProcessor>, BeanPostProcessor>> beanPostProcessors;


    /**
     * Creates a new instance, using the given {@link ApplicationContextFactory}. The given list of
     * <code>BeanPostProcessor</code>s will be registered on all <code>ApplicationContext</code>s that are
     * created.
     *
     * @param applicationContextFactory The factory for creating <code>ApplicationContext</code>s, not null.
     */
    public ApplicationContextManager(ApplicationContextFactory applicationContextFactory) {
        super(ApplicationContext.class, SpringApplicationContext.class);
        this.applicationContextFactory = applicationContextFactory;
        this.beanPostProcessorTypes = new ArrayList<Class<? extends BeanPostProcessor>>();
        this.beanPostProcessors = new HashMap<ApplicationContext, Map<Class<? extends BeanPostProcessor>, BeanPostProcessor>>();
    }


    /**
     * Gets the application context for the given test as described in the class javadoc. A UnitilsException will be
     * thrown if no context could be retrieved or created.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    public ApplicationContext getApplicationContext(Object testObject) {
        ApplicationContext applicationContext = getInstance(testObject);
        if (applicationContext == null) {
            throw new UnitilsException("No configuration found for creating an instance for test " + testObject.getClass() + ". Make sure that you either specify a value " +
                    "for an @" + annotationClass.getSimpleName() + " annotation somewhere in the testclass or a superclass or that you specify a custom create method in the test class itself.");
        }
        return applicationContext;
    }


    /**
     * Checks whether the given test object has an application context linked to it. If true is returned,
     * {@link #getApplicationContext} will return an application context, If false is returned, it will raise
     * an exception.
     *
     * @param testObject The test instance, not null
     * @return True if an application context is linked
     */
    public boolean hasApplicationContext(Object testObject) {
        return hasInstance(testObject);
    }


    /**
     * Forces the reloading of the application context the next time that it is requested. If classes are given
     * only contexts that are linked to those classes will be reset. If no classes are given, all cached
     * contexts will be reset.
     *
     * @param classes The classes for which to reset the contexts
     */
    public void invalidateApplicationContext(Class<?>... classes) {
        invalidateInstance(classes);
    }


    // todo javadoc
    public void addBeanPostProcessorType(Class<? extends BeanPostProcessor> beanPostProcessorType) {
        beanPostProcessorTypes.add(beanPostProcessorType);
    }


    /**
     * Creates a new application context for the given locations. The application context factory is used to create
     * the instance. After creating the context, this will also register all <code>BeanPostProcessor</code>s and
     * refresh the context.
     * <p/>
     * Note: for this to work, the application context may not have been refreshed in the factory.
     * By registering the bean post processors before the refresh, we can intercept bean creation and bean wiring.
     * This is no longer possible if the context is already refreshed.
     *
     * @param locations The locations where to find configuration files, not null
     * @return the context, not null
     */
    @Override
    protected ApplicationContext createInstanceForValues(List<String> locations) {
        try {
            // create application context
            final ConfigurableApplicationContext applicationContext = applicationContextFactory.createApplicationContext(locations);

            // register post processors
            if (!beanPostProcessorTypes.isEmpty()) {
                applicationContext.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
                    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                        for (Class<? extends BeanPostProcessor> beanPostProcessorType : beanPostProcessorTypes) {
                            BeanPostProcessor beanPostProcessor = createInstanceOfType(beanPostProcessorType, false);
                            registerBeanPostProcessor(applicationContext, beanPostProcessorType, beanPostProcessor);
                            beanFactory.addBeanPostProcessor(beanPostProcessor);
                        }
                    }
                });
            }
            // load application context
            applicationContext.refresh();
            return applicationContext;

        } catch (Throwable t) {
            throw new UnitilsException("Unable to create application context for locations " + locations, t);
        }
    }


    // todo javadoc
    protected void registerBeanPostProcessor(ConfigurableApplicationContext applicationContext, Class<? extends BeanPostProcessor> beanPostProcessorType, BeanPostProcessor beanPostProcessor) {
        Map<Class<? extends BeanPostProcessor>, BeanPostProcessor> beanPostProcessorMap = beanPostProcessors.get(applicationContext);
        if (beanPostProcessorMap == null) {
            beanPostProcessorMap = new HashMap<Class<? extends BeanPostProcessor>, BeanPostProcessor>();
            beanPostProcessors.put(applicationContext, beanPostProcessorMap);
        }
        beanPostProcessorMap.put(beanPostProcessorType, beanPostProcessor);
    }


    // todo javadoc
    @SuppressWarnings("unchecked")
    public <T extends BeanPostProcessor> T getBeanPostProcessor(ApplicationContext applicationContext, Class<T> beanPostProcessorType) {
        Map<Class<? extends BeanPostProcessor>, ? extends BeanPostProcessor> beanPostProcessorMap = beanPostProcessors.get(applicationContext);
        if (beanPostProcessorMap == null) {
            return null;
        } else {
            return (T) beanPostProcessorMap.get(beanPostProcessorType);
        }
    }


    /**
     * Gets the locations that are specified for the given {@link SpringApplicationContext} annotation. If the
     * annotation is null or if no locations were specified, null is returned. An array with 1 empty string is
     * also be considered to be empty.
     *
     * @param annotation The annotation
     * @return The locations, null if no values were specified
     */
    @Override
    protected List<String> getAnnotationValues(SpringApplicationContext annotation) {
        if (annotation == null) {
            return null;
        }
        String[] locations = annotation.value();
        if (locations.length == 0 || (locations.length == 1 && StringUtils.isEmpty(locations[0]))) {
            return null;
        }
        return asList(locations);
    }
}
