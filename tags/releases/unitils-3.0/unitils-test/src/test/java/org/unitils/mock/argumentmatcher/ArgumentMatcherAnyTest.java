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

/**
 * Tests the usage of anyInt, anyLong, etc argment matchers.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ArgumentMatcherAnyTest {

    /* Test mock object */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, this);
    }


    @Test
    public void testAnyBoolean() {
        mockObject.returns(true).testMethodBoolean(anyBoolean());

        boolean result = mockObject.getMock().testMethodBoolean(false);
        assertTrue(result);
        mockObject.assertInvoked().testMethodBoolean(anyBoolean());
    }


    @Test
    public void testAnyByte() {
        mockObject.returns(true).testMethodByte(anyByte());

        boolean result = mockObject.getMock().testMethodByte((byte) 5);
        assertTrue(result);
        mockObject.assertInvoked().testMethodByte(anyByte());
    }


    @Test
    public void testAnyShort() {
        mockObject.returns(true).testMethodShort(anyShort());

        boolean result = mockObject.getMock().testMethodShort((short) 5);
        assertTrue(result);
        mockObject.assertInvoked().testMethodShort(anyShort());
    }


    @Test
    public void testAnyChar() {
        mockObject.returns(true).testMethodChar(anyChar());

        boolean result = mockObject.getMock().testMethodChar('a');
        assertTrue(result);
        mockObject.assertInvoked().testMethodChar(anyChar());
    }


    @Test
    public void testAnyInt() {
        mockObject.returns(true).testMethodInteger(anyInt());

        boolean result = mockObject.getMock().testMethodInteger(5);
        assertTrue(result);
        mockObject.assertInvoked().testMethodInteger(anyInt());
    }


    @Test
    public void testAnyLong() {
        mockObject.returns(true).testMethodLong(anyLong());

        boolean result = mockObject.getMock().testMethodLong(5);
        assertTrue(result);
        mockObject.assertInvoked().testMethodLong(anyLong());
    }


    @Test
    public void testAnyFloat() {
        mockObject.returns(true).testMethodFloat(anyFloat());

        boolean result = mockObject.getMock().testMethodFloat(5);
        assertTrue(result);
        mockObject.assertInvoked().testMethodFloat(anyFloat());
    }


    @Test
    public void testAnyDouble() {
        mockObject.returns(true).testMethodDouble(anyDouble());

        boolean result = mockObject.getMock().testMethodDouble(5);
        assertTrue(result);
        mockObject.assertInvoked().testMethodDouble(anyDouble());
    }


    @Test
    public void testAnyIntButDouble() {
        mockObject.returns(true).testMethodDouble(anyInt());

        boolean result = mockObject.getMock().testMethodDouble(5.99);
        assertFalse(result);
        mockObject.assertNotInvoked().testMethodDouble(anyInt());
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        boolean testMethodBoolean(boolean arg1);

        boolean testMethodByte(byte arg1);

        boolean testMethodShort(short arg1);

        boolean testMethodChar(char arg1);

        boolean testMethodInteger(int arg1);

        boolean testMethodLong(long arg1);

        boolean testMethodFloat(float arg1);

        boolean testMethodDouble(double arg1);


    }

}