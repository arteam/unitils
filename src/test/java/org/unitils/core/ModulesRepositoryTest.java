/*
 * Copyright 2006 the original author or authors.
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

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.unitils.db.DatabaseModule;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Arrays;
import java.util.List;

/**
 * Test for {@link ModulesRepositoryTest}.
 */
public class ModulesRepositoryTest extends TestCase {


    /* A test module */
    private Module testModule1 = new TestModule1();

    /* Another test module */
    private Module testModule2a = new TestModule2();

    /* A test module with same type as testModule2a */
    private Module testModule2b = new TestModule2();

    /* Class under test */
    private ModulesRepository modulesRepository;


    /**
     * Sets up the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        List<Module> modules = Arrays.asList(testModule2a, testModule1, testModule2b);
        modulesRepository = new ModulesRepository(modules);
    }


    /**
     * Test initialisation of repository and creation of all test listeners for the modules.
     */
    public void testCreateListeners() {

        assertEquals(3, modulesRepository.getTestListeners().size());
        assertTrue(modulesRepository.getTestListener(testModule1) instanceof TestModule1.TestListener1);
        assertTrue(modulesRepository.getTestListener(testModule2a) instanceof TestModule2.TestListener2);
        assertTrue(modulesRepository.getTestListener(testModule2b) instanceof TestModule2.TestListener2);
    }


    /**
     * Tests getting the first module of type TestModule2
     */
    public void testGetFirstModule() {

        TestModule2 result = modulesRepository.getFirstModule(TestModule2.class);
        assertLenEquals(testModule2a, result);
    }

    /**
     * Tests getting the first module of type TestModule1. Note: TestModule2 is a sub-type of TestModule1 and
     * should be found first.
     */
    public void testGetFirstModule_subType() {

        TestModule1 result = modulesRepository.getFirstModule(TestModule1.class);
        assertLenEquals(testModule2a, result);
    }


    /**
     * Tests getting the first module of type DatabaseModule, but none found. Null should be returned.
     */
    public void testGetFirstModule_noneFound() {

        DatabaseModule result = modulesRepository.getFirstModule(DatabaseModule.class);
        assertNull(result);
    }


    /**
     * Tests getting all modules of type TestModule2.
     */
    public void testGetModules() {

        List<TestModule2> result = modulesRepository.getModules(TestModule2.class);
        assertLenEquals(Arrays.asList(testModule2a, testModule2b), result);
    }


    /**
     * Tests getting all modules of type TestModule1. Note: TestModule2 is a sub-type of TestModule1 and
     * should also be found.
     */
    public void testGetModules_subType() {

        List<TestModule1> result = modulesRepository.getModules(TestModule1.class);
        assertLenEquals(Arrays.asList(testModule1, testModule2a, testModule2b), result);
    }


    /**
     * Tests getting all module of type DatabaseModule, but none found. An empty list should be returned.
     */
    public void testGetModules_noneFound() {

        List<DatabaseModule> result = modulesRepository.getModules(DatabaseModule.class);
        assertTrue(result.isEmpty());
    }


    /**
     * A test module, creating its own test listener.
     */
    private static class TestModule1 implements Module {

        public void init(Configuration configuration) {
        }

        public TestListener createTestListener() {
            return new TestListener1();
        }

        public static class TestListener1 extends TestListener {
        }
    }


    /**
     * A test module that is a subtype of TestModule1 and also creates its own test listener.
     */
    private static class TestModule2 extends TestModule1 implements Module {

        public TestListener createTestListener() {
            return new TestListener2();
        }

        public static class TestListener2 extends TestListener {
        }
    }


}
