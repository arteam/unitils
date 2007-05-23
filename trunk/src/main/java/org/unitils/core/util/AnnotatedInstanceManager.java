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

import org.unitils.core.UnitilsException;

/**
 * Class for managing and creating instances of a given type. A given annotation controls how a new instance will be created.
 * First all values of all found annotations in the test class or any super classes are collected . These values (super class values
 * before of sub class values), are then used to create the instance. If a custom create method is specified on the test class,
 * this method is used to create the instance, else {@link #createInstanceForValues} is used to create it. Custom create
 * methods are methods that are marked with the annotation and have one of following signatures:
 * <ul>
 * <li>T createMethodName() or</li>
 * <li>T createMethodName(List<String> values)</li>
 * </ul>
 * For the second version the found values are passed to the creation method.
 * <p/>
 * Lets explain all this with an example:
 * <pre><code>
 * ' @MyAnnotation("supervalue")
 *   public class SuperClass {
 *   }
 * '
 * ' @MyAnnotation({"value1", "value2"})
 *   public class MyClass extends SuperClass
 * <p/>
 * '     @MyAnnotation("value3")
 *       private MyType type
 * <p/>
 * '     @MyAnnotation
 *       protected MyType createMyType(List<String> values)
 * <p/>
 * <p/>
 * </code></pre>
 * If a new instance needs to be created for this class, following steps are performed. First the value of the super class
 * annotation (supervalue) is retrieved. Next, the values of the MyClass are retrieved from the class annotation and the
 * field annotation. Lastly, the createMyType method is called to create the actual instance passing the list of
 * collected values (supervalue, value1, value2 and value3 in that order)
 * <p/>
 * Created instances are cached on the level in the hierarchy that caused the creation of the instance. That is,
 * if a subclass did not contain any annotations with values or any custom create methods, the super class is tried,
 * if an instance for that super class was already created, that instance will be returned. This way, instances are
 * reused as much as possible.
 * <p/>
 * If an instance needs to be recreated (for example because a test made modification to it), it can be removed from
 * the cache by calling {@link #invalidateInstance}
 *
 * @param <T> Type of the object that is configured by the annotations
 * @param <A> Type of the annotation that is used for configuring the instance
 
 * @author Tim Ducheyne
 * @author Filip Neven
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
     * Gets an instance for the given test. An exception will be raised if no instance could be created because
     * of an incorrect or missing configuration. For example, the annotation could not be found in the test object class
     * or one of its super classes.
     *
     * @param testObject The test object, not null
     * @return The instance, not null
     */
    protected T getInstance(Object testObject) {
        // check whether it already exists for the test class (eg registered instance)
        T instance = instances.get(testObject.getClass());
        if (instance != null) {
            return instance;
        }

        // find class level for which a new instance should be created
        Class<?> testClass = findClassLevelForCreateInstance(testObject.getClass());
        if (testClass == null) {
            return null;
        }

        // check whether it already exists
        instance = instances.get(testClass);
        if (instance != null) {
            return instance;
        }

        // create instance
        instance = createInstance(testObject, testClass);

        // store instance in cache
        registerInstance(testClass, instance);
        return instance;
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
     * throw an exception.
     *
     * @param testObject The test object, not null
     * @return True if an instance is linked to the given test object
     */
    protected boolean hasInstance(Object testObject) {
        if (instances.get(testObject.getClass()) != null) {
            return true;
        }
        return findClassLevelForCreateInstance(testObject.getClass()) != null;
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
     * Creates a new instance for the given test. This will first retrieve the values of all annotations on
     * the test class and its super classes. If there is a custom create method, that method is then used
     * to create the instance (passing the values). If no create was found, {@link #createInstanceForValues} is
     * called to create the instance.
     *
     * @param testObject The test object, not null
     * @param testClass  The level in the hierarchy
     * @return The instance, not null
     */
    protected T createInstance(Object testObject, Class<?> testClass) {
        // retrieve annotations values of test class and super classes
        List<String> annotationValues = new ArrayList<String>();
        getAnnotationValues(testObject, testClass, annotationValues);

        // let custom create-method (if any) create instance
        T instance = invokeCreateInstanceMethod(testObject, testClass, annotationValues);

        // if no instance was created, use default creation mechanism
        if (instance == null) {
            instance = createInstanceForValues(annotationValues);
        }
        return instance;
    }


    /**
     * Gets all values specified in annotations for the given testClass and all its super classes. Super class values
     * in front of sub class values.
     *
     * @param testObject The test object, not null
     * @param testClass  The current class in the hierarchy, can be null
     * @param result     The list to which the values will be added, not null
     */
    protected void getAnnotationValues(Object testObject, Class<?> testClass, List<String> result) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return;
        }

        // add values of super classes
        getAnnotationValues(testObject, testClass.getSuperclass(), result);

        // get class level annotation values
        List<String> classLevelAnnotationValues = getAnnotationValues(testClass.getAnnotation(annotationClass));
        if (classLevelAnnotationValues != null) {
            result.addAll(classLevelAnnotationValues);
        }
        // get field level annotation values
        List<Field> fields = getFieldsAnnotatedWith(testClass, annotationClass);
        for (Field field : fields) {
            List<String> fieldLevelAnnotationValues = getAnnotationValues(field.getAnnotation(annotationClass));
            if (fieldLevelAnnotationValues != null) {
                result.addAll(fieldLevelAnnotationValues);
            }
        }
        // get method level annotation values
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, false);
        for (Method method : methods) {
            List<String> methodLevelAnnotationValues = getAnnotationValues(method.getAnnotation(annotationClass));
            if (methodLevelAnnotationValues != null) {
                result.addAll(methodLevelAnnotationValues);
            }
        }
    }


    /**
     * Creates an instance by calling a custom create method (if there is one). Such a create method should have one of
     * following exact signatures:
     * <ul>
     * <li>Configuration createMethodName() or</li>
     * <li>Configuration createMethodName(List<String> locations)</li>
     * </ul>
     * The second version receives the given locations. They both should return an instance (not null)
     * <p/>
     * If no create method was found, null is returned. If there is more than 1 create method found, an exception is raised.
     *
     * @param testObject The test object, not null
     * @param testClass  The level in the hierarchy
     * @param locations  The specified locations if there are any, not null
     * @return The instance, null if no create method was found
     */
    @SuppressWarnings({"unchecked"})
    protected T invokeCreateInstanceMethod(Object testObject, Class<?> testClass, List<String> locations) {
        // get all annotated methods from the given test class, no superclasses
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, false);

        T result = null;
        boolean createMethodFound = false;
        for (Method method : methods) {
            // do not invoke setter methods
            if (method.getReturnType() == Void.TYPE) {
                continue;
            }
            if (!isCreateInstanceMethod(method)) {
                throw new UnitilsException("Unable to invoke method annotated with @" + annotationClass.getSimpleName() +
                        ". Ensure that this method has following signature: " + instanceClass.getName() + " myMethod( List<String> locations ) or " +
                        instanceClass.getName() + " myMethod()");
            }

            // check whether there is more than 1 custom create method
            if (createMethodFound) {
                throw new UnitilsException("There can only be 1 method per class annotated with @" + annotationClass.getSimpleName() + " for creating a session factory.");
            }
            createMethodFound = true;

            try {
                // call method
                if (method.getParameterTypes().length == 0) {
                    result = (T) invokeMethod(testObject, method);
                } else {
                    result = (T) invokeMethod(testObject, method, locations);
                }
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Method " + testClass.getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + annotationClass.getSimpleName() + ") has thrown an exception", e.getCause());
            }
            // check whether create returned a value
            if (result == null) {
                throw new UnitilsException("Method " + testClass.getSimpleName() + "." + methods.get(0).getName() +
                        " (annotated with " + annotationClass.getSimpleName() + ") has returned null.");

            }
        }
        return result;
    }


    /**
     * Finds the level in the class hierarchy for which an instance should be created. That is, a class level that contains
     * a custom create method or specifies a location in one of the annotations. Such a level should have its own instance
     * and cannot reuse the instance of a superclass.
     * <p/>
     * If a class only contains annotations without locations (for example for injecting the instance into a field), the
     * superclasses will be checked untill a level is found for which a config should be created. If no level is found,
     * null is returned.
     *
     * @param testClass The current level in the hierarchy
     * @return The level for which an instance should created, null if none found
     */
    protected Class<?> findClassLevelForCreateInstance(Class<?> testClass) {
        // nothing to do (ends the recursion)
        if (testClass == null || testClass == Object.class) {
            return null;
        }

        // check class level annotation values
        if (getAnnotationValues(testClass.getAnnotation(annotationClass)) != null) {
            return testClass;
        }

        // check field level annotation values
        List<Field> fields = getFieldsAnnotatedWith(testClass, annotationClass);
        for (Field field : fields) {
            if (getAnnotationValues(field.getAnnotation(annotationClass)) != null) {
                return testClass;
            }
        }

        // check custom create methods and method level annotation values
        List<Method> methods = getMethodsAnnotatedWith(testClass, annotationClass, false);
        for (Method method : methods) {
            if (isCreateInstanceMethod(method)) {
                return testClass;
            }
            if (getAnnotationValues(method.getAnnotation(annotationClass)) != null) {
                return testClass;
            }
        }
        // nothing found on this level, check superclass
        return findClassLevelForCreateInstance(testClass.getSuperclass());
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
    protected boolean isCreateInstanceMethod(Method method) {
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
     * Gets the values that are specified for the given annotation. If the annotation is null or
     * if no values were specified, null should be returned. An array with 1 empty string should
     * also be considered to be empty.
     *
     * @param annotation The annotation
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
