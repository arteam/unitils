package org.unitils.tapestry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.apache.tapestry5.ioc.Registry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.tapestry.annotation.DoNotInject;
import org.unitils.tapestry.annotation.TapestryModule;
import org.unitils.tapestry.annotation.TapestryService;
import org.unitils.tapestry.annotation.TapestrySymbol;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@TapestryModule(Module.class)
public class TapestryUnitilsModuleTest {

	@TapestryService
	private static Service staticTestService;
	@TapestrySymbol("testSymbol")
	private static String staticTestSymbol;

	@TapestryService
	private Service testService;
	@TapestryService(type = Service.class)
	private Object testServiceAsObject;
	@TapestryService(type = Service.class, id = "TestService")
	private Object testServiceAsObjectById;
	@TapestrySymbol("testSymbol")
	private String testSymbol;
	@TapestrySymbol(value = "noSuchSymbol", optional = true)
	private String noSuchSymbol;

	private Registry registry;
	private static Registry staticRegistry;
	@DoNotInject
	private Registry registryThatIsNotInjected;

	@TapestryService
	@ServiceMarker
	private Service2 serviceWithMarker;
	@TapestryService
	@ServiceMarker2
	private Service2 serviceWithMarker2;
	
	private static boolean initializeBeforeTapestryRegistryCalled;
	
	public static void initializeBeforeTapestryRegistry() {
		initializeBeforeTapestryRegistryCalled = true;
	}
	
	@Test
	public void testInitializeBeforeTapestryRegistryIsCalled() {
		assertTrue(initializeBeforeTapestryRegistryCalled);
	}
	
	
	@Test
	public void testMarkedServices() {
		assertNotNull(serviceWithMarker);
		assertNotNull(serviceWithMarker2);
		assertFalse(serviceWithMarker == serviceWithMarker2);
	}
	
	@Test
	public void testNoSuchSymbolIsOptional() {
		assertNull(noSuchSymbol);
	}

	@Test
	public void testDoNotInject() {
		assertNull(registryThatIsNotInjected);
	}

	@Test
	public void testRegistryIsInjected() {
		assertNotNull(registry);
	}

	@Test
	public void testStaticRegistryIsInjected() {
		assertNotNull(staticRegistry);
	}

	@Test
	public void testStaticTestSymbolIsInjected() {
		assertEquals("testSymbolValue", staticTestSymbol);
	}

	@Test
	public void testTestSymbolIsInjected() {
		assertEquals("testSymbolValue", testSymbol);
	}

	@Test
	public void testStaticTestServiceIsInjectd() {
		Assert.assertNotNull(staticTestService);
		Assert.assertEquals("test", staticTestService.test());
	}

	@Test
	public void testServiceByTypeIsInjected() {
		Assert.assertNotNull(testService);
		Assert.assertEquals("test", testService.test());
	}

	@Test
	public void testServiceByTypeIsInjectedIntoObject() {
		Assert.assertNotNull(testServiceAsObject);
		Assert.assertEquals("test", ((Service) testServiceAsObject).test());
	}

	@Test
	public void testServiceByTypeAndIdIsInjectedIntoObject() {
		Assert.assertNotNull(testServiceAsObjectById);
		Assert.assertEquals("test", ((Service) testServiceAsObjectById).test());
	}

}
