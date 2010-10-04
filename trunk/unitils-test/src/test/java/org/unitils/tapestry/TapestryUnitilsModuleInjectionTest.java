package org.unitils.tapestry;

import junit.framework.Assert;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.tapestry.annotation.RunBeforeTapestryRegistryIsCreated;
import org.unitils.tapestry.annotation.TapestryRegistry;

import static org.junit.Assert.*;

@TapestryRegistry(Module.class)
public class TapestryUnitilsModuleInjectionTest extends UnitilsJUnit4 {

    @Inject
    private static Person staticTestService;
    @Inject
    @Symbol("testSymbol")
    private static String staticTestSymbol;

    @Inject
    private Person testService;
    @InjectService("TestService")
    private Person testServiceById;
    @Inject
    @Symbol("testSymbol")
    private String testSymbol;

    @Inject
    private Registry registry;
    @Inject
    private static Registry staticRegistry;
    @Inject
    private ObjectLocator objectLocator;
    @Inject
    private static ObjectLocator staticObjectLocator;


    private Registry registryThatIsNotInjected;
    private Person serviceThatIsNotInjected;

    @Inject
    @DogMarker
    private Animal serviceWithMarker;
    @Inject
    @CatMarker
    private Animal serviceWithMarker2;

    private static boolean initializeBeforeTapestryRegistryCalled;

    @RunBeforeTapestryRegistryIsCreated
    public static void initializeBeforeTapestryRegistry() {
        initializeBeforeTapestryRegistryCalled = true;
    }

    @Test
    public void initializeBeforeTapestryRegistryIsCalled() {
        assertTrue(initializeBeforeTapestryRegistryCalled);
    }

    @Test
    public void injectServicesWithMarkers() {
        assertNotNull(serviceWithMarker);
        assertNotNull(serviceWithMarker2);
        assertFalse(serviceWithMarker == serviceWithMarker2);
    }

    @Test
    public void fieldsThatAreNotAnnotatedAreNotInjected() {
        assertNull(registryThatIsNotInjected);
        assertNull(serviceThatIsNotInjected);
    }

    @Test
    public void injectTapestryRegistry() {
        assertNotNull(registry);
    }

    @Test
    public void injectStaticTapestryRegistry() {
        assertNotNull(staticRegistry);
    }

    @Test
    public void injectTapestryRegistryAsObjectLocator() {
        assertNotNull(objectLocator);
    }

    @Test
    public void injectStaticTapestryRegistryAsObjectLocator() {
        assertNotNull(staticObjectLocator);
    }

    @Test
    public void injectStaticSymbol() {
        assertEquals("testSymbolValue", staticTestSymbol);
    }

    @Test
    public void injectSymbol() {
        assertEquals("testSymbolValue", testSymbol);
    }

    @Test
    public void injectStaticService() {
        Assert.assertNotNull(staticTestService);
    }

    @Test
    public void injectServiceByType() {
        Assert.assertNotNull(testService);
    }

    @Test
    public void injectServiceById() {
        Assert.assertNotNull(testServiceById);
    }

}
