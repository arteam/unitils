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

import org.unitils.core.util.ObjectToInjectHolder;

import java.lang.reflect.Type;

/**
 * @author Tim Ducheyne
 */
public class ObjectToInject {

    protected Object value;
    protected Type declaredType;


    public ObjectToInject(Object value) {
        this.value = value;
    }

    public ObjectToInject(Object value, Type declaredType) {
        this.value = value;
        this.declaredType = declaredType;
    }


    /**
     * Gets the value from the given field. If the value is a holder for an object to inject, the wrapped object is returned.
     * For example in case of a field declared as Mock<MyClass>, this will return the proxy of the mock instead of the mock itself.
     *
     * @return The object
     */
    public Object getValue() {
        if (value instanceof ObjectToInjectHolder<?>) {
            return ((ObjectToInjectHolder<?>) value).getObjectToInject();
        }
        return value;
    }

    /**
     * Gets the type of the given field. If the field is a holder for an object to inject, the wrapped type is returned.
     * For example in case of a field declared as Mock<MyClass>, this will return MyClass instead of Mock<MyClass>
     *
     * @return The type of the object to inject
     */
    public Type getType() {
        if (value instanceof ObjectToInjectHolder<?>) {
            return ((ObjectToInjectHolder<?>) value).getObjectToInjectType(declaredType);
        }
        if (declaredType != null) {
            return declaredType;
        }
        if (value != null) {
            return value.getClass();
        }
        return null;
    }
}
