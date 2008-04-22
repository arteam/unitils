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
package org.unitils.reflectionassert;

import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ReflectionAssert} tests for cyclic dependencies between collections.
 * <p/>
 * Thanks to contributions of mtowler
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertCyclicCollectionTest {

    /* Test object containing a collection that contains a loop */
    TestObject testObjectA;

    /* Same as testObjectA but different instance */
    TestObject testObjectB;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        testObjectA = createTestObject();
        testObjectB = createTestObject();
    }


    /**
     * Tests the comparison of objects containing the cyclic dependency.
     * This should pass and should not cause a StackOverflow.
     */
    @Test
    public void testAssertRefEquals_infiniteLoop() {
        assertRefEquals(testObjectA, testObjectB);
    }


    /**
     * Tests the comparison of objects containing the cyclic dependency.
     * This should pass and should not cause a StackOverflow.
     */
    @Test
    public void testAssertLenEquals_infiniteLoop() {
        assertLenEquals(testObjectA, testObjectB);
    }


    /**
     * Creates a test object that contains cyclic dependencies through collections.
     * <p/>
     * root  -> collection( leaf1, leaf2 )
     * leaf1 -> collection ( root )
     * leaf2 -> collection ( root )
     *
     * @return The test object, not null
     */
    private TestObject createTestObject() {
        TestObject root = new TestObject();
        TestObject leaf1 = new TestObject();
        TestObject leaf2 = new TestObject();

        root.getTestObjects().add(leaf1);
        root.getTestObjects().add(leaf2);
        leaf1.getTestObjects().add(root);
        leaf2.getTestObjects().add(root);
        return root;
    }


    /**
     * Test class with inner collection.
     */
    private static class TestObject {

        private Set<TestObject> testObjects;

        public TestObject() {
            this.testObjects = new HashSet<TestObject>();
        }

        public Set<TestObject> getTestObjects() {
            return testObjects;
        }
    }
}
