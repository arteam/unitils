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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.*;
import org.unitils.util.AnnotationUtils;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitils.util.ReflectionUtils.setFieldValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A module for Spring enabling a test class by offering an easy way to load application contexts and
 * an easy way of retrieving beans from the context and injecting them in the test.
 * <p/>
 * The application context loading can be achieved by using the {@link SpringApplicationContext} and
 * {@link CreateSpringApplicationContext} annotations.
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

    /* All created Spring application contexts per class */
    private Map<Class<?>, ApplicationContext> applicationContexts = new HashMap<Class<?>, ApplicationContext>();


    /**
     * Initializes this module using the given <code>Configuration</code> object
     *
     * @param configuration The configuration, not null
     */
    public void init(Configuration configuration) {
    }


    /**
     * Gets the spring bean with the given name. The given test instance, by using {@link SpringApplicationContext} and
     * {@link CreateSpringApplicationContext}, determines the application context in which to look for the bean.
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
     * Gets the spring bean with the given type. The given test instance, by using {@link SpringApplicationContext} and
     * {@link CreateSpringApplicationContext}, determines the application context in which to look for the bean.
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
     * If needed, an application context will be created using the settings of the {@link SpringApplicationContext} and
     * {@link CreateSpringApplicationContext} annotations.
     * <p/>
     * If a class level {@link SpringApplicationContext} annotation is found, the passed locations will be loaded using
     * a <code>ClassPathXmlApplicationContext</code>.
     * Custom creation methods can be created by annotating them with {@link CreateSpringApplicationContext}. They
     * should have an <code>ApplicationContext</code> as return type and either no or exactly 1 argument of type
     * <code>ApplicationContext</code>. In the latter case, the current configured application context is passed as the argument.
     * <p/>
     * A UnitilsException will be thrown if no context could be retrieved or created.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    @SuppressWarnings({"unchecked"})
    public ApplicationContext getApplicationContext(Object testObject) {
        // get or create context
        ApplicationContext applicationContext = getApplicationContext(testObject, testObject.getClass());

        // check no application context was created
        if (applicationContext == null) {
            throw new UnitilsException("No application context was created. Make sure the context can be created by annotating the class with a @" + SpringApplicationContext.class.getSimpleName() +
                    " annotation or creating a method that is annotated with a @" + CreateSpringApplicationContext.class.getSimpleName() + " annotation that creates the ApplicationContext intstance.");
        }
        return applicationContext;
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBean}.
     *
     * @param testObject The test instance, not null
     */
    public void assignSpringBeans(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), SpringBean.class);
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
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByType.class);
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

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByName.class);
        for (Field field : fields) {
            try {
                setFieldValue(testObject, field, getSpringBean(testObject, field.getName()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets or creates the application context for the given class in the class hierarchy of the given test object.
     * This will first (recursively) get the context for the super class of the test class. This context will
     * then be passed as the parentApplicationContext for the application contexts created for this class.
     * <p/>
     * An application context for a certain class type, will only be created once. The next time a context is requested
     * for this class type, the same context will be returned.
     * <p/>
     * If a context needs to be created, first the class level @SpringApplicationContext, if there is one, is used for
     * creating a context. Next the custom create methods (annotated with @CreateSpringApplicationContext) are used
     * for creating a context.
     *
     * @param testObject The test instance, not null
     * @param testClass  The current class in the hierarchy, can be null
     * @return the new application context, null if none was found or created
     */
    protected ApplicationContext getApplicationContext(Object testObject, Class<?> testClass) {

        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return null;
        }

        // check whether it already exists
        ApplicationContext applicationContext = applicationContexts.get(testClass);
        if (applicationContext != null) {
            return applicationContext;
        }

        // find parent application context recursively
        applicationContext = getApplicationContext(testObject, testClass.getSuperclass());

        // create application context for class level @SpringApplicationContext
        applicationContext = createApplicationContext(testClass, applicationContext);

        // call methods annotated with @CreateSpringApplicationContext passing current application context if requested
        applicationContext = createApplicationContext(testObject, testClass, applicationContext);

        // store application context
        if (applicationContext != null) {
            applicationContexts.put(testClass, applicationContext);
        }
        return applicationContext;
    }


    /**
     * Creates an application context by looking at the declared @SpringApplicationContext for the given class.
     * If no such annotation is found for the given class, the parentApplicationContext is returned.
     * If it is found, the passed locations are used to create an <code>ClassPathXmlApplicationContext</code> and
     * the parentApplicationContext is set as parent.
     *
     * @param testClass                The current class in the hierarchy, not null
     * @param parentApplicationContext The current application context
     * @return the new application context or the parentApplicationContext (can be null)
     */
    protected ApplicationContext createApplicationContext(Class<?> testClass, ApplicationContext parentApplicationContext) {

        SpringApplicationContext springApplicationContextAnnotation = testClass.getAnnotation(SpringApplicationContext.class);
        if (springApplicationContextAnnotation == null) {
            return parentApplicationContext;
        }

        String[] locations = springApplicationContextAnnotation.value();
        try {
            // create application context
            return new ClassPathXmlApplicationContext(locations, parentApplicationContext);

        } catch (Throwable t) {
            throw new UnitilsException("Unable to create application context for locations " + locations);
        }
    }


    /**
     * Creates an application context by calling all methods declared with @CreateSpringApplicationContext.
     * These methods should have one of following exact signatures:
     * <ul>
     * <li>ApplicationContext createMethodName() or</li>
     * <li>ApplicationContext createMethodName(ApplicationContext applicationContext)</li>
     * </ul>
     * The second version receives the current created application context, for example one that was created by a class or
     * superclasses @SpringApplicationContext).
     * They both should return a new application context (possibly setting the passed application context as parent).
     * <p/>
     * This method will only look at the creation methods declared in the given testClass.
     *
     * @param testObject               The test instance, not null
     * @param testClass                The current class in the hierarchy, not null
     * @param parentApplicationContext The current application context
     * @return the new application context or the parentApplicationContext (can be null)
     */
    protected ApplicationContext createApplicationContext(Object testObject, Class<?> testClass, ApplicationContext parentApplicationContext) {
        // start with parent context as current application context
        ApplicationContext applicationContext = parentApplicationContext;

        // call all @CreateSpringApplicationContext methods passing current application context if requested
        List<Method> methods = getMethodsAnnotatedWith(testClass, CreateSpringApplicationContext.class, false);
        for (Method method : methods) {
            Class<?>[] argumentTypes = method.getParameterTypes();
            if (argumentTypes.length > 1 || (argumentTypes.length == 1 && argumentTypes[0] != ApplicationContext.class) || method.getReturnType() != ApplicationContext.class) {
                throw new UnitilsException("Unable to invoke method annotated with @" + CreateSpringApplicationContext.class.getSimpleName() +
                        ". Ensure that this method has following signature: ApplicationContext myMethod(" + ApplicationContext.class.getName() + " context) or ApplicationContext myMethod()");
            }
            try {
                if (argumentTypes.length == 0) {
                    applicationContext = invokeMethod(testObject, method);
                } else {
                    applicationContext = invokeMethod(testObject, method, applicationContext);
                }
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testClass.getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + CreateSpringApplicationContext.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }
        return applicationContext;
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
            assignSpringBeans(testObject);
            assignSpringBeansByType(testObject);
            assignSpringBeansByName(testObject);
        }


    }

}
