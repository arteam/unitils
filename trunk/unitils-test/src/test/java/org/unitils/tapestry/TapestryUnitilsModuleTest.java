package org.unitils.tapestry;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;
import org.unitils.tapestry.annotation.RunBeforeTapestryRegistryIsCreated;
import org.unitils.tapestry.annotation.TapestryRegistry;

public class TapestryUnitilsModuleTest {

	TapestryUnitilsModule module = new TapestryUnitilsModule();

	private void runBeforeClass(Class<?> testClass) {
		module.getTestListener().beforeTestClass(testClass);
	}

	@SuppressWarnings("unchecked")
	private <T> T runTest(Class<T> testClass, boolean runBeforeClass) {
		if (runBeforeClass) {
			runBeforeClass(testClass);
		}
		Object testObject;
		Method testMethod;
		try {
			testObject = testClass.newInstance();
			testMethod = testClass.getMethod("test");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		module.getTestListener().beforeTestSetUp(testObject, testMethod);
		module.getTestListener().afterTestTearDown(testObject, testMethod);
		return (T) testObject;
	}

	private <T> T runTest(T testObject) {
		return runTest(testObject, "test");
	}

	private <T> T runTest(T testObject, String methodName) {
		Method testMethod;
		try {
			testMethod = testObject.getClass().getMethod(methodName);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		module.getTestListener().beforeTestSetUp(testObject, testMethod);
		module.getTestListener().afterTestTearDown(testObject, testMethod);
		return (T) testObject;
	}

	@Test
	public void injectServiceIntoStaticField() {
		runBeforeClass(InjectIntoStaticFields.class);
		assertNotNull(InjectIntoStaticFields.service);
	}

	@Test
	public void injectRegistryIntoStaticField() {
		runBeforeClass(InjectIntoStaticFields.class);
		assertNotNull(InjectIntoStaticFields.registry);
	}

	@Test
	public void injectServiceWithIdIntoStaticField() {
		runBeforeClass(InjectIntoStaticFields.class);
		assertNotNull(InjectIntoStaticFields.serviceById);
		assertEquals("Cat", InjectIntoStaticFields.serviceById.getName());
	}

	@Test
	public void injectServiceWithMarkerIntoStaticField() {
		runBeforeClass(InjectIntoStaticFields.class);
		assertNotNull(InjectIntoStaticFields.serviceByMarker);
		assertEquals("Dog", InjectIntoStaticFields.serviceByMarker.getName());
	}

	@Test
	public void injectSymbolIntoStaticField() {
		runBeforeClass(InjectIntoStaticFields.class);
		assertEquals("testSymbolValue", InjectIntoStaticFields.testSymbol);
	}

	@Test
	public void notAnnotatedStaticFieldsAreNotInjected() {
		runBeforeClass(InjectIntoStaticFields.class);
		assertNull(InjectIntoStaticFields.notInjectedRegistry);
		assertNull(InjectIntoStaticFields.notInjectedService);
	}

	@Test
	public void injectServiceIntoStatic() {
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertNotNull(testObject.service);
	}

	@Test
	public void injectRegistryIntoField() {
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertNotNull(testObject.registry);
	}

	@Test
	public void injectServiceWithIdIntoField() {
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertNotNull(testObject.serviceById);
		assertEquals("Cat", InjectIntoStaticFields.serviceById.getName());
	}

	@Test
	public void injectServiceWithMarkerIntoField() {
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertNotNull(testObject.serviceByMarker);
		assertEquals("Dog", InjectIntoStaticFields.serviceByMarker.getName());
	}

	@Test
	public void injectSymbolIntoField() {
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertEquals("testSymbolValue", testObject.testSymbol);
	}

	@Test
	public void notAnnotatedFieldsAreNotInjected() {
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertNull(testObject.notInjectedRegistry);
		assertNull(testObject.notInjectedService);
	}

	@Test
	public void useCustomRegistryMethodWithStaticInjection() {
		runBeforeClass(RegistryMethodWithStaticInjections.class);
		assertNotNull(RegistryMethodWithStaticInjections.registry);
		assertSame(RegistryMethodWithStaticInjections.registry,
				RegistryMethodWithStaticInjections.injectedRegistry);
	}

	@Test
	public void useCustomRegistryMethodWithNonStaticInjection() {
		RegistryMethodWithNonStaticInjections testObject = runTest(
				RegistryMethodWithNonStaticInjections.class, true);
		assertNotNull(testObject.registry);
		assertSame(testObject.registry, testObject.injectedRegistry);
	}

	@Test
	public void useCustomStaticRegistryMethodWithNonStaticInjection() {
		StaticRegistryMethodWithNonStaticInjections testObject = runTest(
				StaticRegistryMethodWithNonStaticInjections.class, true);
		assertNotNull(StaticRegistryMethodWithNonStaticInjections.registry);
		assertSame(StaticRegistryMethodWithNonStaticInjections.registry,
				testObject.injectedRegistry);
	}

	@Test(expected = TapestryUnitilsModuleException.class)
	public void tryToUseNonStaticRegistryMethodWhenStaticInjectionIsRequired() {
		runBeforeClass(InvalidRegistryMethodWithStaticInjections.class);
	}

	@Test
	public void runBeforeTapestryRegistryCreationWithStaticInjection() {
		InjectIntoStaticFields.beforeRegistryCreatedCount = 0;
		runTest(InjectIntoStaticFields.class, true);
		assertEquals(1, InjectIntoStaticFields.beforeRegistryCreatedCount);
		// the method is not called anymore when tests are executed
		runTest(InjectIntoStaticFields.class, false);
		assertEquals(1, InjectIntoStaticFields.beforeRegistryCreatedCount);
	}

	@Test
	public void runBeforeTapestryRegistryCreationWithNonStaticInjection() {
		InjectIntoFields.staticBeforeRegistryCreatedCount = 0;
		InjectIntoFields testObject = runTest(InjectIntoFields.class, true);
		assertEquals(1, testObject.beforeRegistryCreatedCount);
		assertEquals(1, InjectIntoFields.staticBeforeRegistryCreatedCount);
		// the method is not called anymore when tests are executed
		runTest(testObject);
		assertEquals(1, testObject.beforeRegistryCreatedCount);
		assertEquals(1, InjectIntoFields.staticBeforeRegistryCreatedCount);
	}

	@Test(expected = TapestryUnitilsModuleException.class)
	public void tryToUseNonStaticRunBeforeRegistryCreationMethodWithStaticInjection() {
		runTest(InvalidRunBeforeMethodWithStaticInjections.class, true);
	}

	@Test
	public void useRegistryPerTest() {
		runBeforeClass(RegistryPerTest.class);
		// the static fields are not set because the registry is created for the
		// test method and not for the whole class
		assertNull(RegistryPerTest.staticRegistry);
		RegistryPerTest testObject = runTest(RegistryPerTest.class, false);
		assertNotNull(RegistryPerTest.staticRegistry);
		assertSame(RegistryPerTest.staticRegistry, testObject.registry);
		// the next test get's its own registry
		Registry firstTestRegistry = testObject.registry;
		runTest(testObject, "test2");
		assertNotNull(RegistryPerTest.staticRegistry);
		assertSame(RegistryPerTest.staticRegistry, testObject.registry);
		assertNotSame(firstTestRegistry, testObject.registry);
		Registry secondTestRegistry = testObject.registry;
		runTest(testObject, "testWithoutRegistry");
		// nothing was done - fields just keep their old values
		assertSame(RegistryPerTest.staticRegistry, secondTestRegistry);
		assertSame(testObject.registry, secondTestRegistry);
	}

	@Test
	public void useRegistryPerTestAndUseClassRegistryForAllNonAnnotatedTests() {
		runBeforeClass(RegistryPerTestWithClassRegistry.class);
		assertNotNull(RegistryPerTestWithClassRegistry.staticRegistry);
		Registry classRegistry = RegistryPerTestWithClassRegistry.staticRegistry;
		RegistryPerTestWithClassRegistry testObject = runTest(
				RegistryPerTestWithClassRegistry.class, false);
		assertNotNull(RegistryPerTestWithClassRegistry.staticRegistry);
		assertSame(RegistryPerTestWithClassRegistry.staticRegistry,
				testObject.registry);
		assertNotSame(classRegistry, testObject.registry);
		runTest(testObject, "testWithoutRegistry");
		// nothing was done - fields just keep their old values
		assertSame(classRegistry,
				RegistryPerTestWithClassRegistry.staticRegistry);
		assertSame(classRegistry, testObject.registry);
	}

	@Test
	public void useRegistryPerTestWithCustomRegistryMethod() {
		RegistryPerTestWithCustomRegistryMethod testObject = runTest(
				RegistryPerTestWithCustomRegistryMethod.class, true);
		assertNotNull(testObject.registry);
	}

	@Test
	public void useRegistryPerTestWithCustomStaticRegistryMethod() {
		runTest(RegistryPerTestWithCustomStaticRegistryMethod.class, true);
		assertNotNull(RegistryPerTestWithCustomStaticRegistryMethod.registry);
	}

	public static class RegistryPerTestWithCustomStaticRegistryMethod {

		private static Registry registry;

		public static Registry createRegistry(Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			registry.performRegistryStartup();
			return registry;
		}

		@TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
		public void test() {
		}
	}

	public static class RegistryPerTestWithCustomRegistryMethod {

		private Registry registry;

		public Registry createRegistry(Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			registry.performRegistryStartup();
			return registry;
		}

		@TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
		public void test() {
		}
	}

	@TapestryRegistry(Module.class)
	public static class RegistryPerTestWithClassRegistry {
		@Inject
		public static Registry staticRegistry;
		@Inject
		public Registry registry;

		@TapestryRegistry(Module.class)
		public void test() {
		}

		public void testWithoutRegistry() {
		}
	}

	public static class RegistryPerTest {

		@Inject
		public static Registry staticRegistry;
		@Inject
		public Registry registry;

		@TapestryRegistry(Module.class)
		public void test() {
		}

		@TapestryRegistry(Module.class)
		public void test2() {
		}

		public void testWithoutRegistry() {
		}
	}

	@TapestryRegistry(Module.class)
	public static class InvalidRunBeforeMethodWithStaticInjections {
		@SuppressWarnings("unused")
		@Inject
		private static Registry injectedRegistry;

		@RunBeforeTapestryRegistryIsCreated
		public void runBefore() {
			fail("won't be executed because static injection is required");
		}
	}
	
	@TapestryRegistry(Module.class)
	public static class PrivateRunBeforeMethod {
		@RunBeforeTapestryRegistryIsCreated
		protected static void runBefore() {
			fail("won't be executed because this method must be private");
		}
		
		public void test() {
		}
	}
	
	@Test(expected = TapestryUnitilsModuleException.class)
	public void runBeforeMethodsMustBePublic() {
		runTest(PrivateRunBeforeMethod.class, true);
	}
	
	

	@TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
	public static class InvalidRegistryMethodWithStaticInjections {
		@SuppressWarnings("unused")
		@Inject
		private static Registry injectedRegistry;

		public Registry createRegistry(Class<?>[] modules) {
			fail("won't be called because static injection is required");
			return null;
		}
	}

	@TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
	public static class RegistryMethodWithStaticInjections {

		private static Registry registry;
		@Inject
		private static Registry injectedRegistry;

		public static Registry createRegistry(Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			registry.performRegistryStartup();
			return registry;
		}

		public void test() {
		}
	}

	@TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
	public static class RegistryMethodWithNonStaticInjections {

		private Registry registry;
		@Inject
		private Registry injectedRegistry;

		public Registry createRegistry(Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			registry.performRegistryStartup();
			return registry;
		}

		public void test() {
		}
	}

	@TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
	public static class StaticRegistryMethodWithNonStaticInjections {

		private static Registry registry;
		@Inject
		private Registry injectedRegistry;

		public static Registry createRegistry(Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			registry.performRegistryStartup();
			return registry;
		}

		public void test() {
		}
	}

	@TapestryRegistry(Module.class)
	public static class InjectIntoFields {
		private Person notInjectedService;
		private Registry notInjectedRegistry;

		@Inject
		private Registry registry;
		@Inject
		private Person service;
		@InjectService("cat")
		@CatMarker
		private Animal serviceById;
		@Inject
		@DogMarker
		private Animal serviceByMarker;
		@Inject
		@Symbol("testSymbol")
		private String testSymbol;

		private static int staticBeforeRegistryCreatedCount = 0;
		private int beforeRegistryCreatedCount = 0;

		@RunBeforeTapestryRegistryIsCreated
		public static void staticBeforeRegistryCreated() {
			staticBeforeRegistryCreatedCount++;
		}

		@RunBeforeTapestryRegistryIsCreated
		public void beforeRegistryCreated() {
			beforeRegistryCreatedCount++;
		}

		public void test() {
		}
	}

	@TapestryRegistry(Module.class)
	public static class InjectIntoStaticFields {
		private static Person notInjectedService;
		private static Registry notInjectedRegistry;

		@Inject
		private static Registry registry;
		@Inject
		private static Person service;
		@InjectService("cat")
		@CatMarker
		private static Animal serviceById;
		@Inject
		@DogMarker
		private static Animal serviceByMarker;
		@Inject
		@Symbol("testSymbol")
		private static String testSymbol;

		private static int beforeRegistryCreatedCount = 0;

		@RunBeforeTapestryRegistryIsCreated
		public static void beforeRegistryCreated() {
			beforeRegistryCreatedCount++;
		}

		public void test() {
		}
	}

	@TapestryRegistry(Module.class)
	public static class InvalidBeforeRegistryCreatedMethod {
		@SuppressWarnings("unused")
		@RunBeforeTapestryRegistryIsCreated
		private static void beforeRegistryCreated() {
			fail("won't be called because it is not public");
		}

		public void test() {
		}
	}

	public static class InjectBase {
		@Inject
		public Person baseService;
		@Inject
		public static Person staticBaseService;
		
		private static List<String> runBeforeCalls = new ArrayList<String>();
		
		@RunBeforeTapestryRegistryIsCreated
		public static void runBeforeTapestryRegistryIsCreated() {
			runBeforeCalls.add("1");
		}
		
	}

	@TapestryRegistry(Module.class)
	public static class InjectDerived extends InjectBase {
		@Inject
		public Person derivedService;
		@Inject
		public static Person staticDerivedService;
		
		@RunBeforeTapestryRegistryIsCreated
		public static void runBeforeTapestryRegistryIsCreated() {
			InjectBase.runBeforeCalls.add("2");
		}

		public void test() {
		}
	}
	
	public static abstract class InjectWithOverwrittenRunBeforeMethodsBase {
		protected List<String> runBeforeCalls = new ArrayList<String>();
		
		@RunBeforeTapestryRegistryIsCreated
		public void runBeforeTapestryRegistryIsCreated() {
			runBeforeCalls.add("1");
		}
	}
	
	@TapestryRegistry(Module.class)
	public static class InjectWithOverwrittenRunBeforeMethods extends InjectWithOverwrittenRunBeforeMethodsBase {
		@RunBeforeTapestryRegistryIsCreated
		public void runBeforeTapestryRegistryIsCreated() {
			runBeforeCalls.add("2");
		}
		
		public void test() {
		}
	}
	
	@Test
	public void runBeforeMethodsCanBeOverwritten() {
		InjectWithOverwrittenRunBeforeMethods testObject = runTest(InjectWithOverwrittenRunBeforeMethods.class, true);
		assertEquals(Arrays.asList("2"), testObject.runBeforeCalls);
	}

	@Test
	public void runBeforeCallsAreExecutedInHierarchyOrder() {
		InjectBase.runBeforeCalls = new ArrayList<String>();
		runTest(InjectDerived.class, true);
		assertEquals(Arrays.asList("1", "2"), InjectBase.runBeforeCalls);
	}
	
	@TapestryRegistry(value = {}, registryFactoryMethodName = "createRegistry", registryFactoryMethodParameter = "test")
	public static class CustomRegistryFactoryMethodWithParameters {
		private Registry registry;
		private String arguments;
		@Inject
		private Registry injectedRegistry;

		public Registry createRegistry(String parameter, Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			arguments = parameter;
			return registry;
		}

		public void test() {
		}
	}

	@TapestryRegistry(value = {}, registryFactoryMethodName = "createRegistry")
	public static class NonStaticMethods {

		private Registry registry;
		@Inject
		private Registry injectedRegistry;
		private boolean runBeforeTapestryCreationCalled;

		@RunBeforeTapestryRegistryIsCreated
		public void runBeforeTapestryCreation() {
			runBeforeTapestryCreationCalled = true;
		}

		public Registry createRegistry(Class<?>[] modules) {
			registry = new RegistryBuilder().add(modules).build();
			return registry;
		}

		public void test() {
		}
	}

	@Test
	public void nonStaticMethodsCanBeUsedIfNoStaticInjectionIsRequired() {
		NonStaticMethods testObject = runTest(NonStaticMethods.class, true);
		assertNotNull(testObject.injectedRegistry);
		assertSame(testObject.injectedRegistry, testObject.registry);
		assertTrue(testObject.runBeforeTapestryCreationCalled);
	}

	@Test
	public void passParmetersToRegistryFactoryMethod() {
		CustomRegistryFactoryMethodWithParameters testObject = runTest(
				CustomRegistryFactoryMethodWithParameters.class, true);
		assertNotNull(testObject.injectedRegistry);
		assertSame(testObject.injectedRegistry, testObject.registry);
		assertEquals("test", testObject.arguments);
	}

	@Test
	public void injectionIsDoneThroughHierarchy() {
		InjectDerived testObject = runTest(InjectDerived.class, true);
		assertNotNull(testObject.baseService);
		assertNotNull(testObject.derivedService);
		assertNotNull(InjectBase.staticBaseService);
		assertNotNull(InjectDerived.staticDerivedService);
		assertSame(testObject.baseService, testObject.derivedService);
		assertSame(testObject.baseService, InjectBase.staticBaseService);
		assertSame(testObject.baseService, InjectDerived.staticDerivedService);
	}

}
