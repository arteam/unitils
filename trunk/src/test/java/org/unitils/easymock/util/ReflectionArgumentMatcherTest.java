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
package org.unitils.easymock.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.lenEq;
import static org.unitils.easymock.EasyMockUnitils.refEq;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link org.unitils.easymock.util.ReflectionArgumentMatcher}.
 */
public class ReflectionArgumentMatcherTest {


    /* A test mock instance */
    private TestMock testMock;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        testMock = createMock(TestMock.class);
    }


    /**
     * Tests the refEq argument matcher with strict mode and equal values.
     */
    @Test
    public void testRefEq() {

        testMock.method(refEq("stringValue"), refEq(3), refEq("objectValue1"), refEq("objectValue2"));
        replay(testMock);

        testMock.method("stringValue", 3, "objectValue1", "objectValue2");
        verify(testMock);
    }


    /**
     * Tests the refEq argument matcher with strict mode and different values.
     */
    @Test
    public void testRefEq_notEquals() {

        testMock.method(refEq("stringValue"), refEq(3), refEq("objectValue1"), refEq("objectValue2"));
        replay(testMock);


        try {
            testMock.method("xxxx", 3, "objectValue1", "objectValue2");
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Tests the refEq argument matcher with strict mode and different vararg values.
     */
    @Test
    public void testRefEq_notEqualsVarArgs() {

        testMock.method(refEq("stringValue"), refEq(3), refEq("objectValue1"), refEq("objectValue2"));
        replay(testMock);

        try {
            testMock.method("stringValue", 3, "objectValue1");
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Tests the refEq argument matcher with lenient order mode and lists having a different order.
     */
    @Test
    public void testRefEq_equalsLenientOrder() {

        testMock.method(refEq(Arrays.asList("element1", "element2", "element3"), LENIENT_ORDER));
        replay(testMock);

        testMock.method(Arrays.asList("element3", "element1", "element2"));
        verify(testMock);
    }


    /**
     * Tests the lenEq argument matcher with lists having a different order.
     * This should be the same as refEq with lenient order and ignore defaults.
     */
    @Test
    public void testLenEq() {

        testMock.method(lenEq(Arrays.asList("element1", "element2", "element3")));
        replay(testMock);

        testMock.method(Arrays.asList("element3", "element1", "element2"));
        verify(testMock);
    }


    /**
     * Tests the refEq argument matcher with strict order mode and lists having a different order.
     */
    @Test
    public void testRefEq_notEqualsStrictOrder() {

        testMock.method(refEq(Arrays.asList("element1", "element2", "element3")));
        replay(testMock);

        try {
            testMock.method(Arrays.asList("element3", "element1", "element2"));
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Tests the refEq argument matcher with ignore defaults mode and null default value.
     */
    @Test
    public void testRefEq_equalsIgnoreDefaults() {

        testMock.method(refEq((List<String>) null, IGNORE_DEFAULTS));
        replay(testMock);

        testMock.method(Arrays.asList("element3", "element1", "element2"));
        verify(testMock);
    }


    /**
     * Tests the refEq argument matcher without ignore defaults mode and null default value.
     */
    @Test
    public void testRefEq_notEqualsNoIgnoreDefaults() {

        testMock.method(refEq((List<String>) null));
        replay(testMock);

        try {
            testMock.method(Arrays.asList("element3", "element1", "element2"));
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Test interface that is going to be mocked.
     */
    private interface TestMock {

        public void method(String arg1, int arg2, Object... arg3);

        public void method(List<String> arg1);

    }

}
