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
import org.unitils.easymock.annotation.RegularMock;
import org.unitils.easymock.core.MockService;
import org.unitils.easymock.util.Calls;
import org.unitils.easymock.util.InvocationOrder;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class RegularMockFieldAnnotationListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    private RegularMockFieldAnnotationListener regularMockFieldAnnotationListener;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<RegularMock>> annotationsMock;
    private Mock<MockService> mockServiceMock;

    private RegularMock annotation1;
    private RegularMock annotation2;

    @Dummy
    private MyClass testObject;
    @Dummy
    private MyClass mock;


    @Before
    public void initialize() throws Exception {
        regularMockFieldAnnotationListener = new RegularMockFieldAnnotationListener(mockServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(RegularMock.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(RegularMock.class);

        testInstanceMock.returns(testObject).getTestObject();
        testFieldMock.returns(List.class).getType();
        testFieldMock.returns("fieldName").getName();
    }


    @Test
    public void beforeTestSetUp() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        mockServiceMock.returns(mock).createRegularMock(List.class, InvocationOrder.STRICT, Calls.STRICT);

        regularMockFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(mock);
        mockServiceMock.assertInvoked().callAfterCreateMockMethods(testObject, mock, "fieldName", List.class);
    }

    @Test
    public void allDefaults() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        mockServiceMock.returns(mock).createRegularMock(List.class, InvocationOrder.DEFAULT, Calls.DEFAULT);

        regularMockFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(mock);
        mockServiceMock.assertInvoked().callAfterCreateMockMethods(testObject, mock, "fieldName", List.class);
    }


    public static class MyClass {

        @RegularMock(invocationOrder = InvocationOrder.STRICT, calls = Calls.STRICT)
        private List field1;

        @RegularMock
        private List field2;
    }
}