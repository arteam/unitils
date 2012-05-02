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

package org.unitils.inject;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class InjectUnitilsInjectIntoStaticIntegrationTest extends UnitilsJUnit4 {

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
        InjectUnitils.injectIntoStatic(Target.class, "field", value);
        assertSame(value, Target.field);
    }

    @Test
    public void exceptionWhenFieldDoesNotExist() {
        try {
            InjectUnitils.injectIntoStatic(Target.class, "xxx", value);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static property 'xxx' with target class org.unitils.inject.InjectUnitilsInjectIntoStaticIntegrationTest$Target. Reason:\n" +
                    "Unable to get field for property 'xxx'. Field with name 'xxx' does not exist on class org.unitils.inject.InjectUnitilsInjectIntoStaticIntegrationTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenFieldIsOfOtherType() {
        try {
            InjectUnitils.injectIntoStatic(Target.class, "field", otherValue);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static property 'field' with target class org.unitils.inject.InjectUnitilsInjectIntoStaticIntegrationTest$Target. Reason:\n" +
                    "Unable to set value for field with name 'field'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: org.unitils.inject.InjectUnitilsInjectIntoStaticIntegrationTest$Type. Value: other type", e.getMessage());
        }
    }

    @Test
    public void autoCreationOfInnerFields() {
        InjectUnitils.injectIntoStatic(Target.class, "field.innerField", "value");
        assertEquals("value", Target.field.innerField);
    }

    @Test
    public void exceptionWhenInnerFieldDoesNotExist() {
        try {
            InjectUnitils.injectIntoStatic(Target.class, "field.innerField", "value", false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into static property 'field.innerField' with target class org.unitils.inject.InjectUnitilsInjectIntoStaticIntegrationTest$Target. Reason:\n" +
                    "Unable to set value for composite field with name 'field.innerField'. Inner field with name 'field' is null.", e.getMessage());
        }
    }


    private static class Target {

        private static Type field;
    }

    private static class Type {

        private String innerField;
    }

    private static class OtherType {

        @Override
        public String toString() {
            return "other type";
        }
    }
}