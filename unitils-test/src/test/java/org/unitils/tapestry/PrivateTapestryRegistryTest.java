package org.unitils.tapestry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.apache.tapestry5.ioc.Registry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.tapestry.annotation.DoNotInject;
import org.unitils.tapestry.annotation.PrivateTapestryRegistry;
import org.unitils.tapestry.annotation.TapestryModule;
import org.unitils.tapestry.annotation.TapestryService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@TapestryModule(Module.class)
public class PrivateTapestryRegistryTest {
	@DoNotInject
	private static Registry sharedRegistry;
	
	private static Service sharedService;

	private Registry registry;

	@TapestryService
	private Service testService;

	// hopefully run before testPrivateRegistry()
	@Test
	public void earlierTestMethod() {
		sharedRegistry = registry;
		sharedService = testService;
	}

	@Test
	@PrivateTapestryRegistry
	public void testMethodWithPrivateRegistry() {
		assertNotNull(sharedRegistry);
		assertNotNull(sharedService);
		assertNotSame(sharedRegistry, registry);
		assertNotSame(sharedService, testService);
	}

	// hopefully run after testPrivateRegistry()
	@Test
	public void laterTestMethod() {
		assertSame(sharedRegistry, registry);
		assertSame(sharedService, testService);
	}
}
