package org.unitils.tapestry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4BlockTestClassRunner;
import org.unitils.tapestry.annotation.TapestryRegistry;

@RunWith(UnitilsJUnit4BlockTestClassRunner.class)
@TapestryRegistry(Module.class)
public class TapestryRegistryPerMethodTest {
	private static List<Registry> registries = new ArrayList<Registry>();

	@Inject
	private Registry registry;

	// this test must be executed before the actual test method
	@Test
	public void beforeRegistryPerTestMethod() {
		registries.add(registry);
	}

	@Test
	@TapestryRegistry(Module.class)
	public void registryPerTestMethod() {
		assertEquals(1, registries.size());
		assertNotSame(registries.get(0), registry);
		registries.add(registry);
	}

	// this test must be executed before the actual test method
	@Test
	public void afterRegistryPerTestMethod() {
		assertEquals(2, registries.size());
		assertNotSame(registries.get(0), registry);
		assertNotSame(registries.get(1), registry);
	}
}
