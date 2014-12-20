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

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class UnitilsExceptionConstructorTest {

    @Test
    public void noArguments() {
        UnitilsException result = new UnitilsException();
        assertNull(result.getMessage());
        assertNull(result.getCause());
    }

    @Test
    public void message() {
        UnitilsException result = new UnitilsException("message");
        assertEquals("message", result.getMessage());
        assertNull(result.getCause());
    }

    @Test
    public void messageAndCause() {
        NullPointerException cause = new NullPointerException();

        UnitilsException result = new UnitilsException("message", cause);
        assertEquals("message", result.getMessage());
        assertSame(cause, result.getCause());
    }
}
