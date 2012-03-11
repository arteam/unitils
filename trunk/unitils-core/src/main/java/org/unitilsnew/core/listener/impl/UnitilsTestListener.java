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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTestListener {

    protected List<TestListener> testListeners;
    protected Context context;

    protected TestClass currentTestClass;
    protected TestInstance currentTestInstance;
    protected SortedSet<TestListener> currentTestListeners;


    public UnitilsTestListener(List<TestListener> testListeners, Context context) {
        this.testListeners = testListeners;
        this.context = context;
    }

    // todo test  testphases + testlisteners


    public void beforeTestClass(Class<?> testClass) {
        currentTestClass = new TestClass(testClass);
        currentTestInstance = null;

        currentTestListeners = new TreeSet<TestListener>(new TestListenerTestPhaseComparator());
        currentTestListeners.addAll(testListeners);

        for (TestListener testListener : currentTestListeners) {
            testListener.beforeTestClass(currentTestClass);
        }
    }

    public void beforeTestSetUp(Object testObject, Method testMethod) {
        currentTestInstance = new TestInstance(currentTestClass, testObject, testMethod);

        addFieldAnnotationListeners(currentTestInstance, currentTestListeners);
        addTestAnnotationListeners(currentTestInstance, currentTestListeners);

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


    protected void addFieldAnnotationListeners(TestInstance testInstance, SortedSet<TestListener> testListeners) {
        List<TestField> testFields = testInstance.getTestFields();
        for (TestField testField : testFields) {
            List<Annotation> annotations = testField.getAnnotations();
            for (Annotation annotation : annotations) {
                TestListener testListener = createFieldAnnotationTestListener(annotation, testInstance, testField);
                if (testListener != null) {
                    testListeners.add(testListener);
                }
            }
        }
    }

    protected void addTestAnnotationListeners(TestInstance testInstance, SortedSet<TestListener> testListeners) {
        List<Annotation> classAnnotations = testInstance.getClassAnnotations();
        List<Annotation> methodAnnotations = testInstance.getMethodAnnotations();

        List<Annotation> classAndMethodAnnotations = new ArrayList<Annotation>();
        classAndMethodAnnotations.addAll(classAnnotations);
        classAndMethodAnnotations.addAll(methodAnnotations);

        for (Annotation annotation : classAndMethodAnnotations) {
            TestListener testListener = createTestAnnotationListeners(annotation, testInstance);
            if (testListener != null) {
                testListeners.add(testListener);
            }
        }
    }


    @SuppressWarnings("unchecked")
    protected <A extends Annotation> TestListener createFieldAnnotationTestListener(A annotation, TestInstance testInstance, TestField testField) {
        Class<A> annotationType = (Class<A>) annotation.annotationType();
        List<A> classAnnotations = testInstance.getClassAnnotations(annotationType);
        FieldAnnotation fieldAnnotation = annotationType.getAnnotation(FieldAnnotation.class);
        if (fieldAnnotation == null) {
            return null;
        }
        Class<? extends FieldAnnotationListener<A>> fieldAnnotationListenerType = (Class<FieldAnnotationListener<A>>) fieldAnnotation.value();
        FieldAnnotationListener<A> fieldAnnotationListener = context.getInstanceOfType(fieldAnnotationListenerType);

        Annotations<A> annotations = new Annotations<A>(annotation, classAnnotations, context.getConfiguration());
        return new WrapperForFieldAnnotationListener<A>(testField, annotations, fieldAnnotationListener);
    }

    @SuppressWarnings("unchecked")
    protected <A extends Annotation> TestListener createTestAnnotationListeners(A annotation, TestInstance testInstance) {
        Class<A> annotationType = (Class<A>) annotation.annotationType();
        List<A> classAnnotations = testInstance.getClassAnnotations(annotationType);
        TestAnnotation testAnnotation = annotationType.getAnnotation(TestAnnotation.class);
        if (testAnnotation == null) {
            return null;
        }

        Class<? extends TestAnnotationListener<A>> testAnnotationListenerType = (Class<TestAnnotationListener<A>>) testAnnotation.value();
        TestAnnotationListener<A> testAnnotationListener = context.getInstanceOfType(testAnnotationListenerType);

        Annotations<A> annotations = new Annotations<A>(annotation, classAnnotations, context.getConfiguration());
        return new WrapperForTestAnnotationListener<A>(annotations, testAnnotationListener);
    }
}
