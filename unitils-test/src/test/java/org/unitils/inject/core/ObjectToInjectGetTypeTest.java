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

import org.junit.Test;
import org.unitils.core.util.ObjectToInjectHolder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ObjectToInjectGetTypeTest {

    /* Tested object */
    private ObjectToInject objectToInject;


    @Test
    public void declaredTypeOverridesValueType() {
        objectToInject = new ObjectToInject("value", Map.class);

        Type result = objectToInject.getType();
        assertEquals(Map.class, result);
    }

    @Test
    public void valueTypeIsUsedWhenThereIsNoDeclaredType() {
        objectToInject = new ObjectToInject("value", null);

        Type result = objectToInject.getType();
        assertEquals(String.class, result);
    }

    @Test
    public void valueTypeIsUsedWhenOnlyValueIsSpecified() {
        objectToInject = new ObjectToInject("value");

        Type result = objectToInject.getType();
        assertEquals(String.class, result);
    }

    @Test
    public void nullIsReturnedWhenNoDeclaredTypeAndNoValueType() {
        objectToInject = new ObjectToInject(null, null);

        Type result = objectToInject.getType();
        assertNull(result);
    }

    @Test
    public void holderTypeReturnedWhenWrappedInObjectToInjectHolder() {
        ObjectToInjectHolder objectToInjectHolder = new MyObjectToInjectHolder();
        objectToInject = new ObjectToInject(objectToInjectHolder, Map.class);

        Object result = objectToInject.getType();
        assertEquals(List.class, result);
    }


    private static class MyObjectToInjectHolder implements ObjectToInjectHolder<String> {

        public String getObjectToInject() {
            return null;
        }

        public Type getObjectToInjectType(Type declaredType) {
            assertEquals(Map.class, declaredType);
            return List.class;
        }
    }
}
