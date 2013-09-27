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
import org.unitils.core.reflect.Annotations;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.DummyService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class DummyFieldAnnotationListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    private DummyFieldAnnotationListener dummyFieldAnnotationListener;

    private Mock<TestField> testFieldMock;
    private Mock<Annotations<Dummy>> annotationsMock;
    private Mock<DummyService> dummyServiceMock;

    private Dummy annotation1;

    private List<?> dummyList;


    @Before
    public void initialize() {
        dummyFieldAnnotationListener = new DummyFieldAnnotationListener(dummyServiceMock.getMock());

        dummyList = new ArrayList<Object>();
        dummyServiceMock.returns(dummyList).createDummy("field name", List.class);

        testFieldMock.returns("field name").getName();
        testFieldMock.returns(List.class).getType();
    }


    @Test
    public void beforeTestSetUp() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        dummyFieldAnnotationListener.beforeTestSetUp(null, testFieldMock.getMock(), annotationsMock.getMock());
        testFieldMock.assertInvoked().setValue(dummyList);
    }


    public static class MyClass {

        @Dummy
        private List field1;
    }
}