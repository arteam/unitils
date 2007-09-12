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
package org.unitils.core.util;

import org.unitils.core.UnitilsException;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for managing and creating instances of a given type. A given annotation controls how a new instance will be created.
 * <p/>
 * Instances will be created if an annotation instance is found that specifies values or if a custom create method
 * is found. Custom create methods are methods that are marked with the annotation and have one of following signatures:
 * <ul>
 * <li>T createMethodName() or</li>
 * <li>T createMethodName(List<String> values)</li>
 * </ul>
 * For the second version the found annotation values are passed to the creation method.
 * <p/>
 * Subclass overrides superclass configuration. That is, when a subclass and superclass contain an annotation with values,
 * only the values of the sub-class will be used. Same is true for custom create methods. Methods in subclasses override
 * methods in superclasses.
 * <p/>
 * Lets explain all this with an example:
 * <pre><code>
 * ' @MyAnnotation("supervalue")
 * ' public class SuperClass {
 * '
 * '     @MyAnnotation
 * '     protected MyType createMyType(List<String> values)
 * ' }
 * '
 * ' @MyAnnotation({"value1", "value2"})
 * ' public class MyClass extends SuperClass {
 * '
 * '}
 * </code></pre>
 * Following steps are performed: there is annotation with 2 values on the sub class. These values override the value of
 * the annotation in the superclass and will be used for creating a new instance. The 2 values are then passed to the
 * createMyType custom create method to create the actual instance.
 * <p/>
 * If no custom create method is found, the default {@link #createInstanceForValues} method is called for creating
 * the instance.
 * <p/>
 * Created instances are cached on the level in the hierarchy that caused the creation of the instance. That is,
 * if a subclass did not contain any annotations with values or any custom create methods, the super class is tried,
 * if an instance for that super class was already created, that instance will be returned. This way, instances are
 * reused as much as possible.
 * <p/>
 * If an instance needs to be recreated (for example because a test made modification to it), it can be removed from
 * the cache by calling {@link #invalidateInstance}
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @param <T> Type of the object that is configured by the annotations
 * @param <A> Type of the annotation that is used for configuring the instance
 */
public abstract class AnnotatedInstanceManager<T, A extends Annotation> {

    /**
     * All created intances per class
     */
    protected Map<Class<?>, T> instances = new HashMap<Class<?>, T>();

    /**
     * The type of the managed instances
     */
    protected Class<T> instanceClass;

    /**
     * The annotation type
     */
    protected Class<A> annotationClass;


    /**
     * Creates a manager
     *
     * @param instanceClass   The type of the managed instances
     * @param annotationClass The annotation type
     */
    protected AnnotatedInstanceManager(Class<T> instanceClass, Class<A> annotationClass) {
        this.instanceClass = instanceClass;
        this.annotationClass = annotationClass;
    }


    /**
     * Gets an instance for the given test. This will first look for values of annotations on the test class and
     * its super classes. If there is a custom create method, that method is then used to create the instance
     * (passing the values). If no create was found, {@link #createInstanceForValues} is called to create the instance.
     *
     * @param testObject The test object, not null
     * @return The instance, null if not found
     */
    protected T getInstance(Object testObject) {
        return getInstanceImpl(testObject, testObject.getClass());
    }


    /**
     * Registers an instance for a given class. This will cause {@link #getInstance} to return the given instance
     * if the testObject is of the given test type.
     *
     * @param testClass The test type, not null
     * @param instance  The instance, not null
     */
    protected void registerInstance(Class<?> testClass, T instance) {
        instances.put(testClass, instance);
    }


    /**
     * Checks whether {@link #getInstance} will return an instance. If false is returned, {@link #getInstance} will
     * return null.
     *
     * @param testObject The test object, not null
     * @return True if an instance is linked to the given test object
     */
    protected boolean hasInstance(Object testObject) {
        return hasInstanceImpl(testObject, testObject.getClass());
    }


    /**
     * Forces the recreation of the instance the next time that it is requested. If classes are given as argument
     * only instances on those class levels will be reset. If no classes are given, all cached
     * instances will be reset.
     *
     * @param classes The classes for which to reset the instances
     */
    protected void invalidateInstance(Class<?>... classes) {
        if (classes == null || classes.length == 0) {
            instances.clear();
            return;
        }
        for (Class<?> clazz : classes) {
            instances.remove(clazz);
        }
    }


    /**
     * Recursive implementation of {@link #hasInstance(Object)}.
     *
     * @param testObject The test object, not null
     * @param testClass  The level in the hierarchy
     * @return True if an instance is linked to the given test object
     */
    protected boolean hasInstanceImpl(Object testObject, Class<?> testClass) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return false;
        }

        // check whether it already exists for the test class (eg registered instance)
        if (instances.containsKey(testClass)) {
            return true;
        }

        // check annotation values
        if (!getAnnotationValues(testClass).isEmpty()) {
            return true;
        }

        // check custom create method
        if (getCustomCreateMethod(testClass, false) != null) {
            return true;
        }

        // nothing found on this level, check super-class
        return hasInstanceImpl(testObject, testClass.getSuperclass());
    }


    /**
     * Recursive implementation of {@link #getInstance(Object)}.
     *
     * @param testObject The test object, not null
     * @param testClass  The level in the hierarchy
     * @return The instance, null if not found
     */
    protected T getInstanceImpl(Object testObject, Class<?> testClass) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return null;
        }

        // check whether it already exists for the test class (eg registered instance)
        T instance = instances.get(testClass);
        if (instance != null) {
            return instance;
        }

        // get annotation values of this class
        List<String> annotationValues = getAnnotationValues(testClass);

        // get custom create methods of this class
        Method customCreateMethod = getCustomCreateMethod(testClass, false);

        // invoke custom create method, if there is one
        if (customCreateMethod != null) {
            instance = invokeCustomCreateMethod(customCreateMethod, testObject, annotationValues);
        } else if (!annotationValues.isEmpty()) {
            customCreateMethod = getCustomCreateMethod(testClass, true);
            if (customCreateMethod != null) {
                // if there are values but no custom create method, use default creation mechanism
                instance = invokeCustomCreateMethod(customCreateMethod, testObject, annotationValues);
            } else {
                instance = createInstanceForValues(annotationValues);
            }
        }

        // if nothing found on this level, try super-class
        if (instance == null) {
            return getInstanceImpl(testObject, testClass.getSuperclass());
        }

        // initialize instance if needed
        afterInstanceCreate(instance, testObject, testClass);

        // store instance in cache
        registerInstance(testClass, instance);
        return instance;
    }


    /**
     * Hook method that can be overriden to perform extra initialization after the instance was created.
     *
     * @param instance   The instance, not null
     * @param testObject The test object, not null
     * @param testClass  The level in the hierarchy
     */
    protected void afterInstanceCreate(T instance, Object testObject, Class<?> testClass) {
    }


    /**
     * Gets the values of the annotations on the given class.
     * This will look for class-level, method-level and field-level annotations with values.
     * If more than 1 such annotation is found, an exception is raised.
     * If no annotation was found, an empty list is returned.
     *
     * @param testClass The test class, not null
     * @return The values of the annotation, empty list if none found
     */
    protected List<String> getAnnotationValues(Class<?> testClass) {
        // check class level annotation values
        List<A> annotations = new ArrayList<A>();
        A annotation = testClass.getAnnotation(annotationClass);
        if (annotation != null && getAnnotationValues(annotation) != null) {
            annotations.add(annotation);
        }

        // check field level annotation values
        List<Field> fields = getFieldsAnnotatedWith(testClass, annotationClass);
        for (Field field : fields) {
            annotation = field.getAnnotation(annotationClass);
            if (annotation != null && getAnnotationValues(annotation) != null) {
                annotations.add(annotation);
            }
        }

        // check custom create methods and method level annotation values
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, false);
        for (Method method : methods) {
            annotation = method.getAnnotation(annotationClass);
            if (annotation != null && getAnnotationValues(annotation) != null) {
                annotations.add(annotation);
            }
        }

        // check whether there is more than 1 annotation with values
        if (annotations.size() > 1) {
            throw new UnitilsException("There can only be 1 @" + annotationClass.getSimpleName() + " annotation with values per class.");
        }

        // if nothing found, return empty list
        if (annotations.isEmpty()) {
            return new ArrayList<String>();
        }

        // found exactly 1 annotation ==> get values
        annotation = annotations.get(0);
        return getAnnotationValues(annotation);
    }


    /**
     * Gets the custom create methods on the given class.
     * If there is more than 1 create method found, an exception is raised.
     * If no create method was found, null is returned.
     * If searchSuperClasses is true, it will also look in super classes for create methods.
     *
     * @param testClass          The test class, not null
     * @param searchSuperClasses True to look recursively in superclasses
     * @return The instance, null if no create method was found
     */
    protected Method getCustomCreateMethod(Class<?> testClass, boolean searchSuperClasses) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return null;
        }

        // get all annotated methods from the given test class, no superclasses
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, false);

        // look for correct signature (no return value)
        List<Method> customCreateMethods = new ArrayList<Method>();
        for (Method method : methods) {
            // do not invoke setter methods
            if (method.getReturnType() != Void.TYPE) {
                customCreateMethods.add(method);
            }
        }

        // check whether there is more than 1 custom create method
        if (customCreateMethods.size() > 1) {
            throw new UnitilsException("There can only be 1 method per class annotated with @" + annotationClass.getSimpleName() + " for creating a session factory.");
        }

        // if nothing found, look in superclass or return null
        if (customCreateMethods.isEmpty()) {
            if (searchSuperClasses) {
                return getCustomCreateMethod(testClass.getSuperclass(), searchSuperClasses);
            }
            return null;
        }

        // found exactly 1 custom create method ==> check correct signature
        Method customCreateMethod = customCreateMethods.get(0);
        if (!isCustomCreateMethod(customCreateMethod)) {
            throw new UnitilsException("Custom create method annotated with @" + annotationClass.getSimpleName() + " should have following signature: " + instanceClass.getName() + " myMethod( List<String> locations ) or " + instanceClass.getName() + " myMethod()");
        }
        return customCreateMethod;
    }


    /**
     * Checks whether the given method is a custom create method. A custom create method must have following signature:
     * <ul>
     * <li>T createMethodName() or</li>
     * <li>T createMethodName(List<String> values)</li>
     * </ul>
     *
     * @param method The method, not null
     * @return True if it has the correct signature
     */
    protected boolean isCustomCreateMethod(Method method) {
        Class<?>[] argumentTypes = method.getParameterTypes();
        if (argumentTypes.length > 1) {
            return false;
        }
        if (argumentTypes.length == 1 && argumentTypes[0] != List.class) {
            return false;
        }
        return instanceClass.isAssignableFrom(method.getReturnType());
    }


    /**
     * Creates an instance by calling a custom create method (if there is one). Such a create method should have one of
     * following exact signatures:
     * <ul>
     * <li>Configuration createMethodName() or</li>
     * <li>Configuration createMethodName(List<String> locations)</li>
     * </ul>
     * The second version receives the given locations. They both should return an instance (not null)
     *
     * @param customCreateMethod The create method, not null
     * @param testObject         The test object, not null
     * @param annotationValues   The specified locations if there are any, not null
     * @return The instance, null if no create method was found
     */
    @SuppressWarnings({"unchecked"})
    protected T invokeCustomCreateMethod(Method customCreateMethod, Object testObject, List<String> annotationValues) {
        T result;
        try {
            // call method
            if (customCreateMethod.getParameterTypes().length == 0) {
                result = (T) invokeMethod(testObject, customCreateMethod);
            } else {
                result = (T) invokeMethod(testObject, customCreateMethod, annotationValues);
            }
        } catch (InvocationTargetException e) {
            throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + customCreateMethod + " (annotated with " + annotationClass.getSimpleName() + ") has thrown an exception", e.getCause());
        }
        // check whether create returned a value
        if (result == null) {
            throw new UnitilsException("Method " + testObject.getClass().getSimpleName() + "." + customCreateMethod + " (annotated with " + annotationClass.getSimpleName() + ") has returned null.");
        }
        return result;
    }


    /**
     * Gets the values that are specified for the given annotation. An array with 1 empty string should
     * be considered to be empty and null should be returned.
     *
     * @param annotation The annotation, not null
     * @return The values, null if no values were specified
     */
    abstract protected List<String> getAnnotationValues(A annotation);


    /**
     * Creates an instance for the given values.
     *
     * @param values The values, not null
     * @return The instance, not null
     */
    abstract protected T createInstanceForValues(List<String> values);
}
