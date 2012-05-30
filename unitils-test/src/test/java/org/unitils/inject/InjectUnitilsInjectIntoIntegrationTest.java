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
public class InjectUnitilsInjectIntoIntegrationTest extends UnitilsJUnit4 {

    private Target target;
    private Type value;
    private OtherType otherValue;


    @Before
    public void initialize() {
        value = new Type();
        otherValue = new OtherType();
        target = new Target();
    }


    @Test
    public void typeIsSpecified() {
        InjectUnitils.injectInto(target, "field", value);
        assertSame(value, target.field);
    }

    @Test
    public void exceptionWhenFieldDoesNotExist() {
        try {
            InjectUnitils.injectInto(target, "xxx", value);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into property 'xxx' with target of type org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Target.\n" +
                    "Reason: Unable to get field for property 'xxx'. Field with name 'xxx' does not exist on class org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenFieldIsOfOtherType() {
        try {
            InjectUnitils.injectInto(target, "field", otherValue);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into property 'field' with target of type org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Target.\n" +
                    "Reason: Unable to set value for field with name 'field'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Type. Value: other type\n" +
                    "Reason: IllegalArgumentException: Can not set org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Type field org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Target.field to org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$OtherType", e.getMessage());
        }
    }

    @Test
    public void autoCreationOfInnerFields() {
        InjectUnitils.injectInto(target, "field.innerField", "value");
        assertEquals("value", target.field.innerField);
    }

    @Test
    public void exceptionWhenInnerFieldDoesNotExist() {
        try {
            InjectUnitils.injectInto(target, "field.innerField", "value", false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into property 'field.innerField' with target of type org.unitils.inject.InjectUnitilsInjectIntoIntegrationTest$Target.\n" +
                    "Reason: Unable to set value for composite field with name 'field.innerField'. Inner field with name 'field' is null.", e.getMessage());
        }
    }


    private static class Target {

        private Type field;
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