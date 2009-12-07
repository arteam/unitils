package org.unitils.tapestry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.tapestry.annotation.TapestryRegistry;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@TapestryRegistry(Module.class)
public class TapestryRegistryPerMethodTest {
	@Inject
	private static Registry sharedRegistry;
	@Inject
	private static Service sharedService;

	@Inject
	private Registry registry;
	@Inject
	private Service testService;

	@Test
	@TapestryRegistry(Module.class)
	public void registryPerTestMethod() {
		assertNotNull(sharedRegistry);
		assertNotNull(sharedService);
		assertNotSame(sharedRegistry, registry);
		assertNotSame(sharedService, testService);
	}

}
