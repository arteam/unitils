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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.MockFactory;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class PartialMockTestListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    private PartialMockTestListener partialMockTestListener;

    private Mock<MockFactory> mockFactoryMock;
    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock1;
    private Mock<TestField> testFieldMock2;
    private Mock<PartialMock<TestInterface1>> testInterface1PartialMockMock;
    private Mock<PartialMock<TestInterface2>> testInterface2PartialMockMock;
    @Dummy
    private Properties testObject;


    @Before
    public void initialize() {
        partialMockTestListener = new PartialMockTestListener(mockFactoryMock.getMock());

        testInstanceMock.returns(testObject).getTestObject();
        testInstanceMock.returnsAll(testFieldMock1, testFieldMock2).getTestFieldsOfType(PartialMock.class);
        testFieldMock1.returns("field1").getName();
        testFieldMock1.returns(TestInterface1.class).getSingleGenericClass();
        testFieldMock2.returns("field2").getName();
        testFieldMock2.returns(TestInterface2.class).getSingleGenericClass();
    }


    @Test
    public void createMocksForFieldsOfTypeMock() {
        mockFactoryMock.returns(testInterface1PartialMockMock).createPartialMock("field1", TestInterface1.class, testObject);
        mockFactoryMock.returns(testInterface2PartialMockMock).createPartialMock("field2", TestInterface2.class, testObject);

        partialMockTestListener.beforeTestSetUp(testInstanceMock.getMock());
        testFieldMock1.assertInvoked().setValue(testInterface1PartialMockMock.getMock());
        testFieldMock2.assertInvoked().setValue(testInterface2PartialMockMock.getMock());
    }

    @Test
    public void resetMocksIfFieldAlreadyHaveMockValues() {
        testFieldMock1.returns(testInterface1PartialMockMock).getValue();
        testFieldMock2.returns(testInterface2PartialMockMock).getValue();

        partialMockTestListener.beforeTestSetUp(testInstanceMock.getMock());
        testInterface1PartialMockMock.assertInvoked().resetBehavior();
        testInterface2PartialMockMock.assertInvoked().resetBehavior();
    }

    @Test
    public void ignoredWhenNoTestFields() {
        testInstanceMock.onceReturnsAll().getTestFieldsOfType(PartialMock.class);

        partialMockTestListener.beforeTestSetUp(testInstanceMock.getMock());
        testFieldMock1.assertNotInvoked().setValue(null);
        testFieldMock2.assertNotInvoked().setValue(null);
    }

    @Test
    public void exceptionWhenNoGenericParameter() {
        testFieldMock1.raises(new UnitilsException("expected")).getSingleGenericClass();
        try {
            partialMockTestListener.beforeTestSetUp(testInstanceMock.getMock());
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine type of mock for field field1. A mock should be declared using the generic PartialMock<YourTypeToMock> type.\n" +
                    "Reason: expected", e.getMessage());
        }
    }


    private interface TestInterface1 {
    }

    private interface TestInterface2 {
    }
}