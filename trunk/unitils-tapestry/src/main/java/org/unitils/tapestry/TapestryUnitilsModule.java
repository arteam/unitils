package org.unitils.tapestry;

import org.apache.commons.lang.ClassUtils;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.tapestry.annotation.RegistryFactory;
import org.unitils.tapestry.annotation.RegistryShutdown;
import org.unitils.tapestry.annotation.RunBeforeTapestryRegistryIsCreated;
import org.unitils.tapestry.annotation.TapestryRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Unitils module that creates a Tapestry IOC registry for tests and allows
 * service injection into test fields. For injection just use the standard
 * tapestry injection annotations.
 *
 * Example:
 *
 * <pre>
 * &#064;TapestryRegistry(MyModule.class)
 * &#064;RunWith(UnitilsJUnit4BlockTestClassRunner.class)
 * public class MyTest {
 * 	&#064;Inject
 * 	private MyService service;
 * 	&#064;Inject
 * 	private static MyService staticService;
 * 	&#064;Inject
 * 	&#064;Symbol(&quot;SymbolSource-Symbol&quot;)
 * 	private String value;
 * }
 * </pre>
 *
 * To inject the Tapestry {@link Registry} just add a field of type
 * {@link Registry} and add the {@link Inject} annotation.
 *
 * Test methods marked with {@link TapestryRegistry} get their own private
 * registry otherwise one registry per test class is created. Injection is
 * always done before a test setup is performed (in static as well as non static
 * fields).
 */
public class TapestryUnitilsModule implements Module {

    private static Logger logger = LoggerFactory.getLogger(TapestryUnitilsModule.class);

    private Class<?> testClass;
    private Object testObject;
    private Registry registry;

