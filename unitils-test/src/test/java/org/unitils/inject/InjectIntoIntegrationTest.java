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
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoIntegrationTest extends UnitilsJUnit4 {

    @TestedObject
    private InjectionTarget injectionTarget1;
    @TestedObject
    private InjectionTarget injectionTarget2;
    private InjectionTarget injectionTarget3;
    @TestedObject
    private SubClass subClass;

    @InjectInto(property = "value")
    private StringBuilder value1;

    @InjectInto(target = "injectionTarget3", property = "inner1.value", autoCreateInnerFields = true)
    private StringBuilder value2;

    @InjectInto(target = "injectionTarget3", property = "inner2.value", autoCreateInnerFields = false)
    private StringBuilder value3;

    @InjectInto(target = "injectionTarget3", property = "inner3.inner.value")
    private StringBuilder value4;

    @InjectInto(target = "injectionTarget3", property = "value")
    private StringBuilder value5;

    private Inner inner1;
    private Inner inner2;


    @Before
    public void initialize() {
        value1 = new StringBuilder("value1");
        value2 = new StringBuilder("value2");
        value3 = new StringBuilder("value3");
        value4 = new StringBuilder("value4");
        value5 = new StringBuilder("value5");
        inner1 = new Inner();
        inner2 = new Inner();
        injectionTarget3 = new InjectionTarget();
        injectionTarget3.inner1 = inner1;
        injectionTarget3.inner2 = inner2;
    }


    @Test
    public void injectInto() {
        // simple field + target defaults to tested objects
        assertSame(value1, injectionTarget1.value);
        assertSame(value1, injectionTarget2.value);
        // field on super class
        assertSame(value1, subClass.getValue());
        // composite field, auto create but inner has value
        assertSame(inner1, injectionTarget3.inner1);
        assertSame(value2, injectionTarget3.inner1.value);
        // composite field, no auto create and inner has value
        assertSame(inner2, injectionTarget3.inner2);
        assertSame(value3, injectionTarget3.inner2.value);
        // composite field, auto create
        assertNotNull(injectionTarget3.inner3);
        assertNotNull(injectionTarget3.inner3.inner);
        assertSame(value4, injectionTarget3.inner3.inner.value);
    }


    private static class InjectionTarget {

        private StringBuilder value;
        private Inner inner1;
        private Inner inner2;
        private Inner inner3;

        public StringBuilder getValue() {
            return value;
        }
    }

    private static class Inner {

        private StringBuilder value;

        private Inner inner;
    }

    private static class SubClass extends InjectionTarget {
    }

}
