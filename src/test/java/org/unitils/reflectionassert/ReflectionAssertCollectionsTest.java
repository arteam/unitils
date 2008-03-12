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
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods with collection arguments.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertCollectionsTest {

    /* A test collection */
    List<String> listA;

    /* Same as listA but different instance */
    List<String> listB;

    /* Same as listA but with a different order */
    List<String> listDifferentOrder;

    /* A list having same size as listA but containing different values */
    List<String> listDifferentValues;

    /* A list containing 1 extra element as listA, a double of another element */
    List<String> listDuplicateElement;

    /* A list with one element less than listA */
    List<String> listOneElementLess;

    /* A list with one element more than listA */
    List<String> listOneElementMore;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        listA = Arrays.asList("el1", "el2");
        listB = Arrays.asList("el1", "el2");
        listDifferentOrder = Arrays.asList("el2", "el1");
        listDifferentValues = Arrays.asList("el2", "el3");
        listDuplicateElement = Arrays.asList("el2", "el2", "el1");
        listOneElementLess = Arrays.asList("el1");
        listOneElementMore = Arrays.asList("el1", "el2", "el3");
    }


    /**
     * Test for two equal collections.
     */
    @Test
    public void testAssertRefEquals() {
        assertRefEquals(listA, listB);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertRefEquals_notEqualsDifferentOrder() {
        try {
            assertRefEquals(listA, listDifferentOrder);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertRefEquals_equalsDifferentOrder() {
        assertRefEquals(listA, listDifferentOrder, LENIENT_ORDER);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertLenEquals_equalsDifferentOrder() {
        assertLenEquals(listA, listDifferentOrder);
    }


    /**
     * Test for two collections with different elements.
     */
    @Test
    public void testAssertEquals_differentListSameSize() {
        try {
            assertRefEquals(listA, listDifferentValues);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for a collection with a duplicate element.
     */
    @Test
    public void testAssertEquals_duplicateElement() {
        try {
            assertRefEquals(listA, listDuplicateElement);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for with a collection that has one element less.
     */
    @Test
    public void testAssertEquals_oneElementLess() {
        try {
            assertRefEquals(listA, listOneElementLess);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for with a collection that has one element more.
     */
    @Test
    public void testAssertEquals_oneElementMore() {
        try {
            assertRefEquals(listA, listOneElementMore);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }

}
