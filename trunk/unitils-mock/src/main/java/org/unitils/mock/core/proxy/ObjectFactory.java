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
package org.unitils.mock.core.proxy;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;

/**
 * @author Tim Ducheyne
 */
public class ObjectFactory {

    /* Objenesis instance for creating new instances of types */
    protected Objenesis objenesis = new ObjenesisStd();


    /**
     * Tries to create an instance of the same type as the given value using Objenesis.
     * An exception if the instance could not be created. No constructor will be called.
     *
     * @param type The type of the instance to create
     * @return The new instance, not null
     */
    @SuppressWarnings({"unchecked"})
    public <T> T createWithoutCallingConstructor(Class<T> type) {
        try {
            return (T) objenesis.newInstance(type);

        } catch (Throwable t) {
            throw new UnitilsException("Unable to create instance of " + type, t);
        }
    }
}