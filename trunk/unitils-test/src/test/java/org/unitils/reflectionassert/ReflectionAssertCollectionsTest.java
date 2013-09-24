/*
 * Copyright 2013,  Unitils.org
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

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods with collection arguments.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertCollectionsTest {

    /* A test collection */
    private List<String> listA;
    /* Same as listA but different instance */
    private List<String> listB;
    /* Same as listA but with a different order */
    private List<String> listDifferentOrder;
    /* A list having same size as listA but containing different values */
    private List<String> listDifferentValues;
    /* A list containing 1 extra element as listA, a double of another element */
    private List<String> listDuplicateElement;
    /* A list with one element less than listA */
    private List<String> listOneElementLess;
    /* A list with one element more than listA */
    private List<String> listOneElementMore;


    @Before
    public void initialize() throws Exception {
        listA = asList("el1", "el2");
        listB = asList("el1", "el2");
        listDifferentOrder = asList("el2", "el1");
        listDifferentValues = asList("el2", "el3");
        listDuplicateElement = asList("el2", "el2", "el1");
        listOneElementLess = asList("el1");
        listOneElementMore = asList("el1", "el2", "el3");
    }


    /**
     * Test for two equal collections.
     */
    @Test
    public void testAssertReflectionEquals() {
        assertReflectionEquals(listA, listB);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test(expected = AssertionError.class)
    public void testAssertReflectionEquals_notEqualsDifferentOrder() {
        assertReflectionEquals(listA, listDifferentOrder);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertReflectionEquals_equalsDifferentOrder() {
        assertReflectionEquals(listA, listDifferentOrder, LENIENT_ORDER);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertLenientEquals_equalsDifferentOrder() {
        assertLenientEquals(listA, listDifferentOrder);
    }


    /**
     * Test for two collections with different elements.
     */
    @Test(expected = AssertionError.class)
    public void testAssertEquals_differentListSameSize() {
        assertReflectionEquals(listA, listDifferentValues);
    }


    /**
     * Test for a collection with a duplicate element.
     */
    @Test(expected = AssertionError.class)
    public void testAssertEquals_duplicateElement() {
        assertReflectionEquals(listA, listDuplicateElement);
    }


    /**
     * Test for with a collection that has one element less.
     */
    @Test(expected = AssertionError.class)
    public void testAssertEquals_oneElementLess() {
        assertReflectionEquals(listA, listOneElementLess);
    }


    /**
     * Test for with a collection that has one element more.
     */
    @Test(expected = AssertionError.class)
    public void testAssertEquals_oneElementMore() {
        assertReflectionEquals(listA, listOneElementMore);
    }
}
