/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitilsnew.UnitilsJUnit4;

import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InjectIntoByTypeIntegrationTest extends UnitilsJUnit4 {

    @TestedObject
    private InjectionTarget injectionTarget1;
    @TestedObject
    private InjectionTarget injectionTarget2;
    private InjectionTarget injectionTarget3;
    @TestedObject
    private SubClass subClass;

    @InjectIntoByType
    public StringBuilder value1;
    @InjectIntoByType(target = "injectionTarget3")
    public StringBuilder value2;
    @InjectIntoByType(failWhenNoMatch = false)
    public Properties noMatchFoundIgnoredValue;


    @Before
    public void initialize() {
        value1 = new StringBuilder("value1");
        value2 = new StringBuilder("value2");
        injectionTarget3 = new InjectionTarget();
    }


    @Test
    public void injectIntoByType() {
        // target defaults to tested objects
        assertSame(value1, injectionTarget1.value);
        assertSame(value1, injectionTarget2.value);
        // target specified explicitly
        assertSame(value2, injectionTarget3.value);
        // target field in superclass specified explicitly
        assertSame(value1, subClass.getValue());
    }


    private static class InjectionTarget {

        private StringBuilder value;

        public StringBuilder getValue() {
            return value;
        }
    }

    private static class SubClass extends InjectionTarget {
    }
}