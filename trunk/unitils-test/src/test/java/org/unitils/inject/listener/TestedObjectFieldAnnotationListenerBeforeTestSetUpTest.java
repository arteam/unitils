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

package org.unitils.inject.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.core.TestedObjectService;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

/**
 * @author Tim Ducheyne
 */
public class TestedObjectFieldAnnotationListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    /* Tested object */
    private TestedObjectFieldAnnotationListener testedObjectFieldAnnotationListener;

    private Mock<TestedObjectService> testedObjectServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<TestedObject>> annotationsMock;

    private TestedObject annotation;
    private Type testValue;


    @Before
    public void initialize() throws Exception {
        testedObjectFieldAnnotationListener = new TestedObjectFieldAnnotationListener(testedObjectServiceMock.getMock(), true);

        testValue = new Type();
        annotation = MyClass.class.getDeclaredField("field1").getAnnotation(TestedObject.class);

        annotationsMock.returns(annotation).getAnnotationWithDefaults();
        testedObjectServiceMock.returns(testValue).createTestedObject(Type.class);
    }


    @Test
    public void ignoreWhenDisabled() {
        testedObjectFieldAnnotationListener = new TestedObjectFieldAnnotationListener(testedObjectServiceMock.getMock(), false);
        testFieldMock.returns(testValue).getValue();
        testFieldMock.returns(Type.class).getType();

        testedObjectFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testedObjectServiceMock.assertNotInvoked().createTestedObject(null);
        testFieldMock.assertNotInvoked().setValue(null);
    }

    @Test
    public void autoCreateWhenNullValue() {
        testFieldMock.returns(null).getValue();
        testFieldMock.returns(Type.class).getType();

        testedObjectFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testedObjectServiceMock.assertInvoked().createTestedObject(Type.class);
        testFieldMock.assertInvoked().setValue(testValue);
    }

    @Test
    public void noAutoCreateWhenNotNullValue() {
        testFieldMock.returns(testValue).getValue();
        testFieldMock.returns(Type.class).getType();

        testedObjectFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testedObjectServiceMock.assertNotInvoked().createTestedObject(null);
        testFieldMock.assertNotInvoked().setValue(null);
    }


    private static class MyClass {

        @TestedObject
        private Type field1;
    }

    private static class Type {
    }
}
