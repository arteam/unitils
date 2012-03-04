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

import org.unitilsnew.core.*;
import org.unitilsnew.core.annotation.FieldAnnotation;
import org.unitilsnew.core.annotation.TestAnnotation;
import org.unitilsnew.core.listener.FieldAnnotationListener;
import org.unitilsnew.core.listener.TestAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTestListener {

    private List<TestListener> moduleTestListeners;
    private Context context;

    private TestClass currentTestClass;
    private TestInstance currentTestInstance;
    private List<TestListener> currentTestListeners;

    private TestListenerTestPhaseComparator testListenerTestPhaseComparator = new TestListenerTestPhaseComparator();


    public UnitilsTestListener(List<TestListener> moduleTestListeners, Context context) {
        this.moduleTestListeners = moduleTestListeners;
        this.context = context;

        Collections.sort(moduleTestListeners, testListenerTestPhaseComparator);
    }


    public void beforeTestClass(Class<?> testClass) {
        currentTestClass = new TestClass(testClass);
        currentTestInstance = null;
        currentTestListeners = new ArrayList<TestListener>(moduleTestListeners);

        addFieldAnnotationListeners(currentTestClass, currentTestListeners);
        Collections.sort(currentTestListeners, testListenerTestPhaseComparator);

        for (TestListener testListener : currentTestListeners) {
            testListener.beforeTestClass(currentTestClass);
        }
    }

    public void beforeTestSetUp(Object testObject, Method testMethod) {
        currentTestInstance = new TestInstance(currentTestClass, testObject, testMethod);

        addTestAnnotationListeners(currentTestInstance, currentTestListeners);
        Collections.sort(currentTestListeners, testListenerTestPhaseComparator);

        for (TestListener testListener : currentTestListeners) {
            testListener.beforeTestSetUp(currentTestInstance);
        }
    }

    public void beforeTestMethod() {
        for (TestListener testListener : currentTestListeners) {
            testListener.beforeTestMethod(currentTestInstance);
        }
    }

    public void afterTestMethod(Throwable testThrowable) {
        for (TestListener testListener : currentTestListeners) {
            testListener.afterTestMethod(currentTestInstance, testThrowable);
        }
    }

    public void afterTestTearDown() {
        for (TestListener testListener : currentTestListeners) {
            testListener.afterTestTearDown(currentTestInstance);
        }
    }


    protected void addFieldAnnotationListeners(TestClass testClass, List<TestListener> testListeners) {
        List<Class<? extends Annotation>> annotationTypes = testClass.getAnnotationTypesAnnotatedWith(FieldAnnotation.class);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            addFieldAnnotationTestListeners(testClass, annotationType, testListeners);
        }
    }

    protected <A extends Annotation> void addFieldAnnotationTestListeners(TestClass testClass, Class<A> annotationType, List<TestListener> testListeners) {
        FieldAnnotation fieldAnnotation = annotationType.getAnnotation(FieldAnnotation.class);

        Class<? extends FieldAnnotationListener<A>> fieldAnnotationListenerType = (Class<FieldAnnotationListener<A>>) fieldAnnotation.value();
        FieldAnnotationListener<A> fieldAnnotationListener = context.getInstanceOfType(fieldAnnotationListenerType);

        List<A> classAnnotations = testClass.getClassAnnotations(annotationType);
        List<AnnotatedField<A>> annotatedFields = testClass.getAnnotatedFields(annotationType);
        for (AnnotatedField<A> annotatedField : annotatedFields) {
            Annotation annotation = annotatedField.getAnnotation();
            Field field = annotatedField.getField();
            Annotations<A> annotations = new Annotations<A>(annotatedField.getAnnotation(), classAnnotations, context.getConfiguration());

            WrapperForFieldAnnotationListener<A> wrapperForFieldAnnotationListener = new WrapperForFieldAnnotationListener<A>(field, annotations, fieldAnnotationListener);
            testListeners.add(wrapperForFieldAnnotationListener);
        }
    }

    protected void addTestAnnotationListeners(TestInstance testInstance, List<TestListener> testListeners) {
        Set<Class<? extends Annotation>> annotationTypes = testInstance.getAnnotationTypesAnnotatedWith(TestAnnotation.class);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            addTestAnnotationListeners(testInstance, annotationType, testListeners);
        }
    }

    private <A extends Annotation> void addTestAnnotationListeners(TestInstance testInstance, Class<A> annotationType, List<TestListener> testListeners) {
        TestAnnotation testAnnotation = annotationType.getAnnotation(TestAnnotation.class);

        Class<? extends TestAnnotationListener<A>> testAnnotationListenerType = (Class<TestAnnotationListener<A>>) testAnnotation.value();
        TestAnnotationListener<A> testAnnotationListener = context.getInstanceOfType(testAnnotationListenerType);

        List<A> classAnnotations = testInstance.getTestClass().getClassAnnotations(annotationType);
        A annotation = testInstance.getMethodAnnotation(annotationType);
        Annotations<A> annotations = new Annotations<A>(annotation, classAnnotations, context.getConfiguration());

        WrapperForTestAnnotationListener<A> wrapperForTestAnnotationListener = new WrapperForTestAnnotationListener<A>(annotations, testAnnotationListener);
        testListeners.add(wrapperForTestAnnotationListener);
    }
}
