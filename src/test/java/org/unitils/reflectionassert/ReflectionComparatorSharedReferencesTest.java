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

import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import org.unitils.reflectionassert.difference.Difference;

/**
 * Tests for cases that contain cycles and multiple references to the same instances.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionComparatorSharedReferencesTest extends TestCase {

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    /* Leafs in the test object tree */
    private References leaf1, leaf2;

    /* Same as leaf1 but different instance */
    private References leaf1Copy;

    /* References leaf1 2 times */
    private References doubleReferenced;

    /* References leaf1 and leaf1Copy, should be equal to doubleReferenced */
    private References equalToDoubleReferenced;

    /* References leaf1 and another leaf, should not be equal to doubleReferenced */
    private References notEqualToDoubleReferenced;

    /* References left1 2 times in a nested reference */
    private References nestedDoubleReferenced;

    /* References leaf1Copy 2 times, should be equal to nestedDoubleReferenced */
    private References equalToNestedDoubleReferenced;

    /* References leaf1 and another leaf, should not be equal to nestedDoubleReferenced */
    private References notEqualToNestedDoubleReferenced1;

    /* Contains a cycle */
    private References circularReferenced;

    /* Also contains a cycle but first references circularReferenced, should be equal to nestedDoubleReferenced */
    private References equalToCircularReferenced;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        reflectionComparator = createRefectionComparator();

        // Create circular reference
        leaf1 = new References("Leaf1", null, null);
        leaf2 = new References("Leaf2", null, null);
        leaf1Copy = new References("Leaf1", null, null);

        doubleReferenced = new References("Trunk", leaf1, leaf1);
        equalToDoubleReferenced = new References("Trunk", leaf1, leaf1Copy);
        notEqualToDoubleReferenced = new References("Trunk", leaf1, leaf2);

        nestedDoubleReferenced = new References("Trunk", leaf1, new References("Branch", leaf1, null));
        equalToNestedDoubleReferenced = new References("Trunk", leaf1Copy, new References("Branch", leaf1Copy, null));
        notEqualToNestedDoubleReferenced1 = new References("Trunk", leaf1Copy, new References("Branch", leaf2, null));

        circularReferenced = new References("Trunk", leaf1, null);
        circularReferenced.setRef2(circularReferenced);
        equalToCircularReferenced = new References("Trunk", leaf1, new References("Trunk", leaf1Copy, circularReferenced));
    }


    /**
     * Test for two equal objects referenced more than once.
     */
    public void testDoubleReferenced_equal() {
        Difference result = reflectionComparator.getAllDifferences(doubleReferenced, equalToDoubleReferenced);
        assertNull(result);
    }


    /**
     * Test for two different objects referenced more than once.
     */
    public void testDoubleReferenced_notEqual() {
        Difference result = reflectionComparator.getAllDifferences(doubleReferenced, notEqualToDoubleReferenced);
        assertNotNull(result);
    }


    /**
     * Test for two equal objects referenced more than once in a nested reference.
     */
    public void testNestedDoubleReferenced_equal() {
        Difference result = reflectionComparator.getAllDifferences(nestedDoubleReferenced, equalToNestedDoubleReferenced);
        assertNull(result);
    }


    /**
     * Test for two different objects referenced more than once in a nested reference.
     */
    public void testNestedDoubleReferenced_notEqual() {
        Difference result = reflectionComparator.getAllDifferences(nestedDoubleReferenced, notEqualToNestedDoubleReferenced1);
        assertNotNull(result);
    }


    /**
     * Test for two equal objects that contain a cycle.
     */
    public void testCircularReferenced_equal() {
        Difference result = reflectionComparator.getAllDifferences(circularReferenced, equalToCircularReferenced);
        assertNull(result);
    }


    /**
     * Test for two different objects of which the first contains a cycle.
     */
    public void testCircularReferenced_notEqual() {
        Difference result = reflectionComparator.getAllDifferences(circularReferenced, nestedDoubleReferenced);
        assertNotNull(result);
    }


    /**
     * Test class.
     */
    @SuppressWarnings({"unused", "UnusedDeclaration"})
    private static class References {

        private String name;

        private References ref1;

        private References ref2;

        public References(String name, References ref1, References ref2) {
            this.name = name;
            this.ref1 = ref1;
            this.ref2 = ref2;
        }

        public void setRef1(References ref1) {
            this.ref1 = ref1;
        }

        public void setRef2(References ref2) {
            this.ref2 = ref2;
        }
    }
}
