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
package org.unitils.inject.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitilsnew.core.reflect.OriginalFieldValue;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class InjectionByTypeServiceInjectIntoAllByTypeTest {

    /* Tested object */
    private InjectionByTypeService injectionByTypeService = new InjectionByTypeService();

    private TargetClass target1;
    private TargetClass target2;
    private ObjectToInject objectToInject;


    @Before
    public void initialize() {
        target1 = new TargetClass();
        target2 = new TargetClass();
        objectToInject = new ObjectToInject(new MyType("value"));

        target1.field = new MyType("original value 1");
        target2.field = new MyType("original value 2");
    }


    @Test
    public void simpleProperty() {
        List<OriginalFieldValue> result = injectionByTypeService.injectIntoAllByType(asList(target1, target2), objectToInject, false);

        assertEquals("value", target1.field.value);
        assertEquals("value", target2.field.value);
        assertPropertyLenientEquals("originalValue.value", asList("original value 1", "original value 2"), result);
    }

    @Test
    public void exceptionWhenNullTargets() {
        try {
            injectionByTypeService.injectIntoAllByType(null, objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type. Targets cannot be null.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullTarget() {
        try {
            injectionByTypeService.injectIntoAllByType(asList(target1, null), objectToInject, false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to inject into by type 'class org.unitils.inject.core.InjectionByTypeServiceInjectIntoAllByTypeTest$MyType'. Target cannot be null.", e.getMessage());
        }
    }

    @Test
    public void ignoreWhenEmptyList() {
        List<OriginalFieldValue> result = injectionByTypeService.injectIntoAllByType(Collections.emptyList(), objectToInject, false);

        assertEquals("original value 1", target1.field.value);
        assertEquals("original value 2", target2.field.value);
        assertTrue(result.isEmpty());
    }

    public static class TargetClass {

        private MyType field;

    }

    public static class MyType {

        private String value;

        public MyType(String value) {
            this.value = value;
        }
    }
}
