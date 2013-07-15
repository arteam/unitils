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
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.core.InjectionService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.inject.core.TargetService;
import org.unitils.mock.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class InjectIntoFieldAnnotationListenerBeforeTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private InjectIntoFieldAnnotationListener injectIntoFieldAnnotationListener;

    private Mock<TargetService> targetServiceMock;
    private Mock<InjectionService> injectionServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<InjectInto>> annotationsMock;

    private InjectInto annotation1;
    private InjectInto annotation2;
    private InjectInto annotation3;


    @Before
    public void initialize() throws Exception {
        injectIntoFieldAnnotationListener = new InjectIntoFieldAnnotationListener(targetServiceMock.getMock(), injectionServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(InjectInto.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(InjectInto.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(InjectInto.class);

        testFieldMock.returns("value").getValue();
        testFieldMock.returns(Type.class).getGenericType();
    }


    @Test
    public void singleTarget() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        targetServiceMock.returns(asList("target")).getTargetsForInjection(asList("targetName"), testInstanceMock.getMock());

        injectIntoFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value");
        injectionServiceMock.assertInvoked().injectIntoAll(asList("target"), "field", objectToInject, false);
    }

    @Test
    public void multipleTargets() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        targetServiceMock.returns(asList("target1", "target2")).getTargetsForInjection(asList("targetName1", "targetName2"), testInstanceMock.getMock());

        injectIntoFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value");
        injectionServiceMock.assertInvoked().injectIntoAll(asList("target1", "target2"), "field", objectToInject, true);
    }

    @Test
    public void noTargets() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        targetServiceMock.returns(Collections.<String>emptyList()).getTargetsForInjection(Collections.<String>emptyList(), testInstanceMock.getMock());

        injectIntoFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value");
        injectionServiceMock.assertInvoked().injectIntoAll(Collections.<String>emptyList(), "field", objectToInject, true);
    }


    private static class MyClass {

        @InjectInto(target = "targetName", property = "field", autoCreateInnerFields = false)
        private String field1;

        @InjectInto(target = {"targetName1", "targetName2"}, property = "field", autoCreateInnerFields = true)
        private String field2;

        @InjectInto(property = "field")
        private String field3;
    }

    private static class Type {
    }

}
