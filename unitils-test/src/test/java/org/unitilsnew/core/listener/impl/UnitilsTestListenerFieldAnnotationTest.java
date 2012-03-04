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

package org.unitilsnew.core.listener.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.Annotations;
import org.unitilsnew.core.Context;
import org.unitilsnew.core.TestClass;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.annotation.FieldAnnotation;
import org.unitilsnew.core.config.Configuration;
import org.unitilsnew.core.listener.FieldAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerFieldAnnotationTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListener unitilsTestListener;

    private Mock<Context> contextMock;
    private Mock<TestFieldAnnotationHandler> fieldAnnotationHandlerMock;
    @Dummy
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        unitilsTestListener = new UnitilsTestListener(new ArrayList<TestListener>(), contextMock.getMock());

        fieldAnnotationHandlerMock.returns(EXECUTION).getTestPhase();
        contextMock.returns(fieldAnnotationHandlerMock).getInstanceOfType(TestFieldAnnotationHandler.class, new String[]{});
        contextMock.returns(configuration).getConfiguration();
    }


    @Test
    public void fieldAnnotation() throws Exception {
        Field testField = AnnotationOnField.class.getDeclaredField("testField");
        Method testMethod = AnnotationOnField.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnField.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);
        TestFieldAnnotation fieldAnnotation = testField.getAnnotation(TestFieldAnnotation.class);
        Annotations<TestFieldAnnotation> annotations = new Annotations<TestFieldAnnotation>(fieldAnnotation, new ArrayList<TestFieldAnnotation>(), configuration);

        unitilsTestListener.beforeTestClass(AnnotationOnField.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        fieldAnnotationHandlerMock.assertInvokedInSequence().beforeTestClass(testClass, testField, annotations);
        fieldAnnotationHandlerMock.assertInvokedInSequence().beforeTestSetUp(testInstance, testField, annotations);
        fieldAnnotationHandlerMock.assertInvokedInSequence().beforeTestMethod(testInstance, testField, annotations);
        fieldAnnotationHandlerMock.assertInvokedInSequence().afterTestMethod(testInstance, testField, annotations, null);
        fieldAnnotationHandlerMock.assertInvokedInSequence().afterTestTearDown(testInstance, testField, annotations);
    }

    @Test
    public void classAnnotationIsOnlyForDefaults() throws Exception {
        Field testField = AnnotationOnClass.class.getDeclaredField("testField");
        Method testMethod = AnnotationOnClass.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnClass.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);
        TestFieldAnnotation classAnnotation = AnnotationOnClass.class.getAnnotation(TestFieldAnnotation.class);
        Annotations<TestFieldAnnotation> annotations = new Annotations<TestFieldAnnotation>(null, asList(classAnnotation), configuration);

        unitilsTestListener.beforeTestClass(AnnotationOnClass.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        fieldAnnotationHandlerMock.assertNotInvoked().beforeTestClass(testClass, testField, annotations);
        fieldAnnotationHandlerMock.assertNotInvoked().beforeTestSetUp(testInstance, testField, annotations);
        fieldAnnotationHandlerMock.assertNotInvoked().beforeTestMethod(testInstance, testField, annotations);
        fieldAnnotationHandlerMock.assertNotInvoked().afterTestMethod(testInstance, testField, annotations, null);
        fieldAnnotationHandlerMock.assertNotInvoked().afterTestTearDown(testInstance, testField, annotations);
    }

    @Test
    public void classAndFieldAnnotation() throws Exception {
        Field testField = AnnotationOnClassAndField.class.getDeclaredField("testField");
        Method testMethod = AnnotationOnClassAndField.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnClassAndField.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);
        TestFieldAnnotation fieldAnnotation = testField.getAnnotation(TestFieldAnnotation.class);
        TestFieldAnnotation classAnnotation = AnnotationOnClass.class.getAnnotation(TestFieldAnnotation.class);
        Annotations<TestFieldAnnotation> annotations = new Annotations<TestFieldAnnotation>(fieldAnnotation, asList(classAnnotation), configuration);

        unitilsTestListener.beforeTestClass(AnnotationOnClassAndField.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        fieldAnnotationHandlerMock.assertInvokedInSequence().beforeTestClass(testClass, testField, annotations);
        fieldAnnotationHandlerMock.assertInvokedInSequence().beforeTestSetUp(testInstance, testField, annotations);
        fieldAnnotationHandlerMock.assertInvokedInSequence().beforeTestMethod(testInstance, testField, annotations);
        fieldAnnotationHandlerMock.assertInvokedInSequence().afterTestMethod(testInstance, testField, annotations, null);
        fieldAnnotationHandlerMock.assertInvokedInSequence().afterTestTearDown(testInstance, testField, annotations);
    }

    @Test
    public void noAnnotation() throws Exception {
        Method testMethod = AnnotationOnClass.class.getDeclaredMethod("testMethod");

        TestClass testClass = new TestClass(AnnotationOnClass.class);
        TestInstance testInstance = new TestInstance(testClass, null, testMethod);

        unitilsTestListener.beforeTestClass(AnnotationOnClass.class);
        unitilsTestListener.beforeTestSetUp(null, testMethod);
        unitilsTestListener.beforeTestMethod();
        unitilsTestListener.afterTestMethod(null);
        unitilsTestListener.afterTestTearDown();

        fieldAnnotationHandlerMock.assertNotInvoked().beforeTestClass(null, null, null);
        fieldAnnotationHandlerMock.assertNotInvoked().beforeTestSetUp(null, null, null);
        fieldAnnotationHandlerMock.assertNotInvoked().beforeTestMethod(null, null, null);
        fieldAnnotationHandlerMock.assertNotInvoked().afterTestMethod(null, null, null, null);
        fieldAnnotationHandlerMock.assertNotInvoked().afterTestTearDown(null, null, null);
    }


    @Target({FIELD, TYPE})
    @Retention(RUNTIME)
    @FieldAnnotation(TestFieldAnnotationHandler.class)
    public static @interface TestFieldAnnotation {
    }

    public static class TestFieldAnnotationHandler extends FieldAnnotationListener<TestFieldAnnotation> {
    }

    public static class AnnotationOnField {

        @TestFieldAnnotation
        private String testField;

        public void testMethod() {
        }
    }

    public static class NoAnnotation {

        private String testField;

        public void testMethod() {
        }
    }

    @TestFieldAnnotation
    public static class AnnotationOnClass {

        private String testField;

        public void testMethod() {
        }
    }

    @TestFieldAnnotation
    public static class AnnotationOnClassAndField {

        @TestFieldAnnotation
        private String testField;

        public void testMethod() {
        }
    }

}
