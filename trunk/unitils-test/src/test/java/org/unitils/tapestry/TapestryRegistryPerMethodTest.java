package org.unitils.tapestry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.tapestry.annotation.TapestryRegistry;

@RunWith(UnitilsJUnit4TestClassRunner.class)
@TapestryRegistry(Module.class)
public class TapestryRegistryPerMethodTest {
	private static Registry classRegistry;
	private static Person classTestService;

	@Inject
	private Registry registry;
	@Inject
	private Person testService;

	// this test must be executed before the actual test method
	@Test
	public void beforeRegistryPerTestMethod() {
		classRegistry = registry;
		classTestService = testService;
	}

	@Test
	@TapestryRegistry(Module.class)
	public void registryPerTestMethod() {
		assertNotNull(classRegistry);
		assertNotNull(classTestService);
		assertNotSame(classRegistry, registry);
		assertNotSame(classTestService, testService);
	}

	// this test must be executed before the actual test method
	@Test
	public void afterRegistryPerTestMethod() {
		assertSame(classRegistry, registry);
		assertSame(classTestService, testService);
	}
}
