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
package org.unitils.mock.mockbehavior.impl;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.mock.mockbehavior.impl.DummyValueReturningMockBehavior.MethodKey;

/**
 * @author Tim Ducheyne
 */
public class DummyValueReturningMockBehaviorMethodKeyEqualsTest {


    @Test
    public void equal() {
        MethodKey methodKey1 = new MethodKey("name", asList("value", 1));
        MethodKey methodKey2 = new MethodKey("name", asList("value", 1));

        assertTrue(methodKey1.equals(methodKey2));
        assertTrue(methodKey2.equals(methodKey1));
    }

    @Test
    public void same() {
        MethodKey methodKey = new MethodKey("name", asList("value", 1));

        assertTrue(methodKey.equals(methodKey));
    }

    @Test
    public void notEqualName() {
        MethodKey methodKey1 = new MethodKey("xxx", asList("value", 1));
        MethodKey methodKey2 = new MethodKey("yyy", asList("value", 1));

        assertFalse(methodKey1.equals(methodKey2));
        assertFalse(methodKey2.equals(methodKey1));
    }

    @Test
    public void notEqualArguments() {
        MethodKey methodKey1 = new MethodKey("name", asList("xxx", 1));
        MethodKey methodKey2 = new MethodKey("name", asList("yyy", 2));

        assertFalse(methodKey1.equals(methodKey2));
        assertFalse(methodKey2.equals(methodKey1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        MethodKey methodKey = new MethodKey("name", asList("value", 1));

        assertFalse(methodKey.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        MethodKey methodKey = new MethodKey("name", asList("value", 1));

        assertFalse(methodKey.equals("xxx"));
    }

    @Test
    public void nullNames() {
        MethodKey methodKey1 = new MethodKey(null, asList("value", 1));
        MethodKey methodKey2 = new MethodKey(null, asList("value", 1));

        assertTrue(methodKey1.equals(methodKey2));
    }

    @Test
    public void nullArguments() {
        MethodKey methodKey1 = new MethodKey("name", null);
        MethodKey methodKey2 = new MethodKey("name", null);

        assertTrue(methodKey1.equals(methodKey2));
    }

    @Test
    public void nullNamesAndArguments() {
        MethodKey methodKey1 = new MethodKey(null, null);
        MethodKey methodKey2 = new MethodKey(null, null);

        assertTrue(methodKey1.equals(methodKey2));
    }
}
