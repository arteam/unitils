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
package org.unitils.spring;

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.PropertyUtils.getInstance;
import static org.unitils.util.ReflectionUtils.getFieldName;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.isSetter;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.impl.SpringResourceTransactionManagerTransactionalConnectionHandler;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.spring.annotation.SpringBeanByName;
import org.unitils.spring.annotation.SpringBeanByType;
import org.unitils.spring.util.ApplicationContextFactory;
import org.unitils.spring.util.ApplicationContextManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * A module for Spring enabling a test class by offering an easy way to load application contexts and
 * an easy way of retrieving beans from the context and injecting them in the test.
 * <p/>
 * The application context loading can be achieved by using the {@link SpringApplicationContext} annotation. These
 * contexts are cached, so a context will be reused when possible. For example suppose a superclass loads a context and
 * a test-subclass wants to use this context, it will not create a new one. {@link #invalidateApplicationContext} }
 * can be used to force a reloading of a context if needed.
 * <p/>
 * Spring bean retrieval can be done by annotating the corresponding fields in the test with following
 * annotations: {@link SpringBean}, {@link SpringBeanByName} and {@link SpringBeanByType}.
 * <p/>
 * See the javadoc of these annotations for more info on how you can use them.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModule implements Module {

    /* Property key of the class name of the application context factory */
    public static final String PROPKEY_APPLICATION_CONTEXT_FACTORY_CLASS_NAME = "SpringModule.applicationContextFactory.implClassName";

    /* Manager for storing and creating spring application contexts */
    private ApplicationContextManager applicationContextManager;
    
    private Set<SpringResourceTransactionManagerTransactionalConnectionHandler> transactionalConnectionHandlers = new HashSet<SpringResourceTransactionManagerTransactionalConnectionHandler>();


    /**
     * Initializes this module using the given configuration
     *
     * @param configuration The configuration, not null
     */
    public void init(Properties configuration) {
        // create application context manager that stores and creates the application contexts
        ApplicationContextFactory applicationContextFactory = getInstance(PROPKEY_APPLICATION_CONTEXT_FACTORY_CLASS_NAME, configuration);
        applicationContextManager = new ApplicationContextManager(applicationContextFactory);
    }


    /**
     * Gets the spring bean with the given name. The given test instance, by using {@link SpringApplicationContext},
     * determines the application context in which to look for the bean.
     * <p/>
     * A UnitilsException is thrown when the no bean could be found for the given name.
     *
     * @param testObject The test instance, not null
     * @param name       The name, not null
     * @return The bean, not null
     */
    public Object getSpringBean(Object testObject, String name) {
        try {
            return getApplicationContext(testObject).getBean(name);

        } catch (BeansException e) {
            throw new UnitilsException("Unable to get Spring bean. No Spring bean found for name " + name);
        }
    }


    /**
     * Gets the spring bean with the given type. The given test instance, by using {@link SpringApplicationContext},
     * determines the application context in which to look for the bean.
     * If more there is not exactly 1 possible bean assignment, an UnitilsException will be thrown.
     *
     * @param testObject The test instance, not null
     * @param type       The type, not null
     * @return The bean, not null
     */
    public Object getSpringBeanByType(Object testObject, Class<?> type) {
        Map<?,?> beans = getApplicationContext(testObject).getBeansOfType(type);
        if (beans == null || beans.size() == 0) {
            throw new UnitilsException("Unable to get Spring bean by type. No Spring bean found for type " + type.getSimpleName());
        }
        if (beans.size() > 1) {
            throw new UnitilsException("Unable to get Spring bean by type. More than one possible Spring bean for type " + type.getSimpleName() + ". Possible beans; " + beans);
        }
        return beans.values().iterator().next();
    }

    /**
     * @param testObject The test object
     * @return Whether an ApplicationContext has been configured for the given testObject
     */
    public boolean isApplicationContextConfiguredFor(Object testObject) {
        return applicationContextManager.hasApplicationContext(testObject);
    }


    /**
     * Gets the application context for this test. A new one will be created if it does not exist yet. If a superclass
     * has also declared the creation of an application context, this one will be retrieved (or created if it was not
     * created yet) and used as parent context for this classes context.
     * <p/>
     * If needed, an application context will be created using the settings of the {@link SpringApplicationContext}
     * annotation.
     * <p/>
     * If a class level {@link SpringApplicationContext} annotation is found, the passed locations will be loaded using
     * a <code>ClassPathXmlApplicationContext</code>.
     * Custom creation methods can be created by annotating them with {@link SpringApplicationContext}. They
     * should have an <code>ApplicationContext</code> as return type and either no or exactly 1 argument of type
     * <code>ApplicationContext</code>. In the latter case, the current configured application context is passed as the argument.
     * <p/>
     * A UnitilsException will be thrown if no context could be retrieved or created.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    public ApplicationContext getApplicationContext(Object testObject) {
        return applicationContextManager.getApplicationContext(testObject);
    }


    /**
     * Forces the reloading of the application context the next time that it is requested. If classes are given
     * only contexts that are linked to those classes will be reset. If no classes are given, all cached
     * contexts will be reset.
     *
     * @param classes The classes for which to reset the contexts
     */
    public void invalidateApplicationContext(Class<?>... classes) {
        applicationContextManager.invalidateApplicationContext(classes);
    }


    /**
     * Gets the application context for this class and sets it on the fields and setter methods that are
     * annotated with {@link SpringApplicationContext}. If no application context could be created, an
     * UnitilsException will be raised.
     *
     * @param testObject The test instance, not null
     */
    public void injectApplicationContext(Object testObject) {
        // inject into fields annotated with @SpringApplicationContext
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringApplicationContext.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getApplicationContext(testObject));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the application context to field annotated with @" + SpringApplicationContext.class.getSimpleName(), e);
            }
        }

        // inject into setter methods annotated with @SpringApplicationContext
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), SpringApplicationContext.class, false);
        for (Method method : methods) {
            // ignore custom create methods
            if (method.getReturnType() != Void.TYPE) {
                continue;
            }
            try {
                invokeMethod(testObject, method, getApplicationContext(testObject));

            } catch (Exception e) {
                throw new UnitilsException("Unable to assign the application context to setter annotated with @" + SpringApplicationContext.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBean}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeans(Object testObject) {
        // assign to fields
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBean.class);
        for (Field field : fields) {
            try {
                SpringBean springBeanAnnotation = field.getAnnotation(SpringBean.class);
                setFieldValue(testObject, field, getSpringBean(testObject, springBeanAnnotation.value()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBean.class.getSimpleName(), e);
            }
        }

        // assign to setters
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), SpringBean.class);
        for (Method method : methods) {
            try {
                if (!isSetter(method)) {
                    throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBean.class.getSimpleName() + ". Method " +
                            method.getName() + " is not a setter method.");
                }
                SpringBean springBeanAnnotation = method.getAnnotation(SpringBean.class);
                invokeMethod(testObject, method, getSpringBean(testObject, springBeanAnnotation.value()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBean.class.getSimpleName(), e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBean.class.getSimpleName() + ". Method " +
                        "has thrown an exception.", e.getCause());
            }
        }
    }


    /**
     * Gets the spring beans for all fields methods that are annotated with {@link SpringBeanByType}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeansByType(Object testObject) {
        // assign to fields
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByType.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBeanByType(testObject, field.getType()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByType.class.getSimpleName(), e);
            }
        }

        // assign to setters
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), SpringBeanByType.class);
        for (Method method : methods) {
            try {
                if (!isSetter(method)) {
                    throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBeanByType.class.getSimpleName() + ". Method " +
                            method.getName() + " is not a setter method.");
                }
                invokeMethod(testObject, method, getSpringBeanByType(testObject, method.getParameterTypes()[0]));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBeanByType.class.getSimpleName(), e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBeanByType.class.getSimpleName() + ". Method " +
                        "has thrown an exception.", e.getCause());
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByName}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeansByName(Object testObject) {
        // assign to fields
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByName.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBean(testObject, field.getName()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            }
        }

        // assign to setters
        List<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), SpringBeanByName.class);
        for (Method method : methods) {
            try {
                if (!isSetter(method)) {
                    throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBeanByName.class.getSimpleName() + ". Method " +
                            method.getName() + " is not a setter method.");
                }
                invokeMethod(testObject, method, getSpringBean(testObject, getFieldName(method)));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to method annotated with @" + SpringBeanByName.class.getSimpleName() + ". Method " +
                        "has thrown an exception.", e.getCause());
            }
        }
    }


    /**
     * Register a type of bean post processor. An instance of this bean post processor type will be used
     * when loading an application context
     * 
     * @param beanPostProcessorType
     */
    public void registerBeanPostProcessorType(Class<? extends BeanPostProcessor> beanPostProcessorType) {
        applicationContextManager.addBeanPostProcessorType(beanPostProcessorType);
    }


    /**
     * @param <T> The bean post processor type
     * @param testObject The test object
     * @param beanPostProcessorType Type bean post processor type
     * 
     * @return The bean post processor of the given type that is associated with the application context that is
     * in turn associated with the given test object
     */
    public <T extends BeanPostProcessor> T getBeanPostProcessor(Object testObject, Class<T> beanPostProcessorType) {
        if (applicationContextManager.hasApplicationContext(testObject)) {
            return applicationContextManager.getBeanPostProcessor(getApplicationContext(testObject), beanPostProcessorType);
        } else {
            return null;
        }
    }
    
    
    /**
     * Registers a {@link SpringResourceTransactionManagerTransactionalConnectionHandler} that defines how to retrieve a
     * <code>Connection</code>, given the fact that we use a specific resource factory type.
     * 
     * @param transactionalConnectionHandler
     */
    public void registerSpringResourceTransactionManagerTransactionalConnectionHandler(SpringResourceTransactionManagerTransactionalConnectionHandler transactionalConnectionHandler) {
    	transactionalConnectionHandlers.add(transactionalConnectionHandler);
    }


    /**
     * @return All {@link SpringResourceTransactionManagerTransactionalConnectionHandler}s that were registered using
     * {@link #registerBeanPostProcessorType(Class)}
     */
    public Set<SpringResourceTransactionManagerTransactionalConnectionHandler> getTransactionalConnectionHandlers() {
		return transactionalConnectionHandlers;
	}


	/**
     * @return The {@link TestListener} for this module
     */
    public TestListener getTestListener() {
        return new SpringTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    protected class SpringTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectApplicationContext(testObject);
            assignSpringBeans(testObject);
            assignSpringBeansByType(testObject);
            assignSpringBeansByName(testObject);
        }
    }
}
