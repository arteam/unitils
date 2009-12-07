package org.unitils.tapestry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

	private Registry registry;

	public void afterInit() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shutdownRegistry();
			}
		});
	}

	public TestListener getTestListener() {
		return new TapestryIoCTestListener();
	}

	public void init(Properties configuration) {
	}

	protected Registry createRegistryFor(Class<?> testClass, Method method) {
		RegistryBuilder builder = new RegistryBuilder();
		for (Class<?> module : getTapestryModulesFor(testClass, method)) {
			builder.add(module);
		}
		return builder.build();
	}

	protected List<Class<?>> getTapestryModulesFor(Class<?> testClass,
			Method method) {
		Set<Class<?>> modules = new HashSet<Class<?>>();
		
		boolean addClassModules = true;
		if (method != null) {
			TapestryRegistry registry = method.getAnnotation(TapestryRegistry.class);
			if(registry != null) {
				modules.addAll(Arrays.asList(registry.value()));
			}
			addClassModules = false;
		}
		if(addClassModules) {
			TapestryRegistry registry = testClass.getAnnotation(TapestryRegistry.class);
			if(registry != null) {
				modules.addAll(Arrays.asList(registry.value()));
			}
		}
		return new ArrayList<Class<?>>(modules);
	}

	protected void createAndStartupRegistryFor(Class<?> testClass, Method method) {
		runMethodsBeforeTapestryRegistryIsCreated(testClass);
		shutdownRegistry();
		if (logger.isDebugEnabled()) {
			logger.debug("starting tapestry registry for " + testClass);
		}
		registry = createRegistryFor(testClass, method);
		registry.performRegistryStartup();
	}

	private void runMethodsBeforeTapestryRegistryIsCreated(Class<?> testClass) {
		List<Class<?>> testClasses = new ArrayList<Class<?>>();
		while(testClass != Object.class) {
			testClasses.add(0, testClass);
			testClass = testClass.getSuperclass();
		}
		for(Class<?> clazz : testClasses) {
			for(Method method : clazz.getDeclaredMethods()) {
				if(method.getAnnotation(RunBeforeTapestryRegistryIsCreated.class) != null) {
					if(!Modifier.isStatic(method.getModifiers())) {
						throw new RuntimeException("Cannot call method annotated with @" + ClassUtils.getShortClassName(RunBeforeTapestryRegistryIsCreated.class) + " because it is not static");
					}
					if(!Modifier.isPublic(method.getModifiers())) {
						throw new RuntimeException("Cannot call method annotated with @" + ClassUtils.getShortClassName(RunBeforeTapestryRegistryIsCreated.class) + " because it is not public");
					}
					try {
						method.invoke(null);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	/**
	 * @param testObject
	 *            Nullable. If null, then only static fields are injected.
	 */
	protected void injectTapestryStuff(Object testObject, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			injectFieldValue(testObject, field);
		}
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			injectTapestryStuff(testObject, superClazz);
		}
	}

	protected Object getValueToInjectFor(final Field field) {
		if (field.getType() == Registry.class) {
			return registry;
		}

		return registry.getObject(field.getType(), new AnnotationProvider() {
			public <T extends Annotation> T getAnnotation(
					Class<T> annotationClass) {
				return field.getAnnotation(annotationClass);
			}
		});
	}

	/**
	 * @param testObject
	 *            Nullable. If null, then the service is only injected into
	 *            static fields.
	 */
	protected void injectFieldValue(Object testObject, Field field) {
		boolean injectValue = false;
		Object valueToInject = null;
		boolean isStatic = Modifier.isStatic(field.getModifiers());
		if ((testObject == null && !isStatic)
				|| (testObject != null && isStatic)) {
			return;
		}
		if (field.getAnnotation(Inject.class) != null
				|| field.getAnnotation(InjectService.class) != null) {
			valueToInject = getValueToInjectFor(field);
			injectValue = true;
		}
		if (injectValue) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			try {
				field.set(testObject, valueToInject);
			} catch (Exception e) {
				throw new UnitilsException("cannot inject value into " + field,
						e);
			}
		}
	}

	protected void shutdownRegistry() {
		if (registry != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("shutting down tapestry registry ...");
			}
			try {
				registry.cleanupThread();
				registry.shutdown();
				registry = null;
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	private class TapestryIoCTestListener extends TestListener {
		private boolean currentTestNeedsPrivateRegistry;
		private Registry sharedRegistry;

		@Override
		public void beforeTestClass(Class<?> testClass) {
			createAndStartupRegistryFor(testClass, null);
			injectTapestryStuff(null, testClass);
		}

		@Override
		public void beforeTestSetUp(Object testObject, Method testMethod) {
			currentTestNeedsPrivateRegistry = testMethod
					.isAnnotationPresent(TapestryRegistry.class);
			if (currentTestNeedsPrivateRegistry) {
				sharedRegistry = registry;
				registry = null;
				createAndStartupRegistryFor(testObject.getClass(), testMethod);
			}
			injectTapestryStuff(testObject, testObject.getClass());
		}

		@Override
		public void afterTestTearDown(Object testObject, Method testMethod) {
			if (currentTestNeedsPrivateRegistry) {
				shutdownRegistry();
				registry = sharedRegistry;
				sharedRegistry = null;
			}
		}
	}
}
