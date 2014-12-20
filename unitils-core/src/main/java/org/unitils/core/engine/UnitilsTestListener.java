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

import org.unitils.core.TestInstance;
import org.unitils.core.TestListener;
import org.unitils.core.reflect.ClassWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListener {

    protected List<TestListener> testListeners;
    protected FieldAnnotationTestListenerFactory fieldAnnotationTestListenerFactory;
    protected TestAnnotationTestListenerFactory testAnnotationTestListenerFactory;
    protected TestListenerTestPhaseComparator testListenerTestPhaseComparator = new TestListenerTestPhaseComparator();

    protected ClassWrapper currentClassWrapper;
    protected TestInstance currentTestInstance;
    protected Throwable currentTestThrowable;
    protected List<TestListener> onlyTestListeners;
    protected List<TestListener> testAndAnnotationTestListeners;


    public UnitilsTestListener(List<TestListener> testListeners, FieldAnnotationTestListenerFactory fieldAnnotationTestListenerFactory, TestAnnotationTestListenerFactory testAnnotationTestListenerFactory) {
        this.testListeners = testListeners;
        this.fieldAnnotationTestListenerFactory = fieldAnnotationTestListenerFactory;
        this.testAnnotationTestListenerFactory = testAnnotationTestListenerFactory;
    }


    public void beforeTestClass(Class<?> testClass) {
        currentClassWrapper = new ClassWrapper(testClass);
        currentTestInstance = null;
        currentTestThrowable = null;

        onlyTestListeners = new ArrayList<TestListener>(testListeners);
        sort(onlyTestListeners, testListenerTestPhaseComparator);

        for (TestListener testListener : onlyTestListeners) {
            testListener.beforeTestClass(currentClassWrapper);
        }
    }

    public void beforeTestSetUp(Object testObject, Method testMethod) {
        currentTestInstance = new TestInstance(currentClassWrapper, testObject, testMethod);
        currentTestThrowable = null;

        testAndAnnotationTestListeners = new ArrayList<TestListener>(onlyTestListeners);
        addFieldAndTestAnnotationListeners(currentTestInstance, testAndAnnotationTestListeners);
        sort(testAndAnnotationTestListeners, testListenerTestPhaseComparator);

        for (TestListener testListener : testAndAnnotationTestListeners) {
            testListener.beforeTestSetUp(currentTestInstance);
        }
    }

    public void beforeTestMethod() {
        for (TestListener testListener : testAndAnnotationTestListeners) {
            testListener.beforeTestMethod(currentTestInstance);
        }
    }

    public void afterTestMethod(Throwable testThrowable) {
        currentTestThrowable = testThrowable;

        for (TestListener testListener : testAndAnnotationTestListeners) {
            testListener.afterTestMethod(currentTestInstance, currentTestThrowable);
        }
    }

    public void afterTestTearDown() {
        for (TestListener testListener : testAndAnnotationTestListeners) {
            testListener.afterTestTearDown(currentTestInstance, currentTestThrowable);
        }
    }

    public void afterTestClass() {
        for (TestListener testListener : onlyTestListeners) {
            testListener.afterTestClass(currentClassWrapper);
        }
    }


    protected void addFieldAndTestAnnotationListeners(TestInstance currentTestInstance, List<TestListener> currentTestListeners) {
        List<TestListener> fieldAnnotationTestListeners = fieldAnnotationTestListenerFactory.create(currentTestInstance);
        List<TestListener> testAnnotationTestListeners = testAnnotationTestListenerFactory.create(currentTestInstance);

        currentTestListeners.addAll(fieldAnnotationTestListeners);
        currentTestListeners.addAll(testAnnotationTestListeners);
    }
}
