/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * An interface for replacing the object to inject with another object.
 * During injection instead of injecting the current value, the getObjectToInject() method will be called to get the
 * object that will be injected.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ObjectToInjectHolder<T> {

    /**
     * @return The ojbect that should be injected instead of this object, can be null
     */
    T getObjectToInject();

    /**
     * Gets the type of the object to inject.
     * If this object was declared as an instance field, the field will be given as an argument.
     * This can give more type information when generic types are being used.
     *
     * @param field The field that declared this object, null if there is no field
     * @return The type, not null
     */
    Type getObjectToInjectType(Field field);
}
