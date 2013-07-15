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
package org.unitils.util;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertNotNull;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * Test for creating instances using reflectation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsCreateInstanceOfTypeTest extends UnitilsJUnit4 {


    @Test
    public void publicConstructor() {
        TestConstructor result = createInstanceOfType(TestConstructor.class.getName(), false);
        assertNotNull(result);
    }

    @Test
    public void privateConstructor_byPassing() {
        TestPrivateConstructor result = createInstanceOfType(TestPrivateConstructor.class.getName(), true);
        assertNotNull(result);
    }

    @Test(expected = UnitilsException.class)
    public void privateConstructor_noByPassed() {
        createInstanceOfType(TestPrivateConstructor.class.getName(), false);
    }

    @Test
    public void noConstructor() {
        TestNoConstructor result = createInstanceOfType(TestNoConstructor.class.getName(), false);
        assertNotNull(result);
    }

    @Test(expected = UnitilsException.class)
    public void noConstructorNonStaticInnerClass() {
        createInstanceOfType(TestNoConstructorInnerClass.class.getName(), false);
    }

    @Test(expected = UnitilsException.class)
    public void classNotFound() {
        createInstanceOfType("xxx", false);
    }


    public static class TestConstructor {

        public TestConstructor() {
        }

        public TestConstructor(String test) {
        }

        private TestConstructor(int test) {
        }
    }

    public static class TestPrivateConstructor {

        private TestPrivateConstructor() {
        }
    }

    public static class TestNoConstructor {
    }

    public class TestNoConstructorInnerClass {
    }
}