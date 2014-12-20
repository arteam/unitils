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

import org.unitils.core.Factory;
import org.unitils.core.TestListener;
import org.unitils.core.context.UnitilsContext;
import org.unitils.core.spring.SpringTestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerFactory implements Factory<UnitilsTestListener> {

    protected UnitilsContext unitilsContext;
    protected FieldAnnotationTestListenerFactory fieldAnnotationTestListenerFactory;
    protected TestAnnotationTestListenerFactory testAnnotationTestListenerFactory;
    protected SpringTestListener springTestListener;


    public UnitilsTestListenerFactory(UnitilsContext unitilsContext, FieldAnnotationTestListenerFactory fieldAnnotationTestListenerFactory, TestAnnotationTestListenerFactory testAnnotationTestListenerFactory, SpringTestListener springTestListener) {
        this.unitilsContext = unitilsContext;
        this.fieldAnnotationTestListenerFactory = fieldAnnotationTestListenerFactory;
        this.testAnnotationTestListenerFactory = testAnnotationTestListenerFactory;
        this.springTestListener = springTestListener;
    }


    public UnitilsTestListener create() {
        List<TestListener> testListeners = createTestListeners();
        return new UnitilsTestListener(testListeners, fieldAnnotationTestListenerFactory, testAnnotationTestListenerFactory);
    }


    protected List<TestListener> createTestListeners() {
        List<TestListener> testListeners = new ArrayList<TestListener>();
        testListeners.add(springTestListener);
        for (Class<?> testListenerType : unitilsContext.getTestListenerTypes()) {
            TestListener testListener = (TestListener) unitilsContext.getInstanceOfType(testListenerType);
            testListeners.add(testListener);
        }
        return testListeners;
    }
}
