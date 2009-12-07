package org.unitils.tapestry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.tapestry.annotation.RunBeforeTapestryRegistryIsCreated;
import org.unitils.tapestry.annotation.TapestryRegistry;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@TapestryRegistry(Module.class)
public class TapestryUnitilsModuleTest {

	@Inject
	private static Service staticTestService;
	@Inject
	@Symbol("testSymbol")
	private static String staticTestSymbol;

	@Inject
	private Service testService;
	@InjectService("TestService")
	private Service testServiceById;
	@Inject
	@Symbol("testSymbol")
	private String testSymbol;

	@Inject
	private Registry registry;
	@Inject
	private static Registry staticRegistry;

	private Registry registryThatIsNotInjected;
	private Service serviceThatIsNotInjected;

	@Inject
	@ServiceMarker
	private Service2 serviceWithMarker;
	@Inject
	@ServiceMarker2
	private Service2 serviceWithMarker2;

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
		Assert.assertEquals("test", staticTestService.test());
	}

	@Test
	public void injectServiceByType() {
		Assert.assertNotNull(testService);
		Assert.assertEquals("test", testService.test());
	}

	@Test
	public void injectServiceById() {
		Assert.assertNotNull(testServiceById);
		Assert.assertEquals("test", ((Service) testServiceById).test());
	}

}
