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
import org.unitils.core.UnitilsException;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class InjectUnitilsInjectIntoStaticByTypeIntegrationTest extends UnitilsJUnit4 {

    private Type value;
    private OtherType otherValue;


    @Before
    public void initialize() {
        value = new Type();
        otherValue = new OtherType();

        Target.field = null;
    }


    @Test
    public void typeIsSpecified() {
        InjectUnitils.injectIntoStaticByType(Target.class, value, Type.class);
        assertSame(value, Target.field);
    }

    @Test
    public void typeOfValueIsUsedWhenNoTypeIsSpecified() {
        InjectUnitils.injectIntoStaticByType(Target.class, value);
        assertSame(value, Target.field);
    }

    @Test
    public void exceptionWhenNoTypeAndNullValue() {
        try {
            InjectUnitils.injectIntoStaticByType(Target.class, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type. Unable to determine type from value: value is null. Please specify a type explicitly.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoMatch() {
        try {
            InjectUnitils.injectIntoStaticByType(Target.class, otherValue, OtherType.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class org.unitils.inject.InjectUnitilsInjectIntoStaticByTypeIntegrationTest$OtherType'.\n" +
                    "No static field of matching type exists on class org.unitils.inject.InjectUnitilsInjectIntoStaticByTypeIntegrationTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoTypeSpecifiedAndNoMatch() {
        try {
            InjectUnitils.injectIntoStaticByType(Target.class, otherValue);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static by type 'class org.unitils.inject.InjectUnitilsInjectIntoStaticByTypeIntegrationTest$OtherType'.\n" +
                    "No static field of matching type exists on class org.unitils.inject.InjectUnitilsInjectIntoStaticByTypeIntegrationTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void ignoredWhenNoMatchButNoFailWhenNoMatch() {
        InjectUnitils.injectIntoStaticByType(Target.class, otherValue, OtherType.class, false);
        assertNull(Target.field);
    }


    private static class Target {

        private static Type field;
    }

    private static class Type {
    }

    private static class OtherType {
    }
}