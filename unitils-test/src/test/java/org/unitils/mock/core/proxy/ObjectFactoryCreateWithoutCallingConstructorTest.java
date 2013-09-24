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

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ObjectFactoryCreateWithoutCallingConstructorTest extends UnitilsJUnit4 {

    private ObjectFactory objectFactory = new ObjectFactory();


    @Test
    public void constructorNotCalledAndValueNotInitialized() {
        ExceptionInConstructor result = objectFactory.createWithoutCallingConstructor(ExceptionInConstructor.class);
        assertNull(result.value);
    }

    @Test
    public void exceptionWhenUnableToCreateInstance() {
        try {
            objectFactory.createWithoutCallingConstructor(ExceptionInStaticInitializer.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance of " + ExceptionInStaticInitializer.class, e.getMessage());
        }
    }


    public static class ExceptionInConstructor {

        private String value = "test";

        public ExceptionInConstructor() {
            throw new NullPointerException("Expected");
        }
    }

    public static class ExceptionInStaticInitializer {

        static {
            if (Boolean.TRUE) {
                throw new NullPointerException("Expected");
            }
        }
    }
}