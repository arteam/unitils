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
package org.unitils.inject;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoStatic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoStaticIntegrationTest extends UnitilsJUnit4 {

    @InjectIntoStatic(target = InjectionTarget.class, property = "value")
    private StringBuilder value1;

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner1.value", autoCreateInnerFields = true)
    private StringBuilder value2;

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner2.value", autoCreateInnerFields = false)
    private StringBuilder value3;

    @InjectIntoStatic(target = InjectionTarget.class, property = "inner3.inner.value")
    private StringBuilder value4;

    private Inner inner1;
    private Inner inner2;


    @Before
    public void initialize() {
        value1 = new StringBuilder("value1");
        value2 = new StringBuilder("value2");
        value3 = new StringBuilder("value3");
        value4 = new StringBuilder("value4");
        inner1 = new Inner();
        inner2 = new Inner();
        InjectionTarget.value = null;
        InjectionTarget.inner1 = inner1;
        InjectionTarget.inner2 = inner2;
    }


    @Test
    public void injectIntoStatic() {
        // simple field
        assertSame(value1, InjectionTarget.value);
        // composite field, auto create but inner has value
        assertSame(inner1, InjectionTarget.inner1);
        assertSame(value2, InjectionTarget.inner1.value);
        // composite field, no auto create and inner has value
        assertSame(inner2, InjectionTarget.inner2);
        assertSame(value3, InjectionTarget.inner2.value);
        // composite field, auto create
        assertNotNull(InjectionTarget.inner3);
        assertNotNull(InjectionTarget.inner3.inner);
        assertSame(value4, InjectionTarget.inner3.inner.value);
    }


    private static class InjectionTarget {

        private static StringBuilder value;
        private static Inner inner1;
        private static Inner inner2;
        private static Inner inner3;
    }

    private static class Inner {

        private StringBuilder value;

        private Inner inner;
    }

}
