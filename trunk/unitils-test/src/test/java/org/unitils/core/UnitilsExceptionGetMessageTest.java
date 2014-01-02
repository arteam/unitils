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
package org.unitils.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class UnitilsExceptionGetMessageTest {

    @Test
    public void onlyMessageWhenNoCause() {
        UnitilsException exception = new UnitilsException("message");

        String result = exception.getMessage();
        assertEquals("message", result);
    }

    @Test
    public void nullWhenNoMessageOrCause() {
        UnitilsException exception = new UnitilsException();
        String result = exception.getMessage();
        assertNull(result);
    }

    @Test
    public void nestedUnitilsException() {
        UnitilsException exception1 = new UnitilsException("message 1");
        UnitilsException exception2 = new UnitilsException("message 2", exception1);
        UnitilsException exception3 = new UnitilsException("message 3", exception2);
        String result = exception3.getMessage();
        assertEquals("message 3\n" +
                "Reason: message 2\n" +
                "Reason: message 1", result);
    }

    @Test
    public void nestedOtherException() {
        NullPointerException exception1 = new NullPointerException("message 1");
        UnitilsException exception2 = new UnitilsException("message 2", exception1);
        String result = exception2.getMessage();
        assertEquals("message 2\n" +
                "Reason: NullPointerException: message 1", result);
    }

    @Test
    public void skipExceptionWithoutMessage() {
        UnitilsException exception1 = new UnitilsException("message 1");
        UnitilsException exception2 = new UnitilsException(null, exception1);
        UnitilsException exception3 = new UnitilsException("message 3", exception2);
        String result = exception3.getMessage();
        assertEquals("message 3\n" +
                "Reason: message 1", result);
    }
}
