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
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SpringModule implements Module {

    //todo cache context
    /* The Spring application context */
    private ApplicationContext applicationContext;


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
     * Gets the application context. If there is no context yet, a new one will be created using the settings of
     * the {@link SpringApplicationContext} and {@link CreateSpringApplicationContext} annotations.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    public ApplicationContext getApplicationContext(Object testObject) {
        if (applicationContext == null) {
            applicationContext = createApplicationContext(testObject);
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
                ReflectionUtils.setFieldValue(testObject, field, getSpringBean(testObject, springBeanAnnotation.value()));

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
                ReflectionUtils.setFieldValue(testObject, field, getSpringBeanByType(testObject, field.getType()));

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
                ReflectionUtils.setFieldValue(testObject, field, getSpringBean(testObject, field.getName()));

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Creates an application context using the settings of the {@link SpringApplicationContext} and
     * {@link CreateSpringApplicationContext} annotations.
     * <p/>
     * If a class level {@link SpringApplicationContext} annotation is found, the passed locations will be loaded using
     * a <code>ClassPathXmlApplicationContext</code>.
     * Custom creation methods can be created by annotating them with {@link CreateSpringApplicationContext}. They
     * should have an <code>ApplicationContext</code> as return type and either no or exactly 1 argument of type
     * <code>ApplicationContext</code>. In the latter case, the current configured application context is passed as the argument.
     * <p/>
     * A UnitilsException will be thrown if no context could be created.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    protected ApplicationContext createApplicationContext(Object testObject) {
        ApplicationContext result = null;

        // create application context for class level @SpringApplicationContext
        SpringApplicationContext springApplicationContextAnnotation = testObject.getClass().getAnnotation(SpringApplicationContext.class);
        if (springApplicationContextAnnotation != null) {
            String[] locations = springApplicationContextAnnotation.value();
            try {
                // create application context
                result = new ClassPathXmlApplicationContext(locations);

            } catch (Throwable t) {
                throw new UnitilsException("Unable to create application context for locations " + locations);
            }
        }

        // call methods annotated with @CreateSpringApplicationContext passing current application context if requested
        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), CreateSpringApplicationContext.class);
        for (Method method : methods) {
            Class<?>[] argumentTypes = method.getParameterTypes();
            if (argumentTypes.length > 1 || (argumentTypes.length == 1 && argumentTypes[0] != ApplicationContext.class) || method.getReturnType() != ApplicationContext.class) {
                throw new UnitilsException("Unable to invoke method annotated with @" + CreateSpringApplicationContext.class.getSimpleName() +
                        ". Ensure that this method has following signature: ApplicationContext myMethod(" + ApplicationContext.class.getName() + " context) or ApplicationContext myMethod()");
            }
            try {
                if (argumentTypes.length == 0) {
                    result = ReflectionUtils.invokeMethod(testObject, method);
                } else {
                    result = ReflectionUtils.invokeMethod(testObject, method, result);
                }
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + CreateSpringApplicationContext.class.getSimpleName() + ") has thrown an exception", e.getCause());
            }
        }

        // no application context was created
        if (result == null) {
            throw new UnitilsException("No application context was created. Make sure the context can be created by annotating the class with a @" + SpringApplicationContext.class.getSimpleName() +
                    " annotation or creating a method that is annotated with a @" + CreateSpringApplicationContext.class.getSimpleName() + " annotation that creates the ApplicationContext intstance.");
        }
        return result;
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
            createApplicationContext(testObject);
            assignSpringBeans(testObject);
            assignSpringBeansByType(testObject);
            assignSpringBeansByName(testObject);
        }


    }

}
