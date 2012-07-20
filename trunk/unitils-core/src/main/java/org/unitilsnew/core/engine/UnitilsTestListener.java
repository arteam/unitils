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

import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestListener;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListener {

    protected List<TestListener> testListeners;
    protected WrapperForFieldAnnotationListenerFactory wrapperForFieldAnnotationListenerFactory;
    protected WrapperForTestAnnotationListenerFactory wrapperForTestAnnotationListenerFactory;
    protected TestListenerTestPhaseComparator testListenerTestPhaseComparator = new TestListenerTestPhaseComparator();

    protected ClassWrapper currentClassWrapper;
    protected TestInstance currentTestInstance;
    protected Throwable currentTestThrowable;
    protected List<TestListener> currentTestListeners;


    public UnitilsTestListener(List<TestListener> testListeners, WrapperForFieldAnnotationListenerFactory wrapperForFieldAnnotationListenerFactory, WrapperForTestAnnotationListenerFactory wrapperForTestAnnotationListenerFactory) {
        this.testListeners = testListeners;
        this.wrapperForFieldAnnotationListenerFactory = wrapperForFieldAnnotationListenerFactory;
        this.wrapperForTestAnnotationListenerFactory = wrapperForTestAnnotationListenerFactory;
    }


    public void beforeTestClass(Class<?> testClass) {
        currentClassWrapper = new ClassWrapper(testClass);
        currentTestInstance = null;
        currentTestThrowable = null;

        currentTestListeners = new ArrayList<TestListener>(testListeners);
        sort(currentTestListeners, testListenerTestPhaseComparator);

        for (TestListener testListener : currentTestListeners) {
            testListener.beforeTestClass(currentClassWrapper);
        }
    }

    public void beforeTestSetUp(Object testObject, Method testMethod) {
        currentTestInstance = new TestInstance(currentClassWrapper, testObject, testMethod);
        currentTestThrowable = null;

        currentTestListeners = new ArrayList<TestListener>(testListeners);
        addFieldAndTestAnnotationListeners(currentTestInstance, currentTestListeners);
        sort(currentTestListeners, testListenerTestPhaseComparator);

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
        currentTestThrowable = testThrowable;

        for (TestListener testListener : currentTestListeners) {
            testListener.afterTestMethod(currentTestInstance, currentTestThrowable);
        }
    }

    public void afterTestTearDown() {
        for (TestListener testListener : currentTestListeners) {
            testListener.afterTestTearDown(currentTestInstance, currentTestThrowable);
        }
    }


    protected void addFieldAndTestAnnotationListeners(TestInstance currentTestInstance, List<TestListener> currentTestListeners) {
        List<WrapperForFieldAnnotationListener> fieldAnnotationTestListeners = wrapperForFieldAnnotationListenerFactory.create(currentTestInstance);
        List<WrapperForTestAnnotationListener> testAnnotationTestListeners = wrapperForTestAnnotationListenerFactory.create(currentTestInstance);

        currentTestListeners.addAll(fieldAnnotationTestListeners);
        currentTestListeners.addAll(testAnnotationTestListeners);
    }
}
