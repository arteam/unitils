package org.unitils.tapestry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.tapestry.annotation.RunBeforeTapestryRegistryIsCreated;
import org.unitils.tapestry.annotation.TapestryRegistry;

/**
 * Unitils module that creates a Tapestry IOC registry for tests and allows
 * service injection into test fields. For injection just use the standard
 * tapestry injection annotations.
 * 
 * Example:
 * 
 * <pre>
 * &#064;TapestryRegistry(MyModule.class)
 * &#064;RunWith(UnitilsJUnit4TestClassRunner.class)
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
 * registry otherwise one registry per test class is created.
 */
public class TapestryUnitilsModule implements Module {

	private static Logger logger = LoggerFactory
			.getLogger(TapestryUnitilsModule.class);

	private Registry classRegistry;
	private Registry methodRegistry;

	public void afterInit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdown(classRegistry);
			}
		});
	}

	public TestListener getTestListener() {
		return new TapestryIoCTestListener();
	}

	public void init(Properties configuration) {
	}

	private void injectTapestryStuff(Registry registry, Class<?> testClass,
			Object testObject) {
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
			if(needsInjection(field) && Modifier.isStatic(field.getModifiers())) {
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
		if (field.getType() == Registry.class) {
			return registry;
		}

		return registry.getObject(field.getType(),
				new AnnotationProvider() {
					public <T extends Annotation> T getAnnotation(
							Class<T> annotationClass) {
						return field.getAnnotation(annotationClass);
					}
				});
	}

	private void injectFieldValue(Registry registry, Object testObject,
			Field field) {
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
				throw new UnitilsException("Cannot inject value into " + field,
						e);
			}
		}
	}

	private void shutdown(Registry registry) {
		if (registry != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("shutting down tapestry registry ...");
			}
			try {
				registry.cleanupThread();
				registry.shutdown();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	private boolean needsInjection(Field field) {
		return field.getAnnotation(Inject.class) != null
				|| field.getAnnotation(InjectService.class) != null;
	}

	private Registry createRegistryFor(TapestryRegistry annotation,
			Class<?> testClass, Object testObject) {
		Method method = getRegistryFactoryMethodFor(annotation, testClass,
				testObject);
		if (method == null) {
			Registry registry = new RegistryBuilder().add(annotation.value())
					.build();
			registry.performRegistryStartup();
			return registry;
		} else {
			try {
				if(method.getParameterTypes().length == 2) {
					return (Registry) method.invoke(testObject, annotation.registryFactoryMethodParameter(), (Object) annotation
							.value());
				} else {
					return (Registry) method.invoke(testObject, (Object) annotation
							.value());
				}
			} catch (Throwable t) {
				throw new RuntimeException(String.format("Error invoking %s",
						method), t);
			}
		}
	}

	private Method getRegistryFactoryMethodFor(TapestryRegistry annotation,
			Class<?> testClass, Object testObject) {

		if (annotation.registryFactoryMethodName().isEmpty())
			return null;

		Method method = null;
		try {
			method = testClass.getMethod(
					annotation.registryFactoryMethodName(), String.class,
					Class[].class);
		} catch (SecurityException e) {
			throw new TapestryUnitilsModuleException(String.format(
					"Registry factory method '%s' must be public", annotation
							.registryFactoryMethodName()), e);
		} catch (NoSuchMethodException e) {
		}
		if (method == null) {
			try {
				method = testClass.getMethod(annotation
						.registryFactoryMethodName(), Class[].class);
			} catch (SecurityException e) {
				throw new TapestryUnitilsModuleException(String.format(
						"Registry factory method '%s' must be public",
						annotation.registryFactoryMethodName()), e);
			} catch (NoSuchMethodException e) {
				throw new TapestryUnitilsModuleException(String.format(
						"Could not find registry factory method '%s'",
						annotation.registryFactoryMethodName()), e);
			}
		}
		if (!Registry.class.isAssignableFrom(method.getReturnType())) {
			throw new TapestryUnitilsModuleException(
					String.format("Registry factory method '%s' must return an instance of Registry",
							annotation.registryFactoryMethodName()));
		}
		if (testObject == null && !Modifier.isStatic(method.getModifiers())) {
			throw new TapestryUnitilsModuleException(String.format(
					"Registry factory method '%s' must be static", annotation
							.registryFactoryMethodName()));
		}
		return method;
	}

	private void runBeforeRegistryIsCreatedMethods(Class<?> testClass, Object testObject) {
		for (Method method : testClass.getMethods()) {
			if (method.isAnnotationPresent(RunBeforeTapestryRegistryIsCreated.class)) {
				if (testObject == null && !Modifier.isStatic(method.getModifiers())) {
					throw new TapestryUnitilsModuleException(
							String.format("Method must be static but %s is not static",
									method));
				}
				if (method.getParameterTypes().length != 0) {
					throw new TapestryUnitilsModuleException(
							String.format("Method annotated with @%s may not have any parameters, but %s has parameters",
								ClassUtils.getShortClassName(RunBeforeTapestryRegistryIsCreated.class),
								method));
				}
				try {
					method.invoke(testObject);
				} catch (Throwable t) {
					throw new RuntimeException(String.format(
							"Error invoking %s", method), t);
				}
			}
		}
	}

	private class TapestryIoCTestListener extends TestListener {
		@Override
		public void beforeTestClass(Class<?> testClass) {
			// shutdown the registry of the last test class
			shutdown(classRegistry);
			classRegistry = null;

			if (testClass.isAnnotationPresent(TapestryRegistry.class) && needsStaticInjection(testClass)) {
				runBeforeRegistryIsCreatedMethods(testClass, null);
				classRegistry = createRegistryFor(testClass
						.getAnnotation(TapestryRegistry.class), testClass, null);
				injectTapestryStuff(classRegistry, testClass, null);
			}
		}

		@Override
		public void beforeTestSetUp(Object testObject, Method testMethod) {
			Registry registryToUseForInjection = classRegistry;

			if (testMethod.isAnnotationPresent(TapestryRegistry.class)) {
				runBeforeRegistryIsCreatedMethods(testObject.getClass(), testObject);
				methodRegistry = createRegistryFor(testMethod
						.getAnnotation(TapestryRegistry.class), testObject
						.getClass(), testObject);
				registryToUseForInjection = methodRegistry;
			} else if(testObject.getClass().isAnnotationPresent(TapestryRegistry.class) && classRegistry == null) {
				runBeforeRegistryIsCreatedMethods(testObject.getClass(), testObject);
				classRegistry = createRegistryFor(testObject.getClass().getAnnotation(TapestryRegistry.class), 
						testObject.getClass(), testObject);
				injectTapestryStuff(classRegistry, testObject.getClass(), testObject);
				registryToUseForInjection = classRegistry;
			}
			if (registryToUseForInjection != null) {
				injectTapestryStuff(registryToUseForInjection, testObject
						.getClass(), testObject);
			}
		}

		@Override
		public void afterTestTearDown(Object testObject, Method testMethod) {
			shutdown(methodRegistry);
			methodRegistry = null;
		}
	}
}
