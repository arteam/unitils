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

package org.unitilsnew.core.reflect;

import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperCreateInstanceTest {

    /* Tested object */
    private ClassWrapper classWrapper;


    @Test
    public void createInstance() {
        classWrapper = new ClassWrapper(MyClass.class);

        Object result = classWrapper.createInstance();
        assertTrue(result instanceof MyClass);
    }

    @Test
    public void privateConstructorAllowed() {
        classWrapper = new ClassWrapper(PrivateConstructor.class);

        Object result = classWrapper.createInstance();
        assertTrue(result instanceof PrivateConstructor);
    }

    @Test
    public void exceptionWhenNoDefaultConstructor() {
        try {
            classWrapper = new ClassWrapper(NoDefaultConstructor.class);
            classWrapper.createInstance();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to create instance of type org.unitilsnew.core.reflect.ClassWrapperCreateInstanceTest$NoDefaultConstructor. No default (no-argument) constructor found.\n" +
                    "Reason: NoSuchMethodException: org.unitilsnew.core.reflect.ClassWrapperCreateInstanceTest$NoDefaultConstructor.<init>()", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNonStaticInnerClass() {
        try {
            classWrapper = new ClassWrapper(NonStaticInnerClass.class);
            classWrapper.createInstance();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to create instance of type org.unitilsnew.core.reflect.ClassWrapperCreateInstanceTest$NonStaticInnerClass. Type is a non-static inner class which is only know in the context of an instance of the enclosing class. Declare the inner class static to make construction possible.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenConstructorRaisedException() {
        try {
            classWrapper = new ClassWrapper(ConstructorRaisedException.class);
            classWrapper.createInstance();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to create instance of type org.unitilsnew.core.reflect.ClassWrapperCreateInstanceTest$ConstructorRaisedException.\n" +
                    "Reason: NullPointerException: test", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenAbstractClass() {
        try {
            classWrapper = new ClassWrapper(AbstractClass.class);
            classWrapper.createInstance();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to create instance of type org.unitilsnew.core.reflect.ClassWrapperCreateInstanceTest$AbstractClass. Type is an abstract class.", e.getMessage());
        }
    }


    private static class MyClass {
    }

    private static class PrivateConstructor {
        private PrivateConstructor() {
        }
    }

    private static class NoDefaultConstructor {
        private NoDefaultConstructor(String arg) {
        }
    }

    private class NonStaticInnerClass {
    }

    private static class ConstructorRaisedException {

        private ConstructorRaisedException() {
            throw new NullPointerException("test");
        }
    }

    private static abstract class AbstractClass {
    }
}
