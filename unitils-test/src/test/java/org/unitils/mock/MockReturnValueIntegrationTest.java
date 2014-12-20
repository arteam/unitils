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

import static org.junit.Assert.assertEquals;

/**
 * Tests the behavior of the argument matchers with long and double objects.
 * This is a test for following issue:
 * UNI-135 Mocking methods with return values of type long doesn't work.
 *
 * @author Pieter Valcke
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockReturnValueIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mock;


    @Test
    public void primitiveLong() {
        mock.onceReturns(1L).getLong(null);
        assertEquals(1, mock.getMock().getLong(null));
    }

    @Test
    public void longObject() {
        mock.onceReturns(new Long("1")).getLongObject(null);
        assertEquals(new Long(1L), mock.getMock().getLongObject(null));
    }

    @Test
    public void primitiveInt() {
        mock.onceReturns(1).getInt(null);
        assertEquals(1, mock.getMock().getInt(null));
    }

    @Test
    public void intObject() {
        mock.onceReturns(new Integer("1")).getIntObject(null);
        assertEquals(new Integer(1), mock.getMock().getIntObject(null));
    }

    @Test
    public void primitiveDouble() {
        mock.onceReturns(2.0).getDouble(null);
        assertEquals(2.0, mock.getMock().getDouble(null), 0);
    }

    @Test
    public void doubleObject() {
        mock.onceReturns(new Double("2.0")).getDoubleObject(null);
        assertEquals(new Double(2.0), mock.getMock().getDoubleObject(null));
    }

    @Test
    public void primitiveFloat() {
        mock.onceReturns(2.0F).getFloat(null);
        assertEquals(2.0F, mock.getMock().getFloat(null), 0);
    }

    @Test
    public void floatObject() {
        mock.onceReturns(new Float("2.0")).getFloatObject(null);
        assertEquals(new Float(2.0F), mock.getMock().getFloatObject(null));
    }


    protected static interface TestInterface {

        int getInt(String argument);

        Integer getIntObject(String argument);

        long getLong(String argument);

        Long getLongObject(String argument);

        float getFloat(String argument);

        Float getFloatObject(String argument);

        double getDouble(String argument);

        Double getDoubleObject(String argument);
    }
}