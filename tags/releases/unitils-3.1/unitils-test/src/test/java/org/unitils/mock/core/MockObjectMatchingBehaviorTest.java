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
package org.unitils.mock.core;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectMatchingBehaviorTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
    }


    @Test
    public void betterMatchBySpecifyingValue() {
        mockObject.returns(1).testMethod(null, null, null);
        mockObject.returns(2).testMethod("arg1", null, null);

        int result = mockObject.getMock().testMethod("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void betterMatchBySpecifyingValue_oneTimeMatching() {
        mockObject.onceReturns(1).testMethod(null, null, null);
        mockObject.onceReturns(2).testMethod("arg1", null, null);

        int result = mockObject.getMock().testMethod("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void firstMatchWhenMultipleBestMatches() {
        mockObject.returns(1).testMethod("arg1", null, null);
        mockObject.returns(2).testMethod("arg1", null, null);

        int result = mockObject.getMock().testMethod("arg1", "arg2", "arg3");
        assertEquals(1, result);
    }

    @Test
    public void betterMatchIfMoreSpecific() {
        mockObject.returns(1).testMethod("arg1", null, null);
        mockObject.returns(2).testMethod("arg1", "arg2", null);

        int result = mockObject.getMock().testMethod("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void oneTimeMatchingPreceedsAlwaysMatching() {
        mockObject.onceReturns(1).testMethod("arg1", null, null);
        mockObject.returns(2).testMethod("arg1", "arg2", null);

        int result = mockObject.getMock().testMethod("arg1", "arg2", "arg3");
        assertEquals(1, result);
    }

    @Test
    public void noMatchFound() {
        mockObject.returns(1).testMethod("arg1", null, null);
        mockObject.returns(2).testMethod("arg1", "arg2", null);

        int result = mockObject.getMock().testMethod("xxx", "arg2", "arg3");
        assertEquals(0, result);
    }


    private static interface TestClass {

        public int testMethod(String arg1, String arg2, String arg3);

    }

}