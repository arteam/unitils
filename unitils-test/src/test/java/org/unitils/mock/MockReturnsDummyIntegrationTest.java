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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockReturnsDummyIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;
    private Mock<Set> otherMock;


    @Test
    public void returnsDummy() {
        mockObject.returnsDummy().testMethod();

        MyService result1 = mockObject.getMock().testMethod();
        MyService result2 = mockObject.getMock().testMethod();
        assertNotNull(result1.getValue());
        assertSame(result1, result2);
    }

    @Test
    public void onceReturnsDummy() {
        mockObject.onceReturnsDummy().testMethod();

        MyService result1 = mockObject.getMock().testMethod();
        MyService result2 = mockObject.getMock().testMethod();
        assertNotNull(result1.getValue());
        assertNull(result2);
    }

    @Test
    public void returnsEmptyStringWhenString() {
        mockObject.returnsDummy().stringMethod();

        String result = mockObject.getMock().stringMethod();
        assertEquals("", result);
    }

    @Test
    public void returnsZeroWhenInt() {
        mockObject.returnsDummy().intMethod();

        int result = mockObject.getMock().intMethod();
        assertEquals(0, result);
    }

    @Test
    public void returnsEmptyListWhenList() {
        mockObject.returnsDummy().listMethod();

        List<String> result = mockObject.getMock().listMethod();
        assertTrue(result.isEmpty());
    }

    @Test
    public void returnsEmptySetWhenSet() {
        mockObject.returnsDummy().setMethod();

        Set<String> result = mockObject.getMock().setMethod();
        assertTrue(result.isEmpty());
    }

    @Test
    public void returnsEmptyMapWhenMap() {
        mockObject.returnsDummy().mapMethod();

        Map<String, String> result = mockObject.getMock().mapMethod();
        assertTrue(result.isEmpty());
    }

    @Test
    public void exceptionWhenVoidMethod() {
        mockObject.returnsDummy().voidMethod();
        try {
            mockObject.getMock().voidMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Trying to define mock behavior that returns a value for a void method.", e.getMessage());
        }
    }


    private static interface TestInterface {

        MyService testMethod();

        String stringMethod();

        int intMethod();

        List<String> listMethod();

        Set<String> setMethod();

        Map<String, String> mapMethod();

        void voidMethod();
    }

    private static interface MyService {

        Properties getValue();
    }
}
