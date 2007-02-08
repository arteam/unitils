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
package org.unitils.spring;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;
import org.unitils.spring.annotation.SpringBeanByName;
import org.unitils.spring.annotation.SpringBeanByType;
import org.unitils.spring.annotation.InjectIntoContext;
import org.unitils.spring.annotation.InjectIntoContextByType;
import org.unitils.spring.annotation.InjectIntoContextByName;
import org.unitils.spring.util.ApplicationContextManager;
import org.unitils.spring.util.ClassPathXmlApplicationContextFactory;
import org.unitils.spring.util.HibernateSessionFactoryWrappingBeanPostProcessor;
import org.unitils.spring.util.ProxyingBeanPostProcessor;
import org.unitils.spring.util.SpringBeanProxyManager;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * A module for Spring enabling a test class by offering an easy way to load application contexts and
 * an easy way of retrieving beans from the context and injecting them in the test.
 * <p/>
 * The application context loading can be achieved by using the {@link SpringApplicationContext} annotation. These
 * contexts are cached, so a context will be reused when possible. For example suppose a superclass loads a context and
 * a test-subclass wants to use this context, it will not create a new one. {@link #invalidateApplicationContext(Class[])} }
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

    /* Manager for storing and creating spring application contexts */
    private ApplicationContextManager applicationContextManager = initApplicationContextManager();

    /* Manager for spring beans who's proxy temporarily refers to another object */
    private SpringBeanProxyManager springBeanProxyManager;

    /**
     * Initializes this module using the given <code>Configuration</code> object
     *
     * @param configuration The configuration, not null
     */
    public void init(Configuration configuration) {
    }


    /**
     * todo!! make configurable
     *
     * Initializes the {@link ApplicationContextManager} that is used by the {@link SpringModule}
     * @return A ready-to-use {@link ApplicationContextManager}
     */
    private ApplicationContextManager initApplicationContextManager() {
        // todo!! make configurable which ApplicationContextFactory and which BeanPostProcessors are instantiated
        ApplicationContextManager applicationContextManager = new ApplicationContextManager(
                new ClassPathXmlApplicationContextFactory(),
                Arrays.asList(new HibernateSessionFactoryWrappingBeanPostProcessor(), new ProxyingBeanPostProcessor()));
        return applicationContextManager;
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
        Map beans = getApplicationContext(testObject).getBeansOfType(type);
        if (beans == null || beans.size() == 0) {
            throw new UnitilsException("Unable to get Spring bean by type. No Spring bean found for type " + type.getSimpleName());
        }
        if (beans.size() > 1) {
            throw new UnitilsException("Unable to get Spring bean by type. More than one possible Spring bean for type " + type.getSimpleName() + ". Possible beans; " + beans);
        }
        return beans.values().iterator().next();
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
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBean.class);
        for (Field field : fields) {
            try {
                SpringBean springBeanAnnotation = field.getAnnotation(SpringBean.class);
                setFieldValue(testObject, field, getSpringBean(testObject, springBeanAnnotation.value()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBean.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByType}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeansByType(Object testObject) {
        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByType.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBeanByType(testObject, field.getType()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByType.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByName}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeansByName(Object testObject) {

        List<Field> fields = getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByName.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBean(testObject, field.getName()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            }
        }
    }

    public void initSpringBeanProxyManager(Object testObject) {

        // todo check if ProxyingBeanPostProcessor is activated. If not, don't init springbeanproxymanager
        springBeanProxyManager = new SpringBeanProxyManager(getApplicationContext(testObject));
    }


    /**
     * todo javadoc
     * @param testObject
     */
    public void injectIntoContext(Object testObject) {

        // todo verify is ProxyingBeanPostProcessor is activated and throw an exception if not
        List<Field> annotatedFields = getFieldsAnnotatedWith(testObject.getClass(), InjectIntoContext.class);
        for (Field annotatedField : annotatedFields) {
            InjectIntoContext annotation = annotatedField.getAnnotation(InjectIntoContext.class);
            String springBeanName = annotation.value();
            Object annotatedObject = ReflectionUtils.getFieldValue(testObject, annotatedField);
            springBeanProxyManager.replaceSpringBeanByName(springBeanName, annotatedObject);
        }
        // todo inject result of method into context
    }

    /**
     * todo javadoc
     * @param testObject
     */
    public void injectIntoContextByType(Object testObject) {

        // todo verify is ProxyingBeanPostProcessor is activated and throw an exception if not
        List<Field> annotatedFields = getFieldsAnnotatedWith(testObject.getClass(), InjectIntoContextByType.class);
        for (Field annotatedField : annotatedFields) {
            InjectIntoContextByType annotation = annotatedField.getAnnotation(InjectIntoContextByType.class);
            Class springBeanType = annotatedField.getType();
            Object annotatedObject = ReflectionUtils.getFieldValue(testObject, annotatedField);
            springBeanProxyManager.replaceSpringBeanByType(springBeanType, annotatedObject);
        }
        // todo inject result of method into context
    }

    /**
     * todo javadoc
     * @param testObject
     */
    public void injectIntoContextByName(Object testObject) {

        // todo verify is ProxyingBeanPostProcessor is activated and throw an exception if not
        List<Field> annotatedFields = getFieldsAnnotatedWith(testObject.getClass(), InjectIntoContextByName.class);
        for (Field annotatedField : annotatedFields) {
            InjectIntoContextByName annotation = annotatedField.getAnnotation(InjectIntoContextByName.class);
            String springBeanName = annotatedField.getName();
            Object annotatedObject = ReflectionUtils.getFieldValue(testObject, annotatedField);
            springBeanProxyManager.replaceSpringBeanByName(springBeanName, annotatedObject);
        }
        // todo inject result of method into context
    }

    /**
     * @return The {@link TestListener} for this module
     */
    public TestListener createTestListener() {
        return new SpringTestListener();
    }


    /**
     * The {@link TestListener} for this module
     */
    private class SpringTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject) {
            injectApplicationContext(testObject);
            assignSpringBeans(testObject);
            assignSpringBeansByType(testObject);
            assignSpringBeansByName(testObject);
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            initSpringBeanProxyManager(testObject);
            injectIntoContext(testObject);
            injectIntoContextByType(testObject);
            injectIntoContextByName(testObject);
        }

    }

}
