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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.reflect.Annotations;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.core.MockService;
import org.unitils.easymock.util.*;
import org.unitils.mock.annotation.Dummy;

import java.util.List;


/**
 * @author Tim Ducheyne
 */
public class MockFieldAnnotationListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    private MockFieldAnnotationListener mockFieldAnnotationListener;

    private org.unitils.mock.Mock<TestInstance> testInstanceMock;
    private org.unitils.mock.Mock<TestField> testFieldMock;
    private org.unitils.mock.Mock<Annotations<Mock>> annotationsMock;
    private org.unitils.mock.Mock<MockService> mockServiceMock;

    private Mock annotation1;
    private Mock annotation2;

    @Dummy
    private MyClass testObject;
    @Dummy
    private MyClass mock;


    @Before
    public void initialize() throws Exception {
        mockFieldAnnotationListener = new MockFieldAnnotationListener(mockServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(Mock.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(Mock.class);

        testInstanceMock.returns(testObject).getTestObject();
        testFieldMock.returns(List.class).getType();
        testFieldMock.returns("fieldName").getName();
    }


    @Test
    public void beforeTestSetUp() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        mockServiceMock.returns(mock).createMock(List.class, InvocationOrder.STRICT, Calls.STRICT, Order.STRICT, Dates.STRICT, Defaults.STRICT);

        mockFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(mock);
        mockServiceMock.assertInvoked().callAfterCreateMockMethods(testObject, mock, "fieldName", List.class);
    }

    @Test
    public void allDefaults() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        mockServiceMock.returns(mock).createMock(List.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.DEFAULT);

        mockFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(mock);
        mockServiceMock.assertInvoked().callAfterCreateMockMethods(testObject, mock, "fieldName", List.class);
    }


    public static class MyClass {

        @Mock(invocationOrder = InvocationOrder.STRICT, calls = Calls.STRICT, order = Order.STRICT, dates = Dates.STRICT, defaults = Defaults.STRICT)
        private List field1;

        @Mock
        private List field2;
    }
}