    public void afterInit() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // shutdown the registry of the last test class
                if (testClass != null) {
                    shutdownRegistryFor(testClass.getAnnotation(TapestryRegistry.class), testClass, testObject, registry);
                }
            }
        });
    }

    public TestListener getTestListener() {
        return new TapestryIoCTestListener();
    }

    public void init(Properties configuration) {
    }

    private void injectTapestryStuff(Registry registry, Class<?> testClass, Object testObject) {
        for (Field field : testClass.getDeclaredFields()) {
            injectFieldValue(registry, testObject, field);
        }
        Class<?> superClazz = testClass.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            injectTapestryStuff(registry, superClazz, testObject);
        }
    }

    private boolean needsStaticInjection(Class<?> testClass) {
        for (Field field : testClass.getDeclaredFields()) {
            if (needsInjection(field) && Modifier.isStatic(field.getModifiers())) {
                return true;
            }
        }
        Class<?> superClazz = testClass.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return needsStaticInjection(superClazz);
        } else {
            return false;
        }
    }

    private Object getValueToInjectFor(Registry registry, final Field field) {
        if (field.getType() == Registry.class || field.getType() == ObjectLocator.class) {
            return registry;
        }

        return registry.getObject(field.getType(), new AnnotationProvider() {
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return field.getAnnotation(annotationClass);
            }
        });
    }

    private void injectFieldValue(Registry registry, Object testObject, Field field) {
        boolean injectValue = false;
        Object valueToInject = null;
        boolean isStatic = Modifier.isStatic(field.getModifiers());
        if (testObject == null && !isStatic) {
            return;
        }
        if (needsInjection(field)) {
            valueToInject = getValueToInjectFor(registry, field);
            injectValue = true;
        }
        if (injectValue) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(testObject, valueToInject);
            } catch (Exception e) {
                throw new UnitilsException("Cannot inject value into " + field, e);
            }
        }
    }

    private Method getRegistryShutdownMethodFor(TapestryRegistry annotation, Class<?> testClass, Object testObject) {
        Method shutdownMethod = null;
        if (annotation != null && annotation.registryShutdownMethodName().length() != 0) {
            try {
                shutdownMethod = testClass.getMethod(annotation.registryShutdownMethodName(), new Class<?>[]{Registry.class});
            } catch (SecurityException e) {
                throw new TapestryUnitilsModuleException(String.format("Registry shutdown method '%s' must be public", annotation.registryShutdownMethodName()),
                        e);
            } catch (NoSuchMethodException e) {
                throw new TapestryUnitilsModuleException(String.format("Could not find registry shutdown method '%s'", annotation.registryShutdownMethodName()),
                        e);
            }
        } else if(annotation != null) {
        	shutdownMethod = findMostSpecificAnnotatedMethod(testClass, RegistryShutdown.class, void.class, Registry.class);
        }
        
        if(shutdownMethod == null)
        	return null;
        
    	if(!Modifier.isPublic(shutdownMethod.getModifiers())) {
	        throw new TapestryUnitilsModuleException(String.format("Registry shutdown method '%s' must be public", shutdownMethod.getName()));
    	}
        if (Modifier.isStatic(shutdownMethod.getModifiers())) {
            throw new TapestryUnitilsModuleException(String.format("Registry factory method '%s' may not be static", shutdownMethod.getName()));
        }
        
        return shutdownMethod;
    }
    
    private void shutdownRegistryFor(TapestryRegistry annotation, Class<?> testClass, Object testObject, Registry registry) {
        if (registry != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("shutting down tapestry registry ...");
            }
            Method shutdownMethod = getRegistryShutdownMethodFor(annotation, testClass, testObject);
            try {
                if (shutdownMethod != null) {
                    shutdownMethod.invoke(testObject, registry);
                } else {
                    registry.shutdown();
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean needsInjection(Field field) {
        return field.getAnnotation(Inject.class) != null || field.getAnnotation(InjectService.class) != null;
    }

    private Registry createRegistryFor(TapestryRegistry annotation, Class<?> testClass, Object testObject) {
        Method method = getRegistryFactoryMethodFor(annotation, testClass, testObject);
        if (method == null) {
            Registry registry = new RegistryBuilder().add(annotation.value()).build();
            registry.performRegistryStartup();
            return registry;
        } else {
            try {
                if (method.getParameterTypes().length == 2) {
                    return (Registry) method.invoke(testObject, annotation.registryFactoryMethodParameter(), (Object) annotation.value());
                } else {
                    return (Registry) method.invoke(testObject, (Object) annotation.value());
                }
            } catch (Throwable t) {
                throw new RuntimeException(String.format("Error invoking %s", method), t);
            }
        }
    }

    private Method findMostSpecificAnnotatedMethod(Class<?> type, Class<? extends Annotation> annotationType, Class<?> returnType, Class<?>... parameterTypes) {
    	if(type == null) 
    		return null;
    	
    	for(Method method : type.getDeclaredMethods()) {
    		if(method.getAnnotation(annotationType) != null && method.getReturnType().equals(returnType) && 
    				Arrays.equals(method.getParameterTypes(), parameterTypes)) {
    			return method;
    		}
    	}
    	return findMostSpecificAnnotatedMethod(type.getSuperclass(), annotationType, returnType, parameterTypes);
    }
    
    private Method getRegistryFactoryMethodFor(TapestryRegistry annotation, Class<?> testClass, Object testObject) {
        Method method = null;
    	if (annotation.registryFactoryMethodName().length() == 0) {
            method = findMostSpecificAnnotatedMethod(testClass, RegistryFactory.class, Registry.class, Class[].class);
    	} else {
            try {
                method = testClass.getMethod(annotation.registryFactoryMethodName(), String.class, Class[].class);
            } catch (SecurityException e) {
                throw new TapestryUnitilsModuleException(String.format("Registry factory method '%s' must be public", annotation.registryFactoryMethodName()), e);
            } catch (NoSuchMethodException nsme) {
                try {
                    method = testClass.getMethod(annotation.registryFactoryMethodName(), Class[].class);
                } catch (SecurityException e) {
                    throw new TapestryUnitilsModuleException(String.format("Registry factory method '%s' must be public", annotation.registryFactoryMethodName()),
                            e);
                } catch (NoSuchMethodException e) {
                    throw new TapestryUnitilsModuleException(String.format("Could not find registry factory method '%s'", annotation.registryFactoryMethodName()),
                            e);
                }
            }
    	}
    	
    	if(method == null) 
    		return null;
    	
    	if(!Modifier.isPublic(method.getModifiers())) {
	        throw new TapestryUnitilsModuleException(String.format("Registry factory method '%s' must be public", method.getName()));
    	}
        if (!Registry.class.isAssignableFrom(method.getReturnType())) {
            throw new TapestryUnitilsModuleException(String.format("Registry factory method '%s' must return an instance of Registry", method.getName()));
        }
        if (testObject == null && !Modifier.isStatic(method.getModifiers())) {
            throw new TapestryUnitilsModuleException(String.format("Registry factory method '%s' must be static", method.getName()));
        }
        return method;
    }

    private void runBeforeRegistryIsCreatedMethods(Class<?> testClass, Object testObject) {
        // collect all classes in hierarchy order
        List<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> currentClass = testClass;
        while (currentClass != Object.class) {
            classes.add(0, currentClass);
            currentClass = currentClass.getSuperclass();
        }

        Map<Class<?>, List<Method>> methods = new HashMap<Class<?>, List<Method>>();
        for (Class<?> clazz : classes) {
            methods.put(clazz, new ArrayList<Method>());
        }
        // collect all static methods by class
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RunBeforeTapestryRegistryIsCreated.class) && Modifier.isStatic(method.getModifiers())) {
                    methods.get(clazz).add(method);
                }
            }
        }
        // collect all non static public methods
        for (Method method : testClass.getMethods()) {
            if (method.isAnnotationPresent(RunBeforeTapestryRegistryIsCreated.class) && !Modifier.isStatic(method.getModifiers())) {
                methods.get(method.getDeclaringClass()).add(method);
            }
        }
        for (Class<?> clazz : classes) {
            for (Method method : methods.get(clazz)) {
                if (method.isAnnotationPresent(RunBeforeTapestryRegistryIsCreated.class)) {
                    if (!Modifier.isPublic(method.getModifiers())) {
                        throw new TapestryUnitilsModuleException(String.format("Method annotated with @%s must be public", ClassUtils
                                .getShortClassName(RunBeforeTapestryRegistryIsCreated.class), method));
                    }
                    if (testObject == null && !Modifier.isStatic(method.getModifiers())) {
                        throw new TapestryUnitilsModuleException(String.format("Method must be static but %s is not static", method));
                    }
                    if (method.getParameterTypes().length != 0) {
                        throw new TapestryUnitilsModuleException(String.format("Method annotated with @%s may not have any parameters, but %s has parameters",
                                ClassUtils.getShortClassName(RunBeforeTapestryRegistryIsCreated.class), method));
                    }
                    try {
                        method.invoke(testObject);
                    } catch (Throwable t) {
                        throw new RuntimeException(String.format("Error invoking %s", method), t);
                    }
                }
            }
        }
    }

    private boolean needsStaticRegistry(Class<?> type) {
        return testClass.isAnnotationPresent(TapestryRegistry.class) && needsStaticInjection(testClass);
    }

    private boolean needsRegistry(Class<?> type) {
        return testClass.isAnnotationPresent(TapestryRegistry.class);
    }

    private boolean needsRegistry(Class<?> type, Method method) {
        return (type.isAnnotationPresent(TapestryRegistry.class) && type.getAnnotation(TapestryRegistry.class).createRegistryPerTest())
                || method.isAnnotationPresent(TapestryRegistry.class);
    }

    private TapestryRegistry getAnnotation(Class<?> type, Method method) {
        return method.isAnnotationPresent(TapestryRegistry.class) ? method.getAnnotation(TapestryRegistry.class) : type.getAnnotation(TapestryRegistry.class);
    }

    private class TapestryIoCTestListener extends TestListener {
        @Override
        public void beforeTestClass(Class<?> currentTestClass) {
            // shutdown the registry of the last test class
            if (testClass != null) {
                shutdownRegistryFor(testClass.getAnnotation(TapestryRegistry.class), testClass, testObject, registry);
                registry = null;
            }

            testObject = null;
            testClass = currentTestClass;
            if (needsStaticRegistry(testClass)) {
                runBeforeRegistryIsCreatedMethods(testClass, null);
                registry = createRegistryFor(testClass.getAnnotation(TapestryRegistry.class), testClass, null);
                injectTapestryStuff(registry, testClass, null);
            }
        }

        @Override
        public void beforeTestSetUp(Object currentTestObject, Method testMethod) {
            testObject = currentTestObject;
            if (needsRegistry(testClass, testMethod)) {
                // per method registry

                // shutdown class registry
                shutdownRegistryFor(testClass.getAnnotation(TapestryRegistry.class), testClass, currentTestObject, registry);
                runBeforeRegistryIsCreatedMethods(currentTestObject.getClass(), currentTestObject);
                registry = createRegistryFor(getAnnotation(testClass, testMethod), currentTestObject.getClass(), currentTestObject);
            } else if (needsRegistry(testClass) && registry == null) {
                // per class registry
                runBeforeRegistryIsCreatedMethods(currentTestObject.getClass(), currentTestObject);
                registry = createRegistryFor(currentTestObject.getClass().getAnnotation(TapestryRegistry.class), currentTestObject.getClass(), currentTestObject);
            }
            if (registry != null) {
                injectTapestryStuff(registry, currentTestObject.getClass(), currentTestObject);
            }
        }

        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            if (needsRegistry(testClass, testMethod)) {
                // shutdown method registry
                shutdownRegistryFor(getAnnotation(testClass, testMethod), testClass, testObject, registry);
                registry = null;
            }
        }
    }
}
