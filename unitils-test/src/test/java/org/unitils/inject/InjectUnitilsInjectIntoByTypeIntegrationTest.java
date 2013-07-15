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
public class InjectUnitilsInjectIntoByTypeIntegrationTest extends UnitilsJUnit4 {

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
        InjectUnitils.injectIntoByType(target, value, Type.class);
        assertSame(value, target.field);
    }

    @Test
    public void typeOfValueIsUsedWhenNoTypeIsSpecified() {
        InjectUnitils.injectIntoByType(target, value);
        assertSame(value, target.field);
    }

    @Test
    public void exceptionWhenNoTypeAndNullValue() {
        try {
            InjectUnitils.injectIntoByType(target, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type. Unable to determine type from value: value is null. Please specify a type explicitly.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoMatch() {
        try {
            InjectUnitils.injectIntoByType(target, otherValue, OtherType.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.InjectUnitilsInjectIntoByTypeIntegrationTest$OtherType'.\n" +
                    "No field of matching type exists on class org.unitils.inject.InjectUnitilsInjectIntoByTypeIntegrationTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoTypeSpecifiedAndNoMatch() {
        try {
            InjectUnitils.injectIntoByType(target, otherValue);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.InjectUnitilsInjectIntoByTypeIntegrationTest$OtherType'.\n" +
                    "No field of matching type exists on class org.unitils.inject.InjectUnitilsInjectIntoByTypeIntegrationTest$Target or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void ignoredWhenNoMatchButNoFailWhenNoMatch() {
        InjectUnitils.injectIntoByType(target, otherValue, OtherType.class, false);
        assertNull(target.field);
    }


    @Test
    public void constructionForCoverage() {
        new InjectUnitils();
    }


    private static class Target {

        private Type field;
    }

    private static class Type {
    }

    private static class OtherType {
    }
}