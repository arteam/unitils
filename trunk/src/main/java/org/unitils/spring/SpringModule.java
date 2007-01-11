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
     *
     * @param testObject The test instance, not null
     * @param name       The name, not null
     * @return The bean, a <code>NoSuchBeanDefinitionException</code> if not found
     */
    public Object getSpringBean(Object testObject, String name) throws BeansException {
        return getApplicationContext(testObject).getBean(name);
    }


    /**
     * Gets the spring bean with the given type. The given test instance, by using {@link SpringApplicationContext} and
     * {@link CreateSpringApplicationContext}, determines the application context in which to look for the bean.
     *
     * @param testObject The test instance, not null
     * @param type       The type, not null
     * @return The bean, a <code>NoSuchBeanDefinitionException</code> if not found
     */
    //Todo refactor
    // todo implement correct typing and not found behavior
    public Object getSpringBeanByType(Object testObject, Class<?> type) throws BeansException {
        Map beans = getApplicationContext(testObject).getBeansOfType(type);
        if (beans == null || beans.size() == 0) {
            return null;
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
     * Creates an application context using the settings of the {@link SpringApplicationContext} and
     * {@link CreateSpringApplicationContext} annotations.
     *
     * @param testObject The test instance, not null
     * @return The application context, not null
     */
    protected ApplicationContext createApplicationContext(Object testObject) {
        //todo impement
        ApplicationContext result = null;

        SpringApplicationContext springApplicationContextAnnotation = testObject.getClass().getAnnotation(SpringApplicationContext.class);
        if (springApplicationContextAnnotation != null) {
            // create application context
            try{
            result = new ClassPathXmlApplicationContext(springApplicationContextAnnotation.value());
            }catch(Throwable t){
                t.printStackTrace();
            }
            System.out.println("result = " + result);
        }

        List<Method> methods = AnnotationUtils.getMethodsAnnotatedWith(testObject.getClass(), CreateSpringApplicationContext.class);
        for (Method method : methods) {
            // todo implement method CreateSpring..
        }

        //todo what if no application context => exception
        return result;
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBean}.
     *
     * @param testObject The test instance, not null
     */
    public void getSpringBeans(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), SpringBean.class);
        for (Field field : fields) {
            try {
                SpringBean springBeanAnnotation = field.getAnnotation(SpringBean.class);
                ReflectionUtils.setFieldValue(testObject, field, getSpringBean(testObject, springBeanAnnotation.value()));

            } catch (UnitilsException e) {
                //todo check
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBean.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByType}.
     *
     * @param testObject The test instance, not null
     */
    public void getSpringBeansByType(Object testObject) {
        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByType.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, getSpringBeanByType(testObject, field.getType()));

            } catch (UnitilsException e) {
                //todo check
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByType.class.getSimpleName(), e);
            }
        }
    }


    /**
     * Gets the spring beans for all fields that are annotated with {@link SpringBeanByName}.
     *
     * @param testObject The test instance, not null
     */
    public void getSpringBeansByName(Object testObject) {

        List<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), SpringBeanByName.class);
        for (Field field : fields) {
            try {
                ReflectionUtils.setFieldValue(testObject, field, getSpringBean(testObject, field.getName()));

            } catch (UnitilsException e) {
                //todo check
                throw new UnitilsException("Unable to assign the Spring bean value to field annotated with @" + SpringBeanByName.class.getSimpleName(), e);
            }
        }
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
            getSpringBeans(testObject);
            getSpringBeansByType(testObject);
            getSpringBeansByName(testObject);
        }


    }

}
