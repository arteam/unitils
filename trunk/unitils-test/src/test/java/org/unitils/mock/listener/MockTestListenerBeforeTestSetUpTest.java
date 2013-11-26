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
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.MockFactory;

import java.util.Properties;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class MockTestListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    private MockTestListener mockTestListener;

    private Mock<MockFactory> mockFactoryMock;
    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock1;
    private Mock<TestField> testFieldMock2;
    private Mock<Mock<TestInterface1>> testInterface1MockMock;
    private Mock<Mock<TestInterface2>> testInterface2MockMock;
    @Dummy
    private Properties testObject;


    @Before
    public void initialize() {
        mockTestListener = new MockTestListener(mockFactoryMock.getMock());

        testInstanceMock.returns(testObject).getTestObject();
        // todo td  make it possible to re-write this as
        // testInstanceMock.returnsList(testFieldMock1, testFieldMock2).getTestFieldsOfType(Mock.class);
        testInstanceMock.returns(asList(testFieldMock1.getMock(), testFieldMock2.getMock())).getTestFieldsOfType(Mock.class);
        testFieldMock1.returns("field1").getName();
        testFieldMock1.returns(TestInterface1.class).getSingleGenericClass();
        testFieldMock2.returns("field2").getName();
        testFieldMock2.returns(TestInterface2.class).getSingleGenericClass();
    }


    @Test
    public void createMocksForFieldsOfTypeMock() {
        mockFactoryMock.returns(testInterface1MockMock).createMock("field1", TestInterface1.class, testObject);
        mockFactoryMock.returns(testInterface2MockMock).createMock("field2", TestInterface2.class, testObject);

        mockTestListener.beforeTestSetUp(testInstanceMock.getMock());
        testFieldMock1.assertInvoked().setValue(testInterface1MockMock.getMock());
        testFieldMock2.assertInvoked().setValue(testInterface2MockMock.getMock());
    }

    @Test
    public void resetMocksIfFieldAlreadyHaveMockValues() {
        testFieldMock1.returns(testInterface1MockMock).getValue();
        testFieldMock2.returns(testInterface2MockMock).getValue();

        mockTestListener.beforeTestSetUp(testInstanceMock.getMock());
        testInterface1MockMock.assertInvoked().resetBehavior();
        testInterface2MockMock.assertInvoked().resetBehavior();
    }

    @Test
    public void ignoredWhenNoTestFields() {
        testInstanceMock.returns(emptyList()).getTestFieldsOfType(Mock.class);

        mockTestListener.beforeTestSetUp(testInstanceMock.getMock());
        testFieldMock1.assertNotInvoked().setValue(null);
        testFieldMock2.assertNotInvoked().setValue(null);
    }

    @Test
    public void exceptionWhenNoGenericParameter() {
        testFieldMock1.raises(new UnitilsException("expected")).getSingleGenericClass();
        try {
            mockTestListener.beforeTestSetUp(testInstanceMock.getMock());
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine type of mock for field field1. A mock should be declared using the generic Mock<YourTypeToMock> type.\n" +
                    "Reason: expected", e.getMessage());
        }
    }


    private interface TestInterface1 {
    }

    private interface TestInterface2 {
    }
}