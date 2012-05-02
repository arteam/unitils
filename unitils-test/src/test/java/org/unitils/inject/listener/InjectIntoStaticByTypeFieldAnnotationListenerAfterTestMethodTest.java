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
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.core.InjectionByTypeService;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.core.reflect.OriginalFieldValue;

import static org.unitils.inject.util.Restore.*;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoStaticByTypeFieldAnnotationListenerAfterTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private InjectIntoStaticByTypeFieldAnnotationListener injectIntoStaticByTypeFieldAnnotationListener;

    private Mock<InjectionByTypeService> injectionByTypeServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<InjectIntoStaticByType>> annotationsMock;
    private Mock<OriginalFieldValue> originalFieldValueMock;

    private InjectIntoStaticByType annotation1;
    private InjectIntoStaticByType annotation2;
    private InjectIntoStaticByType annotation3;
    private InjectIntoStaticByType annotation4;


    @Before
    public void initialize() throws Exception {
        injectIntoStaticByTypeFieldAnnotationListener = new InjectIntoStaticByTypeFieldAnnotationListener(injectionByTypeServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(InjectIntoStaticByType.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(InjectIntoStaticByType.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(InjectIntoStaticByType.class);
        annotation4 = MyClass.class.getDeclaredField("field4").getAnnotation(InjectIntoStaticByType.class);

        testFieldMock.returns("value").getValue();
        testFieldMock.returns(Type.class).getGenericType();
    }


    @Test
    public void restoreToOldValue() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        injectionByTypeServiceMock.returns(originalFieldValueMock).injectIntoStaticByType(null, null, true);

        injectIntoStaticByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticByTypeFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertInvoked().restoreToOriginalValue();
        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
    }

    @Test
    public void restoreToNullOr0Value() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        injectionByTypeServiceMock.returns(originalFieldValueMock).injectIntoStaticByType(null, null, true);

        injectIntoStaticByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticByTypeFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }

    @Test
    public void noRestore() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        injectionByTypeServiceMock.returns(originalFieldValueMock).injectIntoStaticByType(null, null, true);

        injectIntoStaticByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticByTypeFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }

    @Test
    public void defaultIsNoRestore() {
        annotationsMock.returns(annotation4).getAnnotationWithDefaults();
        injectionByTypeServiceMock.returns(originalFieldValueMock).injectIntoStaticByType(null, null, true);

        injectIntoStaticByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticByTypeFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }

    @Test
    public void noRestoreWhenThereIsNoOriginalValue() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        injectionByTypeServiceMock.returns(null).injectIntoStaticByType(null, null, true);

        injectIntoStaticByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticByTypeFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }


    private static class MyClass {

        @InjectIntoStaticByType(target = Target.class, restore = OLD_VALUE)
        private String field1;

        @InjectIntoStaticByType(target = Target.class, restore = NULL_OR_0_VALUE)
        private String field2;

        @InjectIntoStaticByType(target = Target.class, restore = NO_RESTORE)
        private String field3;

        @InjectIntoStaticByType(target = Target.class, restore = DEFAULT)
        private String field4;
    }

    private static class Type {
    }

    private static class Target {
    }

}
