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

package org.unitils.mock.listener;

import org.unitils.core.*;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.core.MockFactory;

import java.util.List;

import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class PartialMockTestListener extends TestListener {

    protected MockFactory mockFactory;


    public PartialMockTestListener(MockFactory mockFactory) {
        this.mockFactory = mockFactory;
    }


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        List<TestField> partialMockTestFields = testInstance.getTestFieldsOfType(PartialMock.class);
        for (TestField partialMockTestField : partialMockTestFields) {
            Mock<?> mock = partialMockTestField.getValue();
            if (mock != null) {
                mock.resetBehavior();
                continue;
            }
            mock = createPartialMock(partialMockTestField, testInstance);
            partialMockTestField.setValue(mock);
        }
    }


    protected Mock<?> createPartialMock(TestField testField, TestInstance testInstance) {
        String mockName = testField.getName();
        Class<?> mockedType = getMockedClass(testField);
        Object testObject = testInstance.getTestObject();
        return mockFactory.createPartialMock(mockName, mockedType, testObject);
    }

    protected Class<?> getMockedClass(TestField testField) {
        try {
            return testField.getSingleGenericClass();
        } catch (UnitilsException e) {
            throw new UnitilsException("Unable to determine type of mock for field " + testField.getName() + ". A mock should be declared using the generic PartialMock<YourTypeToMock> type.", e);
        }
    }
}
