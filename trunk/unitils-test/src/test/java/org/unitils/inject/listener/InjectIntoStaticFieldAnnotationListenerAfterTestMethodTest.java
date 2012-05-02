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
import org.unitils.inject.annotation.InjectIntoStatic;
import org.unitils.inject.core.InjectionService;
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
public class InjectIntoStaticFieldAnnotationListenerAfterTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private InjectIntoStaticFieldAnnotationListener injectIntoStaticFieldAnnotationListener;

    private Mock<InjectionService> injectionServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<InjectIntoStatic>> annotationsMock;
    private Mock<OriginalFieldValue> originalFieldValueMock;

    private InjectIntoStatic annotation1;
    private InjectIntoStatic annotation2;
    private InjectIntoStatic annotation3;
    private InjectIntoStatic annotation4;


    @Before
    public void initialize() throws Exception {
        injectIntoStaticFieldAnnotationListener = new InjectIntoStaticFieldAnnotationListener(injectionServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(InjectIntoStatic.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(InjectIntoStatic.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(InjectIntoStatic.class);
        annotation4 = MyClass.class.getDeclaredField("field4").getAnnotation(InjectIntoStatic.class);

        testFieldMock.returns("value").getValue();
        testFieldMock.returns(Type.class).getGenericType();
    }


    @Test
    public void restoreToOldValue() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        injectionServiceMock.returns(originalFieldValueMock).injectIntoStatic(null, null, null, true);

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertInvoked().restoreToOriginalValue();
        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
    }

    @Test
    public void restoreToNullOr0Value() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        injectionServiceMock.returns(originalFieldValueMock).injectIntoStatic(null, null, null, true);

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }

    @Test
    public void noRestore() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        injectionServiceMock.returns(originalFieldValueMock).injectIntoStatic(null, null, null, true);

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }

    @Test
    public void defaultIsNoRestore() {
        annotationsMock.returns(annotation4).getAnnotationWithDefaults();
        injectionServiceMock.returns(originalFieldValueMock).injectIntoStatic(null, null, null, true);

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }

    @Test
    public void noRestoreWhenThereIsNoOriginalValue() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        injectionServiceMock.returns(null).injectIntoStatic(null, null, null, true);

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());
        injectIntoStaticFieldAnnotationListener.afterTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock(), null);

        originalFieldValueMock.assertNotInvoked().restoreToNullOr0();
        originalFieldValueMock.assertNotInvoked().restoreToOriginalValue();
    }


    private static class MyClass {

        @InjectIntoStatic(target = Target.class, property = "field", restore = OLD_VALUE)
        private String field1;

        @InjectIntoStatic(target = Target.class, property = "field", restore = NULL_OR_0_VALUE)
        private String field2;

        @InjectIntoStatic(target = Target.class, property = "field", restore = NO_RESTORE)
        private String field3;

        @InjectIntoStatic(target = Target.class, property = "field", restore = DEFAULT)
        private String field4;
    }

    private static class Type {
    }

    private static class Target {
    }

}
