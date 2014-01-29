/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.util;

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.unitils.core.UnitilsException;
import org.unitils.core.util.ResourceConfig;
import org.unitils.core.util.ResourceConfigLoader;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;
import org.unitils.util.AnnotationUtils;

/**
 * Loads the configuration of a resource that is configured on a test object, by reading class, method or field
 * level annotations. Also supports custom configuration methods.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 * @param <A> The annotation class used for configuring the resource on a test object
 * @param <CFG> The type of the resource configuration class
 */
abstract public class AnnotationConfigLoader<A extends Annotation, CFG extends ResourceConfig> implements ResourceConfigLoader<CFG> {
	
	/**
	 * The annotation class used for configuring the resource on a test object
	 */
	protected Class<A> annotationClass;
	
	
	/**
	 * Creates a new instance for the given annotation class
	 * 
	 * @param annotationClass The class of the configuring annotation, not null
	 */
	public AnnotationConfigLoader(Class<A> annotationClass) {
		this.annotationClass = annotationClass;
	}
	
	
	/**
	 * Loads the resource configuration for the given test object
	 * 
	 * @return The resource configuration, if available. Null if not.
	 */
	public CFG loadResourceConfig(Object testObject) {
		return getResourceConfig(testObject.getClass());
	}
	
	
	/**
	 * @param testClass The test class, not null
	 * @return The resource config object
	 */
	protected CFG getResourceConfig(Class<?> testClass) {
		
		// look for a class level configuring annotation 
		Set<A> configuringAnnotations = new HashSet<A>();
        A annotation = testClass.getAnnotation(annotationClass);
        if (annotation != null && isConfiguringAnnotation(annotation)) {
            configuringAnnotations.add(annotation);
        }

        // look for a field level configuring annotation 
        Set<Field> annotatedFields = getFieldsAnnotatedWith(testClass, annotationClass);
        for (Field field : annotatedFields) {
            annotation = field.getAnnotation(annotationClass);
            if (isConfiguringAnnotation(annotation)) {
                configuringAnnotations.add(annotation);
            }
        }

        // look for a method level configuring annotation 
        Set<Method> annotatedMethods = getMethodsAnnotatedWith(testClass, annotationClass, false);
        for (Method annotatedMethod : annotatedMethods) {
            annotation = annotatedMethod.getAnnotation(annotationClass);
            if (isConfiguringAnnotation(annotation)) {
                configuringAnnotations.add(annotation);
            }
        }
        
        // look for a custom config method
        Method customConfigMethod = null;
        for (Method annotatedMethod : annotatedMethods) {
        	if (isCustomConfigMethod(annotatedMethod)) {
        		customConfigMethod = annotatedMethod;
        	}
        }
        
        if (configuringAnnotations.size() > 1) {
            throw new UnitilsException("Class " + testClass.getSimpleName() + " has " + configuringAnnotations.size() + 
            		" configuring @" + annotationClass.getSimpleName() + " annotations. There can only be one such annotation per class.");
		}
		
		if (configuringAnnotations.size() == 0 && customConfigMethod == null) {
			if (Object.class.equals(testClass.getSuperclass())) {
				return null;
			}
			return getResourceConfig(testClass.getSuperclass());
		}
		
		return createResourceConfig(configuringAnnotations.iterator().next(), customConfigMethod);
	}
	
	
	/**
	 * Either the given configuring annotation or custom config method must not be null (may also both be not-null)
	 * 
	 * @param configuringAnnotation Configuring annotation, if any
	 * @param customConfigMethod Custom config method, if any
	 * @return A new resource config object, given the configuring annotation and custom configuration method 
	 */
	abstract protected CFG createResourceConfig(A configuringAnnotation, Method customConfigMethod);


	/**
	 * @param testClass The test class, not null
	 * @return The test class's custom configuration method, if any
	 */
	protected Method getCustomConfigMethod(Class<?> testClass) {
		Set<Method> annotatedMethods = AnnotationUtils.getMethodsAnnotatedWith(testClass, JpaEntityManagerFactory.class);
		for (Method annotatedMethod : annotatedMethods) {
			if (isCustomConfigMethod(annotatedMethod)) {
				return annotatedMethod;
			}
		}
		return null;
	}
	
	
	/**
	 * @param annotatedMethod A method annotated with {@link #annotationClass}
	 * @return True if the given method is a custom configuration method
	 */
	protected abstract boolean isCustomConfigMethod(Method annotatedMethod);

	
	/**
	 * @param annotation Annotation of type {@link #annotationClass}
	 * @return True if for the given annotation, the necessary attributes are filled so that it can be
	 * regarded as a configuring annotation
	 */
	protected abstract boolean isConfiguringAnnotation(A annotation);

}
