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
package org.unitils.inject.core;

import org.junit.Test;
import org.unitils.core.util.ObjectToInjectHolder;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ObjectToInjectGetValueTest {

    /* Tested object */
    private ObjectToInject objectToInject;


    @Test
    public void getValue() {
        objectToInject = new ObjectToInject("value");

        Object result = objectToInject.getValue();
        assertEquals("value", result);
    }

    @Test
    public void nullValue() {
        objectToInject = new ObjectToInject(null);

        Object result = objectToInject.getValue();
        assertNull(result);
    }

    @Test
    public void holderValueReturnedWhenWrappedInObjectToInjectHolder() {
        ObjectToInjectHolder objectToInjectHolder = new MyObjectToInjectHolder();
        objectToInject = new ObjectToInject(objectToInjectHolder);

        Object result = objectToInject.getValue();
        assertEquals("holder value", result);
    }


    private static class MyObjectToInjectHolder implements ObjectToInjectHolder<String> {

        public String getObjectToInject() {
            return "holder value";
        }

        public Type getObjectToInjectType(Type declaredType) {
            return null;
        }
    }
}
