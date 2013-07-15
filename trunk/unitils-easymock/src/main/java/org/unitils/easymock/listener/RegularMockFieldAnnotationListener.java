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
import org.unitils.easymock.annotation.RegularMock;
import org.unitils.easymock.core.MockService;
import org.unitils.easymock.util.Calls;
import org.unitils.easymock.util.InvocationOrder;

import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class RegularMockFieldAnnotationListener extends FieldAnnotationListener<RegularMock> {

    protected MockService mockService;


    public RegularMockFieldAnnotationListener(MockService mockService) {
        this.mockService = mockService;
    }


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<RegularMock> annotations) {
        RegularMock annotation = annotations.getAnnotationWithDefaults();

        Object testObject = testInstance.getTestObject();
        Class<?> mockType = testField.getType();
        String fieldName = testField.getName();

        InvocationOrder invocationOrder = annotation.invocationOrder();
        Calls calls = annotation.calls();

        Object mock = mockService.createRegularMock(mockType, invocationOrder, calls);
        testField.setValue(mock);
        mockService.callAfterCreateMockMethods(testObject, mock, fieldName, mockType);
    }
}
