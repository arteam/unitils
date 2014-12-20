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
package org.unitils.easymock.listener;

import org.unitils.core.FieldAnnotationListener;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.TestPhase;
import org.unitils.core.reflect.Annotations;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.core.MockService;
import org.unitils.easymock.util.*;

import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class MockFieldAnnotationListener extends FieldAnnotationListener<Mock> {

    protected MockService mockService;


    public MockFieldAnnotationListener(MockService mockService) {
        this.mockService = mockService;
    }


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<Mock> annotations) {
        Mock annotation = annotations.getAnnotationWithDefaults();

        Object testObject = testInstance.getTestObject();
        Class<?> mockType = testField.getType();
        String fieldName = testField.getName();

        InvocationOrder invocationOrder = annotation.invocationOrder();
        Calls calls = annotation.calls();
        Order order = annotation.order();
        Dates dates = annotation.dates();
        Defaults defaults = annotation.defaults();

        Object mock = mockService.createMock(mockType, invocationOrder, calls, order, dates, defaults);
        testField.setValue(mock);
        mockService.callAfterCreateMockMethods(testObject, mock, fieldName, mockType);
    }
}
