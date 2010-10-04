package org.unitils.tapestry;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.easymock.EasyMock;
import org.junit.Test;
import org.unitils.tapestry.annotation.RunBeforeTapestryRegistryIsCreated;
import org.unitils.tapestry.annotation.TapestryRegistry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static org.junit.Assert.*;

public class TapestryUnitilsModuleTest {

    private TapestryUnitilsModule module = new TapestryUnitilsModule();

    private void runBeforeClass(Class<?> testClass) throws Exception {
        module.getTestListener().beforeTestClass(testClass, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T runTest(Class<T> testClass, boolean runBeforeClass, String methodName) throws Exception {
        if (runBeforeClass) {
            runBeforeClass(testClass);
        }
        Object testObject;
        Method testMethod;
        try {
            testObject = testClass.newInstance();
            testMethod = testClass.getMethod(methodName == null ? "test" : methodName);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        module.getTestListener().beforeTestMethod(testObject, testMethod, null);
        module.getTestListener().afterTestMethod(testObject, testMethod, null, null);
        return (T) testObject;
    }

    private <T> T runTest(T testObject) throws Exception {
        return runTest(testObject, "test");
    }

    private <T> T runTest(T testObject, String methodName) throws Exception {
        Method testMethod;
        try {
            testMethod = testObject.getClass().getMethod(methodName);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        module.getTestListener().beforeTestMethod(testObject, testMethod, null);
        module.getTestListener().afterTestMethod(testObject, testMethod, null, null);
        return (T) testObject;
    }

    @Test
    public void injectServiceIntoStaticField() throws Exception {
        runBeforeClass(InjectIntoStaticFields.class);
        assertNotNull(InjectIntoStaticFields.service);
    }

    @Test
    public void injectRegistryIntoStaticField() throws Exception {
        runBeforeClass(InjectIntoStaticFields.class);
        assertNotNull(InjectIntoStaticFields.registry);
    }

    @Test
    public void injectServiceWithIdIntoStaticField() throws Exception {
        runBeforeClass(InjectIntoStaticFields.class);
        assertNotNull(InjectIntoStaticFields.serviceById);
        assertEquals("Cat", InjectIntoStaticFields.serviceById.getName());
    }

    @Test
    public void injectServiceWithMarkerIntoStaticField() throws Exception {
        runBeforeClass(InjectIntoStaticFields.class);
        assertNotNull(InjectIntoStaticFields.serviceByMarker);
        assertEquals("Dog", InjectIntoStaticFields.serviceByMarker.getName());
    }

    @Test
    public void injectSymbolIntoStaticField() throws Exception {
        runBeforeClass(InjectIntoStaticFields.class);
        assertEquals("testSymbolValue", InjectIntoStaticFields.testSymbol);
    }

    @Test
    public void notAnnotatedStaticFieldsAreNotInjected() throws Exception {
        runBeforeClass(InjectIntoStaticFields.class);
        assertNull(InjectIntoStaticFields.notInjectedRegistry);
        assertNull(InjectIntoStaticFields.notInjectedService);
    }

    @Test
    public void injectServiceIntoStatic() throws Exception {
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertNotNull(testObject.service);
    }

    @Test
    public void injectRegistryIntoField() throws Exception {
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertNotNull(testObject.registry);
    }

    @Test
    public void injectServiceWithIdIntoField() throws Exception {
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertNotNull(testObject.serviceById);
        assertEquals("Cat", InjectIntoStaticFields.serviceById.getName());
    }

    @Test
    public void injectServiceWithMarkerIntoField() throws Exception {
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertNotNull(testObject.serviceByMarker);
        assertEquals("Dog", InjectIntoStaticFields.serviceByMarker.getName());
    }

    @Test
    public void injectSymbolIntoField() throws Exception {
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertEquals("testSymbolValue", testObject.testSymbol);
    }

    @Test
    public void notAnnotatedFieldsAreNotInjected() throws Exception {
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertNull(testObject.notInjectedRegistry);
        assertNull(testObject.notInjectedService);
    }

    @Test
    public void useCustomRegistryMethodWithStaticInjection() throws Exception {
        runBeforeClass(RegistryMethodWithStaticInjections.class);
        assertNotNull(RegistryMethodWithStaticInjections.registry);
        assertSame(RegistryMethodWithStaticInjections.registry, RegistryMethodWithStaticInjections.injectedRegistry);
    }

    @Test
    public void useCustomRegistryMethodWithNonStaticInjection() throws Exception {
        RegistryMethodWithNonStaticInjections testObject = runTest(RegistryMethodWithNonStaticInjections.class, true, null);
        assertNotNull(testObject.registry);
        assertSame(testObject.registry, testObject.injectedRegistry);
    }

    @Test
    public void useCustomStaticRegistryMethodWithNonStaticInjection() throws Exception {
        StaticRegistryMethodWithNonStaticInjections testObject = runTest(StaticRegistryMethodWithNonStaticInjections.class, true, null);
        assertNotNull(StaticRegistryMethodWithNonStaticInjections.registry);
        assertSame(StaticRegistryMethodWithNonStaticInjections.registry, testObject.injectedRegistry);
    }

    @Test(expected = TapestryUnitilsModuleException.class)
    public void tryToUseNonStaticRegistryMethodWhenStaticInjectionIsRequired() throws Exception {
        runBeforeClass(InvalidRegistryMethodWithStaticInjections.class);
    }

    @Test
    public void runBeforeTapestryRegistryCreationWithStaticInjection() throws Exception {
        InjectIntoStaticFields.beforeRegistryCreatedCount = 0;
        runTest(InjectIntoStaticFields.class, true, null);
        assertEquals(1, InjectIntoStaticFields.beforeRegistryCreatedCount);
        // the method is not called anymore when tests are executed
        runTest(InjectIntoStaticFields.class, false, null);
        assertEquals(1, InjectIntoStaticFields.beforeRegistryCreatedCount);
    }

    @Test
    public void runBeforeTapestryRegistryCreationWithNonStaticInjection() throws Exception {
        InjectIntoFields.staticBeforeRegistryCreatedCount = 0;
        InjectIntoFields testObject = runTest(InjectIntoFields.class, true, null);
        assertEquals(1, testObject.beforeRegistryCreatedCount);
        assertEquals(1, InjectIntoFields.staticBeforeRegistryCreatedCount);
        // the method is not called anymore when tests are executed
        runTest(testObject);
        assertEquals(1, testObject.beforeRegistryCreatedCount);
        assertEquals(1, InjectIntoFields.staticBeforeRegistryCreatedCount);
    }

    @Test(expected = TapestryUnitilsModuleException.class)
    public void tryToUseNonStaticRunBeforeRegistryCreationMethodWithStaticInjection() throws Exception {
        runTest(InvalidRunBeforeMethodWithStaticInjections.class, true, null);
    }

    @Test
    public void useRegistryPerTest() throws Exception {
        runBeforeClass(RegistryPerTest.class);
        // the static fields are not set because the registry is created for the
        // test method and not for the whole class
        assertNull(RegistryPerTest.staticRegistry);
        RegistryPerTest testObject = runTest(RegistryPerTest.class, false, null);
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
    public void useRegistryPerTestAndUseClassRegistryForAllNonAnnotatedTests() throws Exception {
        Registry lastRegistry = null;

        runBeforeClass(RegistryPerTestWithClassRegistry.class);

        RegistryPerTestWithClassRegistry testObject = runTest(RegistryPerTestWithClassRegistry.class, false, "testWithoutRegistry1");
        assertNotNull(testObject.registry);
        lastRegistry = testObject.registry;

        runTest(testObject, "testWithRegistry");
        assertNotNull(testObject.registry);
        assertNotSame(lastRegistry, testObject.registry);
        lastRegistry = testObject.registry;

        runTest(testObject, "testWithoutRegistry2");
        assertNotNull(testObject.registry);
        assertNotSame(lastRegistry, testObject.registry);
    }

    @Test
    public void useRegistryPerTestWithCustomRegistryMethod() throws Exception {
        RegistryPerTestWithCustomRegistryMethod testObject = runTest(RegistryPerTestWithCustomRegistryMethod.class, true, null);
        assertNotNull(testObject.registry);
        // assert that the registry is shut down
        EasyMock.verify(testObject.registry);
    }

    @Test
    public void useRegistryPerTestWithCustomRegistryAndRegistryShutdownMethod() throws Exception {
        RegistryPerTestWithCustomRegistryAndRegistryShutdownMethod testObject = runTest(RegistryPerTestWithCustomRegistryAndRegistryShutdownMethod.class, true,
                null);
        assertNotNull(testObject.registry);
        assertTrue(testObject.shutdownRegistryCalled);
        // assert that the registry is shut down
        EasyMock.verify(testObject.registry);
    }

    @Test
    public void useRegistryPerTestWithCustomStaticRegistryMethod() throws Exception {
        runTest(RegistryPerTestWithCustomStaticRegistryMethod.class, true, null);
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
            registry = EasyMock.createMock(Registry.class);
            registry.shutdown();
            EasyMock.replay(registry);
            return registry;
        }

        @TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry")
        public void test() {
        }
    }

    public static class RegistryPerTestWithCustomRegistryAndRegistryShutdownMethod {

        private Registry registry;
        private boolean shutdownRegistryCalled;

        public Registry createRegistry(Class<?>[] modules) {
            registry = EasyMock.createMock(Registry.class);
            registry.shutdown();
            EasyMock.replay(registry);
            return registry;
        }

        public void shutdownRegistry(Registry registry) {
            shutdownRegistryCalled = true;
            registry.shutdown();
        }

        @TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry", registryShutdownMethodName = "shutdownRegistry")
        public void test() {
        }
    }

    @TapestryRegistry(value = Module.class, registryFactoryMethodName = "createRegistry", registryShutdownMethodName = "shutdownRegistry")
    public static class RegistryPerClassWithCustomRegistryAndRegistryShutdownMethod {
        private Registry registry;
        private boolean shutdownRegistryCalled;

        public Registry createRegistry(Class<?>[] modules) {
            registry = EasyMock.createMock(Registry.class);
            registry.shutdown();
            EasyMock.replay(registry);
            return registry;
        }

        public void shutdownRegistry(Registry registry) {
            shutdownRegistryCalled = true;
            registry.shutdown();
        }

        public void test() {
        }
    }

    public static class TestWithoutTapestrySupport {
        public void test() {
        }
    }

    @Test
    public void useRegistryPerClassWithCustomRegistryAndRegistryShutdownMethod() throws Exception {
        RegistryPerClassWithCustomRegistryAndRegistryShutdownMethod testObject = runTest(RegistryPerClassWithCustomRegistryAndRegistryShutdownMethod.class,
                true, null);
        assertNotNull(testObject.registry);
        // assert that the registry is not shut down (registry will be shut down
        // when the next test executes ...)
        assertFalse(testObject.shutdownRegistryCalled);
        runTest(TestWithoutTapestrySupport.class, true, null);
        assertTrue(testObject.shutdownRegistryCalled);
        EasyMock.verify(testObject.registry);
    }

    @TapestryRegistry(Module.class)
    public static class RegistryPerTestWithClassRegistry {
        @Inject
        public Registry registry;

        public void testWithoutRegistry1() {
        }

        @TapestryRegistry(Module.class)
        public void testWithRegistry() {
        }

        public void testWithoutRegistry2() {
        }

    }

    @TapestryRegistry(value = {Module.class}, createRegistryPerTest = true)
    public static class AutoRegistryPerTest {
        @Inject
        public Registry registry;

        public void test1() {
        }

        public void test2() {
        }
    }

    @Test
    public void useRegistryPerTestWithClassAnnotationOnly() throws Exception {
        Registry lastRegistry = null;

        AutoRegistryPerTest testObject = runTest(AutoRegistryPerTest.class, true, "test1");
        assertNotNull(testObject.registry);
        lastRegistry = testObject.registry;

        runTest(testObject, "test2");
        assertNotNull(testObject.registry);
        assertNotSame(lastRegistry, testObject.registry);
    }

    @TapestryRegistry(value = {Module.class}, createRegistryPerTest = true, registryFactoryMethodName = "createRegistry", registryShutdownMethodName = "shutdownRegistry")
    public static class AutoRegistryPerTestWithCustomRegistryAndRegistryShutdownMethods {
        @Inject
        public Registry registry;
        public int shutdownRegistryCalls;

        public Registry createRegistry(Class<?>... modules) {
            return EasyMock.createNiceMock(Registry.class);
        }

        public void shutdownRegistry(Registry registry) {
            shutdownRegistryCalls++;
        }

        public void test1() {
        }

        public void test2() {
        }
    }

    @Test
    public void checkRegistryShutdownWithRegistryPerTestAndClassAnnotationOnly() throws Exception {
        Registry lastRegistry = null;

        AutoRegistryPerTestWithCustomRegistryAndRegistryShutdownMethods testObject = runTest(AutoRegistryPerTestWithCustomRegistryAndRegistryShutdownMethods.class, true, "test1");
        assertNotNull(testObject.registry);
        assertEquals(1, testObject.shutdownRegistryCalls);
        lastRegistry = testObject.registry;

        runTest(testObject, "test2");
        assertNotNull(testObject.registry);
        assertNotSame(lastRegistry, testObject.registry);
        assertEquals(2, testObject.shutdownRegistryCalls);
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
    public void runBeforeMethodsMustBePublic() throws Exception {
        runTest(PrivateRunBeforeMethod.class, true, null);
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
    public void runBeforeMethodsCanBeOverwritten() throws Exception {
        InjectWithOverwrittenRunBeforeMethods testObject = runTest(InjectWithOverwrittenRunBeforeMethods.class, true, null);
        assertEquals(Arrays.asList("2"), testObject.runBeforeCalls);
    }

    @Test
    public void runBeforeCallsAreExecutedInHierarchyOrder() throws Exception {
        InjectBase.runBeforeCalls = new ArrayList<String>();
        runTest(InjectDerived.class, true, null);
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
    public void nonStaticMethodsCanBeUsedIfNoStaticInjectionIsRequired() throws Exception {
        NonStaticMethods testObject = runTest(NonStaticMethods.class, true, null);
        assertNotNull(testObject.injectedRegistry);
        assertSame(testObject.injectedRegistry, testObject.registry);
        assertTrue(testObject.runBeforeTapestryCreationCalled);
    }

    @Test
    public void passParmetersToRegistryFactoryMethod() throws Exception {
        CustomRegistryFactoryMethodWithParameters testObject = runTest(CustomRegistryFactoryMethodWithParameters.class, true, null);
        assertNotNull(testObject.injectedRegistry);
        assertSame(testObject.injectedRegistry, testObject.registry);
        assertEquals("test", testObject.arguments);
    }

    @Test
    public void injectionIsDoneThroughHierarchy() throws Exception {
        InjectDerived testObject = runTest(InjectDerived.class, true, null);
        assertNotNull(testObject.baseService);
        assertNotNull(testObject.derivedService);
        assertNotNull(InjectBase.staticBaseService);
        assertNotNull(InjectDerived.staticDerivedService);
        assertSame(testObject.baseService, testObject.derivedService);
        assertSame(testObject.baseService, InjectBase.staticBaseService);
        assertSame(testObject.baseService, InjectDerived.staticDerivedService);
    }

}
