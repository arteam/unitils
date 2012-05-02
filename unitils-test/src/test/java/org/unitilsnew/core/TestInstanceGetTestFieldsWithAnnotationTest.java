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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class TestInstanceGetTestFieldsWithAnnotationTest {

    /* Tested object */
    private TestInstance testInstance;

    private Field field1;
    private Field subClassField1;
    private Field subClassField3;


    @Before
    public void initialize() throws Exception {
        field1 = MyClass.class.getDeclaredField("field1");
        subClassField1 = MySubClass.class.getDeclaredField("field1");
        subClassField3 = MySubClass.class.getDeclaredField("field3");

        ClassWrapper classWrapper = new ClassWrapper(MySubClass.class);
        Object testObject = new MySubClass();
        testInstance = new TestInstance(classWrapper, testObject, null);
    }


    @Test
    public void annotatedFields() {
        List<TestField> result = testInstance.getTestFieldsWithAnnotation(MyAnnotation.class);
        assertPropertyLenientEquals("field", asList(field1, subClassField1, subClassField3), result);
    }

    @Test
    public void noFieldsFound() {
        List<TestField> result = testInstance.getTestFieldsOfType(Target.class);
        assertTrue(result.isEmpty());
    }


    @Retention(RUNTIME)
    private static @interface MyAnnotation {
    }

    private static class MyClass {

        @MyAnnotation
        private String field1;
        private String field2;
    }

    private static class MySubClass extends MyClass {

        @MyAnnotation
        private String field1;
        @MyAnnotation
        private String field3;
        private String field4;
    }
}
