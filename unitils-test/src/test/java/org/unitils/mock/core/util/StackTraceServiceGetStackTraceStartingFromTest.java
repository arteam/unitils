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
package org.unitils.mock.core.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class StackTraceServiceGetStackTraceStartingFromTest {

    private StackTraceService stackTraceService = new StackTraceService();

    private StackTraceElement[] stackTrace;


    @Before
    public void initialize() {
        StackTraceElement element1 = new StackTraceElement("1", "1", "1", 1);
        StackTraceElement element2 = new StackTraceElement("2", "2", "2", 2);
        StackTraceElement element3 = new StackTraceElement("3", "3", "3", 3);
        stackTrace = new StackTraceElement[]{element1, element2, element3};
    }


    @Test
    public void getStackTraceStartingFrom() {
        StackTraceElement[] result = stackTraceService.getStackTraceStartingFrom(stackTrace, 1);
        assertEquals(2, result.length);
        assertEquals("2", result[0].getClassName());
        assertEquals("3", result[1].getClassName());
    }

    @Test
    public void fullStackTraceWhenNegativeIndex() {
        StackTraceElement[] result = stackTraceService.getStackTraceStartingFrom(stackTrace, -1);
        assertSame(stackTrace, result);
    }

    @Test
    public void fullStackTraceWhenZeroIndex() {
        StackTraceElement[] result = stackTraceService.getStackTraceStartingFrom(stackTrace, 0);
        assertSame(stackTrace, result);
    }

    @Test
    public void emptyStackTraceWhenIndexTooHigh() {
        StackTraceElement[] result = stackTraceService.getStackTraceStartingFrom(stackTrace, 3);
        Assert.assertEquals(0, result.length);
    }
}
