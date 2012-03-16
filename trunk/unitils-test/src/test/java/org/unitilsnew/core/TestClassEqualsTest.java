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

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestClassEqualsTest {


    @Test
    public void equal() {
        TestClass testClass1 = new TestClass(StringBuffer.class);
        TestClass testClass2 = new TestClass(StringBuffer.class);

        assertTrue(testClass1.equals(testClass2));
        assertTrue(testClass2.equals(testClass1));
    }

    @Test
    public void same() {
        TestClass testClass = new TestClass(StringBuffer.class);

        assertTrue(testClass.equals(testClass));
    }

    @Test
    public void notEqual() {
        TestClass testClass1 = new TestClass(StringBuffer.class);
        TestClass testClass2 = new TestClass(List.class);

        assertFalse(testClass1.equals(testClass2));
        assertFalse(testClass2.equals(testClass1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        TestClass testClass = new TestClass(StringBuffer.class);

        assertFalse(testClass.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        TestClass testClass = new TestClass(StringBuffer.class);

        assertFalse(testClass.equals("xxx"));
    }

    @Test
    public void nullClasses() {
        TestClass testClass1 = new TestClass(null);
        TestClass testClass2 = new TestClass(null);

        assertTrue(testClass1.equals(testClass2));
    }
}
