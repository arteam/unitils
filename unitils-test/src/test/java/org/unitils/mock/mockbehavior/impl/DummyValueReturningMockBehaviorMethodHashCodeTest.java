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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.mock.mockbehavior.impl.DummyValueReturningMockBehavior.MethodKey;

/**
 * @author Tim Ducheyne
 */
public class DummyValueReturningMockBehaviorMethodHashCodeTest {


    @Test
    public void hashCodeForNameAndArguments() {
        MethodKey key = new MethodKey("name", asList("value", 1));
        int result = key.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        MethodKey key1 = new MethodKey("name", asList("value", 1));
        MethodKey key2 = new MethodKey("name", asList("value", 1));

        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void nullName() {
        MethodKey key = new MethodKey(null, asList("value", 1));
        int result = key.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullArguments() {
        MethodKey key = new MethodKey("name", null);
        int result = key.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullNameAndArguments() {
        MethodKey key = new MethodKey(null, null);
        int result = key.hashCode();

        assertTrue(result == 0);
    }
}
