/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestClassHashCodeTest {


    @Test
    public void hashCodeForClass() {
        TestClass testClass = new TestClass(StringBuffer.class);
        int result = testClass.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        TestClass testClass1 = new TestClass(StringBuffer.class);
        TestClass testClass2 = new TestClass(StringBuffer.class);

        assertEquals(testClass1.hashCode(), testClass2.hashCode());
    }

    @Test
    public void nullClass() {
        TestClass testClass = new TestClass(null);
        int result = testClass.hashCode();

        assertEquals(0, result);
    }
}
