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
package org.unitils.mock.argumentmatcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.unitils.mock.ArgumentMatchers.*;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the usage of argment matchers.
 */
public class ArgumentMatcherTest {

    /* Test mock object */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, false, new Scenario());
    }


    /**
     * Tests the equals argument matcher, for an matching argument.
     */
    @Test
    public void testEqualsArgumentMatcher() {
        mockObject.returns(true).testMethodString(eq("test"));

        boolean result = mockObject.getInstance().testMethodString("test");
        assertTrue(result);
    }


    /**
     * Tests the equals argument matcher, for a non-matching argument.
     */
    @Test
    public void testEqualsArgumentMatcher_noMatch() {
        mockObject.returns(true).testMethodString(eq("test"));

        boolean result = mockObject.getInstance().testMethodString("xxxx");
        assertFalse(result);
    }


    /**
     * Tests the equals argument matcher, for a matching null argument.
     */
    @Test
    public void testEqualsArgumentMatcher_bothNull() {
        mockObject.returns(true).testMethodString(eq((String) null));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertTrue(result);
    }


    /**
     * Tests the equals argument matcher, for a non-matching null argument.
     */
    @Test
    public void testEqualsArgumentMatcher_null() {
        mockObject.returns(true).testMethodString(eq("test"));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertFalse(result);
    }


    /**
     * Tests the not null argument matcher, for an matching (not null) argument.
     */
    @Test
    public void testNotNullArgumentMatcher() {
        mockObject.returns(true).testMethodString(notNull(String.class));

        boolean result = mockObject.getInstance().testMethodString("test");
        assertTrue(result);
    }


    /**
     * Tests the not null argument matcher, for a non-matching (null) argument.
     */
    @Test
    public void testNotNullArgumentMatcher_noMatch() {
        mockObject.returns(true).testMethodString(notNull(String.class));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertFalse(result);
    }


    /**
     * Tests the not null argument matcher, for an matching (null) argument.
     */
    @Test
    public void testNullArgumentMatcher() {
        mockObject.returns(true).testMethodString(isNull(String.class));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertTrue(result);
    }


    /**
     * Tests the not null argument matcher, for a non-matching (not null) argument.
     */
    @Test
    public void testNullArgumentMatcher_noMatch() {
        mockObject.returns(true).testMethodString(isNull(String.class));

        boolean result = mockObject.getInstance().testMethodString("test");
        assertFalse(result);
    }


    /**
     * Tests the lenient equals argument matcher, for an matching argument.
     */
    @Test
    public void testLenEqArgumentMatcher() {
        mockObject.returns(true).testMethodString(lenEq("test"));

        boolean result = mockObject.getInstance().testMethodString("test");
        assertTrue(result);
    }


    /**
     * Tests the lenient equals argument matcher, for a non-matching argument.
     */
    @Test
    public void testLenEqArgumentMatcher_noMatch() {
        mockObject.returns(true).testMethodString(lenEq("test"));

        boolean result = mockObject.getInstance().testMethodString("xxxx");
        assertFalse(result);
    }


    /**
     * Tests the lenient equals argument matcher, for a matching null argument.
     */
    @Test
    public void testLenEqArgumentMatcher_bothNull() {
        mockObject.returns(true).testMethodString(lenEq((String) null));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertTrue(result);
    }


    /**
     * Tests the lenient equals argument matcher, for a non-matching null argument.
     */
    @Test
    public void testLenEqArgumentMatcher_null() {
        mockObject.returns(true).testMethodString(lenEq("test"));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertFalse(result);
    }


    /**
     * Tests the reflection equals argument matcher, for an matching argument.
     */
    @Test
    public void testRefEqArgumentMatcher() {
        mockObject.returns(true).testMethodString(refEq("test"));

        boolean result = mockObject.getInstance().testMethodString("test");
        assertTrue(result);
    }


    /**
     * Tests the reflection equals argument matcher, for a non-matching argument.
     */
    @Test
    public void testRefEqArgumentMatcher_noMatch() {
        mockObject.returns(true).testMethodString(refEq("test"));

        boolean result = mockObject.getInstance().testMethodString("xxxx");
        assertFalse(result);
    }


    /**
     * Tests the reflection equals argument matcher, for a matching null argument.
     */
    @Test
    public void testRefEqArgumentMatcher_bothNull() {
        mockObject.returns(true).testMethodString(refEq((String) null));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertTrue(result);
    }


    /**
     * Tests the reflection equals argument matcher, for a non-matching null argument.
     */
    @Test
    public void testRefEqArgumentMatcher_null() {
        mockObject.returns(true).testMethodString(refEq("test"));

        boolean result = mockObject.getInstance().testMethodString(null);
        assertFalse(result);
    }


    /**
     * Tests the same argument matcher, for an matching argument.
     */
    @Test
    public void testSameArgumentMatcher() {
        List object = new ArrayList();
        mockObject.returns(true).testMethodObject(same(object));

        boolean result = mockObject.getInstance().testMethodObject(object);
        assertTrue(result);
    }


    /**
     * Tests the same argument matcher, for a non-matching argument.
     */
    @Test
    public void testSameArgumentMatcher_noMatch() {
        mockObject.returns(true).testMethodObject(same(new ArrayList()));

        boolean result = mockObject.getInstance().testMethodObject(new ArrayList());
        assertFalse(result);
    }


    /**
     * Tests the same argument matcher, for a matching null argument.
     */
    @Test
    public void testSameArgumentMatcher_bothNull() {
        mockObject.returns(true).testMethodObject(same(null));

        boolean result = mockObject.getInstance().testMethodObject(null);
        assertTrue(result);
    }


    /**
     * Tests the same argument matcher, for a non-matching null argument.
     */
    @Test
    public void testSameArgumentMatcher_null() {
        mockObject.returns(true).testMethodObject(same(new ArrayList()));

        boolean result = mockObject.getInstance().testMethodObject(null);
        assertFalse(result);
    }


    /**
     * Tests the same argument matcher using assert invoked.
     */
    @Test
    public void testSameArgumentMatcher_assertInvoked() {
        List<String> list = new ArrayList<String>();
        mockObject.getInstance().testMethodObject(list);

        mockObject.assertInvoked().testMethodObject(same(list));
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        boolean testMethodString(String arg1);

        boolean testMethodObject(Object arg1);

    }

}