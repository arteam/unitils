package org.unitils.tapestry;

import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.tapestry.annotation.Constants;
import org.unitils.tapestry.annotation.DoNotInject;
import org.unitils.tapestry.annotation.PrivateTapestryRegistry;
import org.unitils.tapestry.annotation.TapestryModule;
import org.unitils.tapestry.annotation.TapestryService;
import org.unitils.tapestry.annotation.TapestrySymbol;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Unitils module that creates a Tapestry IOC registry for tests and
 * allows service injection into test fields.
 * 
 * Example:
 * 
 * <pre>
 * &#064;TapestryModule( { StorageModule.class })
 * &#064;RunWith(UnitilsJUnit4TestClassRunner.class)
 * public class MyTest {
 *   &#064;TapestryService
 *   private MyService service;
 *   &#064;TapestryService
 *   private static MyService staticService;
 *   &#064;TapestrySymbol(&quot;SymbolSource-Symbol&quot;)
 *   private String value;
 * }
 * </pre>
 * 
 * To inject a marked service, just add the marker annotation to the field,
 * e.g.:
 * 
 * <pre>
 * &#064;TapestryService
 * &#064;MyServices
 * private MyService service;
 * </pre>
 * 
 * To inject the Tapestry {@link Registry} just add a field of type
 * {@link Registry}.
 * 
 * If you want to prevent that anything is injected at all (e.g. if you do not
 * want the Tapestry registry to be injected) add a {@link DoNotInject}
 * annotation.
 *
 * Test methods marked with {@link PrivateTapestryRegistry} get their own private
 * registry otherwise one registry per test class is created.
 * 
 * If you need to do to anything in your test (e.g. override some system properties)
 * you can implement a static method called <tt>initializeBeforeTapestryRegistry</tt>
 * which will be called before the registry is created.
 */
public class TapestryUnitilsModule implements Module {

	private static Logger logger = LoggerFactory.getLogger(TapestryUnitilsModule.class);

	private Registry registry;

	public void afterInit() {
		// PRI: TODO patch Unitils to support AfterClass!
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

	protected Registry createRegistryFor(Class<?> testClass) {
		RegistryBuilder builder = new RegistryBuilder();
		for (Class<?> module : getTapestryModulesFor(testClass)) {
			builder.add(module);
		}
		return builder.build();
	}

	protected List<Class<?>> getTapestryModulesFor(Class<?> testClass) {
		TapestryModule module = testClass.getAnnotation(TapestryModule.class);
		return module == null ? new ArrayList<Class<?>>() : Arrays.asList(module.value());
	}

	protected void createAndStartupRegistryFor(Class<?> testClass) {
		try {
			Method method = testClass.getMethod("initializeBeforeTapestryRegistry");
			method.invoke(null);
		} catch (Exception ignored) {
		}
		shutdownRegistry();
		if (logger.isDebugEnabled()) {
			logger.debug("starting tapestry registry for " + testClass);
		}
		registry = createRegistryFor(testClass);
		registry.performRegistryStartup();
	}

	/**
	 * @param testObject
	 *            Nullable. If null, then only static fields are injected.
	 */
	protected void injectTapestryStuff(Object testObject, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			injectServiceInto(testObject, field);
			injectSymbolsInto(testObject, field);
		}
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			injectTapestryStuff(testObject, superClazz);
		}
	}

	protected Object getServiceFor(TapestryService serviceAnnotation, final Field field) {
		Class<?> type = serviceAnnotation.type();
		if (type == Constants.UseFieldTypeAsServiceType.class) {
			type = field.getType();
		}
		if (Constants.NO_SERVICE_ID.equals(serviceAnnotation.id())) {
			return registry.getObject(type, new AnnotationProvider() {
				public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
					return field.getAnnotation(annotationClass);
				}
			});
		} else {
			return registry.getService(serviceAnnotation.id(), type);
		}

	}

	protected Object getSymbolFor(TapestrySymbol symbolAnnotation, Field field) {
		try {
			return registry.getService(SymbolSource.class).valueForSymbol(symbolAnnotation.value());
		} catch (RuntimeException ex) {
			if (symbolAnnotation.optional()) {
				return null;
			} else {
				throw ex;
			}
		}

	}

	/**
	 * @param testObject
	 *            Nullable. If null, then the service is only injected into
	 *            static fields.
	 */
	protected void injectServiceInto(Object testObject, Field field) {
		if (field.getAnnotation(DoNotInject.class) != null) {
			return;
		}

		boolean injectValue = false;
		Object valueToInject = null;
		boolean isStatic = Modifier.isStatic(field.getModifiers());
		if ((testObject == null && !isStatic) || (testObject != null && isStatic)) {
			return;
		}
		TapestryService serviceAnnotation = field.getAnnotation(TapestryService.class);
		if (serviceAnnotation != null) {
			valueToInject = getServiceFor(serviceAnnotation, field);
			injectValue = true;
		} else if (field.getType() == Registry.class) {
			valueToInject = registry;
			injectValue = true;
		}
		if (injectValue) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			try {
				field.set(testObject, valueToInject);
			} catch (Exception e) {
				throw new UnitilsException("cannot inject service into " + field, e);
			}
		}
	}

	/**
	 * @param testObject
	 *            Nullable. If null, then the symbol is only injected into
	 *            static fields.
	 */
	protected void injectSymbolsInto(Object testObject, Field field) {
		TapestrySymbol symbolAnnotation = field.getAnnotation(TapestrySymbol.class);
		if (symbolAnnotation != null) {
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			if ((testObject == null && !isStatic) || (testObject != null && isStatic)) {
				return;
			}
			Object service = getSymbolFor(symbolAnnotation, field);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			try {
				field.set(testObject, service);
			} catch (Exception e) {
				throw new UnitilsException("cannot inject service into " + field, e);
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
			createAndStartupRegistryFor(testClass);
			injectTapestryStuff(null, testClass);
		}

		@Override
		public void beforeTestSetUp(Object testObject, Method testMethod) {
			currentTestNeedsPrivateRegistry = testMethod.isAnnotationPresent(PrivateTapestryRegistry.class);
			if (currentTestNeedsPrivateRegistry) {
				sharedRegistry = registry;
				registry = null;
				createAndStartupRegistryFor(testObject.getClass());
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
