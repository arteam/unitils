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

import org.unitilsnew.core.TestClass;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.listener.TestListener;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CompositeTestListener extends TestListener {

    private List<TestListener> testListeners;


    public CompositeTestListener(List<TestListener> testListeners) {
        this.testListeners = testListeners;
    }


    @Override
    public void beforeTestClass(TestClass testClass) {
        for (TestListener testListener : testListeners) {
            testListener.beforeTestClass(testClass);
        }
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        for (TestListener testListener : testListeners) {
            testListener.beforeTestSetUp(testInstance);
        }
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        for (TestListener testListener : testListeners) {
            testListener.beforeTestMethod(testInstance);
        }
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        for (TestListener testListener : testListeners) {
            testListener.afterTestMethod(testInstance, testThrowable);
        }
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance) {
        for (TestListener testListener : testListeners) {
            testListener.afterTestTearDown(testInstance);
        }
    }

}
