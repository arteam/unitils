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

package org.unitils.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.FieldAnnotationListener;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.annotation.FieldAnnotation;
import org.unitils.core.config.Configuration;
import org.unitils.core.context.Context;
import org.unitils.core.reflect.ClassWrapper;
import org.unitils.core.reflect.FieldWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class WrapperForFieldAnnotationListenerFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private WrapperForFieldAnnotationListenerFactory wrapperForFieldAnnotationListenerFactory;

    private Mock<Context> contextMock;
    private Mock<MyFieldAnnotationListener1> fieldAnnotationListener1Mock;
    private Mock<MyFieldAnnotationListener2> fieldAnnotationListener2Mock;
    @Dummy
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        wrapperForFieldAnnotationListenerFactory = new WrapperForFieldAnnotationListenerFactory(contextMock.getMock());

        fieldAnnotationListener1Mock.returns(EXECUTION).getTestPhase();
        fieldAnnotationListener2Mock.returns(EXECUTION).getTestPhase();
        contextMock.returns(fieldAnnotationListener1Mock).getInstanceOfType(MyFieldAnnotationListener1.class);
        contextMock.returns(fieldAnnotationListener2Mock).getInstanceOfType(MyFieldAnnotationListener2.class);
        contextMock.returns(configuration).getConfiguration();
    }


    @Test
    public void fieldAnnotations() throws Exception {
        Field field1 = SuperClass.class.getDeclaredField("field1");
        Field field2 = MyClass.class.getDeclaredField("field2");
        Method testMethod = MyClass.class.getDeclaredMethod("testMethod");
        Object testObject = new MyClass();

        ClassWrapper classWrapper = new ClassWrapper(MyClass.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        TestField testField1 = new TestField(new FieldWrapper(field1), testObject);
        MyFieldAnnotation1 fieldAnnotation1 = testField1.getAnnotation(MyFieldAnnotation1.class);
        TestField testField2 = new TestField(new FieldWrapper(field2), testObject);
        MyFieldAnnotation2 fieldAnnotation2 = testField2.getAnnotation(MyFieldAnnotation2.class);

        List<WrapperForFieldAnnotationListener> result = wrapperForFieldAnnotationListenerFactory.create(testInstance);
        assertEquals(2, result.size());

        WrapperForFieldAnnotationListener listener1 = result.get(0);
        assertTrue(listener1.fieldAnnotationListener instanceof MyFieldAnnotationListener2);
        assertEquals(testField2, listener1.testField);
        assertEquals(fieldAnnotation2, listener1.annotations.getAnnotation());
        assertTrue(listener1.annotations.getClassAnnotations().isEmpty());

        WrapperForFieldAnnotationListener listener2 = result.get(1);
        assertTrue(listener2.fieldAnnotationListener instanceof MyFieldAnnotationListener1);
        assertEquals(testField1, listener2.testField);
        assertEquals(fieldAnnotation1, listener2.annotations.getAnnotation());
        assertTrue(listener2.annotations.getClassAnnotations().isEmpty());
    }

    @Test
    public void classAndFieldAnnotation() throws Exception {
        Field field = AnnotationOnClassAndField.class.getDeclaredField("field");
        Method testMethod = AnnotationOnClassAndField.class.getDeclaredMethod("testMethod");
        Object testObject = new AnnotationOnClassAndField();

        ClassWrapper classWrapper = new ClassWrapper(AnnotationOnClassAndField.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        TestField testField = new TestField(new FieldWrapper(field), testObject);
        MyFieldAnnotation1 fieldAnnotation = testField.getAnnotation(MyFieldAnnotation1.class);
        MyFieldAnnotation1 classAnnotation = AnnotationOnClassAndField.class.getAnnotation(MyFieldAnnotation1.class);
        MyFieldAnnotation1 superClassAnnotation = AnnotationOnSuperClass.class.getAnnotation(MyFieldAnnotation1.class);

        List<WrapperForFieldAnnotationListener> result = wrapperForFieldAnnotationListenerFactory.create(testInstance);
        assertEquals(1, result.size());

        WrapperForFieldAnnotationListener listener1 = result.get(0);
        assertTrue(listener1.fieldAnnotationListener instanceof MyFieldAnnotationListener1);
        assertEquals(testField, listener1.testField);
        assertEquals(fieldAnnotation, listener1.annotations.getAnnotation());
        assertEquals(asList(classAnnotation, superClassAnnotation), listener1.annotations.getClassAnnotations());
    }

    @Test
    public void classAnnotationIsOnlyForDefaults() throws Exception {
        Field field = AnnotationOnClass.class.getDeclaredField("field");
        Method testMethod = AnnotationOnClass.class.getDeclaredMethod("testMethod");
        Object testObject = new AnnotationOnClass();

        ClassWrapper classWrapper = new ClassWrapper(AnnotationOnClass.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        List<WrapperForFieldAnnotationListener> result = wrapperForFieldAnnotationListenerFactory.create(testInstance);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoAnnotation() throws Exception {
        Method testMethod = NoAnnotation.class.getDeclaredMethod("testMethod");
        Object testObject = new NoAnnotation();

        ClassWrapper classWrapper = new ClassWrapper(NoAnnotation.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        List<WrapperForFieldAnnotationListener> result = wrapperForFieldAnnotationListenerFactory.create(testInstance);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoFields() throws Exception {
        Method testMethod = NoFields.class.getDeclaredMethod("testMethod");
        Object testObject = new NoFields();

        ClassWrapper classWrapper = new ClassWrapper(NoFields.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        List<WrapperForFieldAnnotationListener> result = wrapperForFieldAnnotationListenerFactory.create(testInstance);
        assertTrue(result.isEmpty());
    }

    @Test
    public void otherAnnotationsAreIgnored() throws Exception {
        Method testMethod = OtherAnnotation.class.getDeclaredMethod("testMethod");
        Object testObject = new OtherAnnotation();

        ClassWrapper classWrapper = new ClassWrapper(OtherAnnotation.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        List<WrapperForFieldAnnotationListener> result = wrapperForFieldAnnotationListenerFactory.create(testInstance);
        assertTrue(result.isEmpty());
    }


    @Target({FIELD, TYPE})
    @Retention(RUNTIME)
    @FieldAnnotation(MyFieldAnnotationListener1.class)
    public static @interface MyFieldAnnotation1 {
    }

    @Target({FIELD, TYPE})
    @Retention(RUNTIME)
    @FieldAnnotation(MyFieldAnnotationListener2.class)
    public static @interface MyFieldAnnotation2 {
    }

    @Target({FIELD, TYPE})
    @Retention(RUNTIME)
    public static @interface NotAFieldAnnotation {
    }

    public static class MyFieldAnnotationListener1 extends FieldAnnotationListener<MyFieldAnnotation1> {
    }

    public static class MyFieldAnnotationListener2 extends FieldAnnotationListener<MyFieldAnnotation2> {
    }


    private static class SuperClass {

        @MyFieldAnnotation1
        private String field1;
    }

    private static class MyClass extends SuperClass {

        @MyFieldAnnotation2
        private String field2;

        public void testMethod() {
        }
    }


    @MyFieldAnnotation1
    private static class AnnotationOnSuperClass {
    }

    @MyFieldAnnotation1
    private static class AnnotationOnClassAndField extends AnnotationOnSuperClass {

        @MyFieldAnnotation1
        private String field;

        public void testMethod() {
        }
    }


    @MyFieldAnnotation1
    private static class AnnotationOnClass {

        private String field;

        public void testMethod() {
        }
    }


    private static class NoAnnotation {

        private String field;

        public void testMethod() {
        }
    }

    @NotAFieldAnnotation
    private static class OtherAnnotation {

        @NotAFieldAnnotation
        private String field;

        public void testMethod() {
        }
    }

    private static class NoFields {

        public void testMethod() {
        }
    }
}
