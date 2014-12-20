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

package org.unitils.inject.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.reflect.Annotations;
import org.unitils.inject.annotation.InjectIntoStatic;
import org.unitils.inject.core.InjectionService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.mock.Mock;

import static org.unitils.inject.util.Restore.OLD_VALUE;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoStaticFieldAnnotationListenerBeforeTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private InjectIntoStaticFieldAnnotationListener injectIntoStaticFieldAnnotationListener;

    private Mock<InjectionService> injectionServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<InjectIntoStatic>> annotationsMock;

    private InjectIntoStatic annotation1;
    private InjectIntoStatic annotation2;


    @Before
    public void initialize() throws Exception {
        injectIntoStaticFieldAnnotationListener = new InjectIntoStaticFieldAnnotationListener(injectionServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(InjectIntoStatic.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(InjectIntoStatic.class);

        testFieldMock.returns("value").getValue();
        testFieldMock.returns(Type.class).getGenericType();
    }


    @Test
    public void specifiedAnnotationValues() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value");
        injectionServiceMock.assertInvoked().injectIntoStatic(Target.class, "field", objectToInject, false);
    }

    @Test
    public void defaultAnnotationValues() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();

        injectIntoStaticFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value");
        injectionServiceMock.assertInvoked().injectIntoStatic(Target.class, "field", objectToInject, true);
    }


    private static class MyClass {

        @InjectIntoStatic(target = Target.class, property = "field", restore = OLD_VALUE, autoCreateInnerFields = false)
        private String field1;

        @InjectIntoStatic(target = Target.class, property = "field")
        private String field2;
    }

    private static class Type {
    }

    private static class Target {
    }

}
