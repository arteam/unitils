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

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

/**
 * Tests the behavior of the argment matchers with long and double objects.
 * This is a test for an issue reported by Pieter Valcke:
 * UNI-135 Mocking methods with return values of type long doesn't work.
 *
 * @author Pieter Valcke
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@SuppressWarnings({"UnnecessaryBoxing"})
public class ArgumentMatcherLongDoubleTest extends UnitilsJUnit4 {

    private Mock<TestInterface> testInterface;


    @Test
    public void testLong() {
        testInterface.onceReturns(1l).getLong(null);
        assertEquals(1, testInterface.getMock().getLong(null));
    }


    @Test
    public void testLongObject() {
        testInterface.onceReturns(new Long(1l)).getLongObject(null);
        assertEquals(new Long(1l), testInterface.getMock().getLongObject(null));
    }


    @Test
    public void testInt() {
        testInterface.onceReturns(1).getInt(null);
        assertEquals(1, testInterface.getMock().getInt(null));
    }


    @Test
    public void testIntObject() {
        testInterface.onceReturns(new Integer(1)).getIntObject(null);
        assertEquals(new Integer(1), testInterface.getMock().getIntObject(null));
    }


    @Test
    public void testDouble() {
        testInterface.onceReturns(2.0).getDouble(null);
        assertEquals(2.0, testInterface.getMock().getDouble(null), 0);
    }

    @Test
    public void testDoubleObject() {
        testInterface.onceReturns(new Double(2.0)).getDoubleObject(null);
        assertEquals(new Double(2.0), testInterface.getMock().getDoubleObject(null));
    }


    @Test
    public void testFloat() {
        testInterface.onceReturns(2.0f).getFloat(null);
        assertEquals(2.0f, testInterface.getMock().getFloat(null), 0);
    }


    @Test
    public void testFloatObject() {
        testInterface.onceReturns(new Float(2.0f)).getFloatObject(null);
        assertEquals(new Float(2.0f), testInterface.getMock().getFloatObject(null));
    }


    protected static interface TestInterface {

        public int getInt(String argument);

        public Integer getIntObject(String argument);

        public long getLong(String argument);

        public Long getLongObject(String argument);

        public float getFloat(String argument);

        public Float getFloatObject(String argument);

        public double getDouble(String argument);

        public Double getDoubleObject(String argument);
    }

}