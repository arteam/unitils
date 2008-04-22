/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.core.ModulesLoader.PROPKEY_MODULES;
import static org.unitils.core.ModulesLoader.PROPKEY_MODULE_PREFIX;
import static org.unitils.core.ModulesLoader.PROPKEY_MODULE_SUFFIX_CLASS_NAME;
import static org.unitils.core.ModulesLoader.PROPKEY_MODULE_SUFFIX_ENABLED;
import static org.unitils.core.ModulesLoader.PROPKEY_MODULE_SUFFIX_RUN_AFTER;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

/**
 * Test for {@link ModulesLoader}.
 */
public class ModulesLoaderTest extends UnitilsJUnit4 {


	/* Class under test */
	private ModulesLoader modulesLoader;

	/* The unitils configuration settings that control the core loading */
	private Properties configuration;


	/**
	 * Creates the test instance and initializes the fixture.
	 */
	@Before
	public void setUp() throws Exception {
		modulesLoader = new ModulesLoader();

		configuration = new Properties();
		configuration.setProperty(PROPKEY_MODULES, "a, b, c, d");
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "a" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, TestModuleA.class.getName());
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "a" + PROPKEY_MODULE_SUFFIX_RUN_AFTER, "b, d");
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "b" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, TestModuleB.class.getName());
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "b" + PROPKEY_MODULE_SUFFIX_RUN_AFTER, "d");
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "c" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, TestModuleC.class.getName());
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "c" + PROPKEY_MODULE_SUFFIX_RUN_AFTER, "a");
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "d" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, TestModuleD.class.getName());
	}


	/**
	 * Test the loading of a normal configuration.
	 */
	@Test
	public void testLoadModules() {
		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertEquals(4, result.size());
		assertTrue(result.get(0) instanceof TestModuleD);
		assertTrue(result.get(1) instanceof TestModuleB);
		assertTrue(result.get(2) instanceof TestModuleA);
		assertTrue(result.get(3) instanceof TestModuleC);
	}


	/**
	 * Tests the loading with 1 core name left out: c. The c core should not have been loaded.
	 */
	@Test
	public void testLoadModules_notActive() {
		configuration.setProperty(PROPKEY_MODULES, "a, b, d");

		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertEquals(3, result.size());
		assertTrue(result.get(0) instanceof TestModuleD);
		assertTrue(result.get(1) instanceof TestModuleB);
		assertTrue(result.get(2) instanceof TestModuleA);
	}

	/**
	 * Tests the loading with core d disabled. The core should have been ignored
	 */
	@Test
	public void testLoadModules_notEnabled() {
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "d" + PROPKEY_MODULE_SUFFIX_ENABLED, "false");

		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertEquals(3, result.size());
		assertTrue(result.get(0) instanceof TestModuleB);
		assertTrue(result.get(1) instanceof TestModuleA);
		assertTrue(result.get(2) instanceof TestModuleC);
	}


	/**
	 * Tests the loading with modules (a, b) and dependencies (a -> b, d) that are declared twice. The doubles should
	 * have been ignored
	 */
	@Test
	public void testLoadModules_notDoubles() {
		configuration.setProperty(PROPKEY_MODULES, "a, b, c, d, a, b");
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "a" + PROPKEY_MODULE_SUFFIX_RUN_AFTER, "b, b, d, b, d");

		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertEquals(4, result.size());
		assertTrue(result.get(0) instanceof TestModuleD);
		assertTrue(result.get(1) instanceof TestModuleB);
		assertTrue(result.get(2) instanceof TestModuleA);
		assertTrue(result.get(3) instanceof TestModuleC);
	}


	/**
	 * Tests the loading with a totally empty configuration.
	 */
	@Test
	public void testLoadModules_emptyConfiguration() {
		configuration.clear();

		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}


	/**
	 * Tests the loading of a core that is configured with a class name for a class that is not a UnitilsModule. A
	 * warning should have been logged and the other modules should have been loaded.
	 */
	@Test
	public void testLoadModules_wrongClassName() {
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "a" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, "java.lang.String");
		try {
			modulesLoader.loadModules(configuration);
			fail();

		} catch (UnitilsException e) {
			// expected
		}
	}


	/**
	 * Tests the loading of a core that is configured with a class name for a class that does not exist. A warning
	 * should have been logged and the other modules should have been loaded.
	 */
	@Test
	public void testLoadModules_classNotFound() {
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "a" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, "xxxxx");

		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertEquals(3, result.size());
		assertTrue(result.get(0) instanceof TestModuleD);
		assertTrue(result.get(1) instanceof TestModuleB);
		assertTrue(result.get(2) instanceof TestModuleC);
	}


	/**
	 * Tests the loading of modules that contain a circular dependency. a must run after b must run after d must run
	 * after a A runtime exception should have been thrown.
	 */
	@Test
	public void testLoadModules_circular() {
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "b" + PROPKEY_MODULE_SUFFIX_RUN_AFTER, "c");
		try {
			modulesLoader.loadModules(configuration);
			fail();

		} catch (UnitilsException e) {
			// expected
		}
	}


	/**
	 * Tests the loading of a core that is configured with a class name for a class that has a private constructor.
	 */
	@Test
	public void testLoadModules_privateConstructor() {
		configuration.setProperty(PROPKEY_MODULE_PREFIX + "a" + PROPKEY_MODULE_SUFFIX_CLASS_NAME, TestModulePrivate.class.getName());

		List<Module> result = modulesLoader.loadModules(configuration);

		assertNotNull(result);
		assertEquals(4, result.size());
		assertTrue(result.get(0) instanceof TestModuleD);
		assertTrue(result.get(1) instanceof TestModuleB);
		assertTrue(result.get(2) instanceof TestModulePrivate);
		assertTrue(result.get(3) instanceof TestModuleC);
	}


	/**
	 * A test unitils core type
	 */
	public static class TestModuleA implements Module {

		public void init(Properties configuration) {
		}

		public void afterInit() {
		}

		public TestListener getTestListener() {
			return null;
		}
	}

	/**
	 * A test unitils core type
	 */
	public static class TestModuleB implements Module {

		public void init(Properties configuration) {
		}

		public void afterInit() {
		}

		public TestListener getTestListener() {
			return null;
		}
	}

	/**
	 * A test unitils core type
	 */
	public static class TestModuleC implements Module {

		public void init(Properties configuration) {
		}

		public void afterInit() {
		}

		public TestListener getTestListener() {
			return null;
		}
	}

	/**
	 * A test unitils core type
	 */
	public static class TestModuleD implements Module {

		public void init(Properties configuration) {
		}
		
		public void afterInit() {
		}

		public TestListener getTestListener() {
			return null;
		}
	}


	/**
	 * A test unitils core type having a private constructor
	 */
	public static class TestModulePrivate implements Module {

		public void init(Properties configuration) {
		}
		
		public void afterInit() {
		}

		private TestModulePrivate() {
		}

		public TestListener getTestListener() {
			return null;
		}
	}
}
