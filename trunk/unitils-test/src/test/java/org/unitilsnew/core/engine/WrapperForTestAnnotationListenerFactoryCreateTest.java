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

package org.unitilsnew.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestAnnotationListener;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.annotation.TestAnnotation;
import org.unitilsnew.core.config.Configuration;
import org.unitilsnew.core.context.Context;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * todo add test for annotation that is not annotated
 *
 * @author Tim Ducheyne
 */
public class WrapperForTestAnnotationListenerFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private WrapperForTestAnnotationListenerFactory wrapperForTestAnnotationListenerFactory;

    private Mock<Context> contextMock;
    private Mock<MyTestAnnotationListener1> testAnnotationListener1Mock;
    private Mock<MyTestAnnotationListener2> testAnnotationListener2Mock;
    @Dummy
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        wrapperForTestAnnotationListenerFactory = new WrapperForTestAnnotationListenerFactory(contextMock.getMock());

        testAnnotationListener1Mock.returns(EXECUTION).getTestPhase();
        testAnnotationListener2Mock.returns(EXECUTION).getTestPhase();
        contextMock.returns(testAnnotationListener1Mock).getInstanceOfType(MyTestAnnotationListener1.class);
        contextMock.returns(testAnnotationListener2Mock).getInstanceOfType(MyTestAnnotationListener2.class);
        contextMock.returns(configuration).getConfiguration();
    }


    @Test
    public void methodAnnotation() throws Exception {
        Method testMethod = MyClass.class.getDeclaredMethod("testMethod");
        Object testObject = new MyClass();

        ClassWrapper classWrapper = new ClassWrapper(MyClass.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        MyTestAnnotation1 methodAnnotation1 = testMethod.getAnnotation(MyTestAnnotation1.class);
        Annotations<MyTestAnnotation1> annotations1 = new Annotations<MyTestAnnotation1>(methodAnnotation1, new ArrayList<MyTestAnnotation1>(), configuration);

        MyTestAnnotation2 methodAnnotation2 = testMethod.getAnnotation(MyTestAnnotation2.class);
        Annotations<MyTestAnnotation2> annotations2 = new Annotations<MyTestAnnotation2>(methodAnnotation2, new ArrayList<MyTestAnnotation2>(), configuration);

        List<WrapperForTestAnnotationListener> result = wrapperForTestAnnotationListenerFactory.create(testInstance);
        assertEquals(2, result.size());

        WrapperForTestAnnotationListener listener1 = result.get(0);
        WrapperForTestAnnotationListener listener2 = result.get(1);
        assertTrue((listener1.testAnnotationListener instanceof MyTestAnnotationListener1 && listener2.testAnnotationListener instanceof MyTestAnnotationListener2) ||
                (listener2.testAnnotationListener instanceof MyTestAnnotationListener1 && listener1.testAnnotationListener instanceof MyTestAnnotationListener2));
        assertTrue((methodAnnotation1.equals(listener1.annotations.getAnnotation()) && methodAnnotation2.equals(listener2.annotations.getAnnotation())) ||
                (methodAnnotation1.equals(listener2.annotations.getAnnotation()) && methodAnnotation2.equals(listener1.annotations.getAnnotation())));
        assertTrue(listener1.annotations.getClassAnnotations().isEmpty());
        assertTrue(listener2.annotations.getClassAnnotations().isEmpty());
    }

    @Test
    public void classAnnotation() throws Exception {
        Method testMethod = AnnotationOnClass.class.getDeclaredMethod("testMethod");
        Object testObject = new AnnotationOnClass();

        ClassWrapper classWrapper = new ClassWrapper(AnnotationOnClass.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);
        MyTestAnnotation1 classAnnotation = AnnotationOnClass.class.getAnnotation(MyTestAnnotation1.class);
        MyTestAnnotation1 superClassAnnotation = AnnotationOnSuperClass.class.getAnnotation(MyTestAnnotation1.class);

        List<WrapperForTestAnnotationListener> result = wrapperForTestAnnotationListenerFactory.create(testInstance);
        assertEquals(1, result.size());

        WrapperForTestAnnotationListener listener1 = result.get(0);
        assertTrue(listener1.testAnnotationListener instanceof MyTestAnnotationListener1);
        assertNull(listener1.annotations.getAnnotation());
        assertEquals(asList(classAnnotation, superClassAnnotation), listener1.annotations.getClassAnnotations());
    }

    @Test
    public void classAndMethodAnnotation() throws Exception {
        Method testMethod = AnnotationOnClassAndMethod.class.getDeclaredMethod("testMethod");
        Object testObject = new AnnotationOnClassAndMethod();

        ClassWrapper classWrapper = new ClassWrapper(AnnotationOnClassAndMethod.class);
        TestInstance testInstance = new TestInstance(classWrapper, null, testMethod);
        MyTestAnnotation1 methodAnnotation = testMethod.getAnnotation(MyTestAnnotation1.class);
        MyTestAnnotation1 classAnnotation = AnnotationOnClassAndMethod.class.getAnnotation(MyTestAnnotation1.class);
        MyTestAnnotation1 superClassAnnotation = AnnotationOnSuperClass.class.getAnnotation(MyTestAnnotation1.class);

        List<WrapperForTestAnnotationListener> result = wrapperForTestAnnotationListenerFactory.create(testInstance);
        assertEquals(1, result.size());

        WrapperForTestAnnotationListener listener1 = result.get(0);
        assertTrue(listener1.testAnnotationListener instanceof MyTestAnnotationListener1);
        assertEquals(methodAnnotation, listener1.annotations.getAnnotation());
        assertEquals(asList(classAnnotation, superClassAnnotation), listener1.annotations.getClassAnnotations());
    }

    @Test
    public void otherAnnotationsAreIgnored() throws Exception {
        Method testMethod = OtherAnnotation.class.getDeclaredMethod("testMethod");
        Object testObject = new OtherAnnotation();

        ClassWrapper classWrapper = new ClassWrapper(OtherAnnotation.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        List<WrapperForTestAnnotationListener> result = wrapperForTestAnnotationListenerFactory.create(testInstance);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoAnnotation() throws Exception {
        Method testMethod = NoAnnotation.class.getDeclaredMethod("testMethod");
        Object testObject = new NoAnnotation();

        ClassWrapper classWrapper = new ClassWrapper(NoAnnotation.class);
        TestInstance testInstance = new TestInstance(classWrapper, testObject, testMethod);

        List<WrapperForTestAnnotationListener> result = wrapperForTestAnnotationListenerFactory.create(testInstance);
        assertTrue(result.isEmpty());
    }


    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    @TestAnnotation(MyTestAnnotationListener1.class)
    public static @interface MyTestAnnotation1 {
    }

    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    @TestAnnotation(MyTestAnnotationListener2.class)
    public static @interface MyTestAnnotation2 {
    }

    @Target({METHOD, TYPE})
    @Retention(RUNTIME)
    public static @interface NotATestAnnotation {
    }

    public static class MyTestAnnotationListener1 extends TestAnnotationListener<MyTestAnnotation1> {
    }

    public static class MyTestAnnotationListener2 extends TestAnnotationListener<MyTestAnnotation2> {
    }


    public static class MyClass {

        @MyTestAnnotation1
        @MyTestAnnotation2
        public void testMethod() {
        }
    }

    @MyTestAnnotation1
    private static class AnnotationOnSuperClass {
    }

    @MyTestAnnotation1
    private static class AnnotationOnClass extends AnnotationOnSuperClass {

        public void testMethod() {
        }
    }

    @MyTestAnnotation1
    private static class AnnotationOnClassAndMethod extends AnnotationOnSuperClass {

        @MyTestAnnotation1
        public void testMethod() {
        }
    }


    @NotATestAnnotation
    private static class OtherAnnotation {

        @NotATestAnnotation
        public void testMethod() {
        }
    }

    private static class NoAnnotation {

        public void testMethod() {
        }
    }
}
