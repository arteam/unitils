/*
 * Copyright 2011,  Unitils.org
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

package org.unitils.io.annotation.handler;

import org.unitils.core.TestListener;

import java.lang.reflect.Method;

/**
 * The composite annotation handler makes it possible to combine multipele TestListeners into one.
 * Since each module can only have one {@link TestListener} (for now), this class allows you to use multiple ones on.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class CompositeAnnotationHandler extends TestListener {

    private TestListener[] testListeners;

    public CompositeAnnotationHandler(TestListener... testListeners) {
        this.testListeners = testListeners;
    }

    @Override
    public void beforeTestClass(Class<?> testClass) {
        for (TestListener listener : testListeners) {
            listener.beforeTestClass(testClass);
        }

    }

    @Override
    public void afterCreateTestObject(Object testObject) {
        for (TestListener listener : testListeners) {
            listener.afterCreateTestObject(testObject);
        }

    }

    @Override
    public void beforeTestSetUp(Object testObject, Method testMethod) {
        for (TestListener listener : testListeners) {
            listener.beforeTestSetUp(testObject, testMethod);
        }
    }

    @Override
    public void beforeTestMethod(Object testObject, Method testMethod) {
        for (TestListener listener : testListeners) {
            listener.beforeTestMethod(testObject, testMethod);
        }
    }

    @Override
    public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable) {
        for (TestListener listener : testListeners) {
            listener.afterTestMethod(testObject, testMethod, testThrowable);
        }
    }

    @Override
    public void afterTestTearDown(Object testObject, Method testMethod) {
        for (TestListener listener : testListeners) {
            listener.afterTestTearDown(testObject, testMethod);
        }
    }

}
