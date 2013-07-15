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
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.reflect.Annotations;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.core.InjectionByTypeService;
import org.unitils.inject.core.ObjectToInject;
import org.unitils.inject.core.TargetService;
import org.unitils.mock.Mock;

import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class InjectIntoByTypeFieldAnnotationListenerBeforeTestMethodTest {

    /* Tested object */
    private InjectIntoByTypeFieldAnnotationListener injectIntoByTypeFieldAnnotationListener;

    private Mock<TargetService> targetServiceMock;
    private Mock<InjectionByTypeService> injectionByTypeServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<InjectIntoByType>> annotationsMock;

    private InjectIntoByType annotation1;
    private InjectIntoByType annotation2;
    private InjectIntoByType annotation3;


    @Before
    public void initialize() throws Exception {
        injectIntoByTypeFieldAnnotationListener = new InjectIntoByTypeFieldAnnotationListener(targetServiceMock.getMock(), injectionByTypeServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(InjectIntoByType.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(InjectIntoByType.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(InjectIntoByType.class);

        testFieldMock.returns("value").getValue();
        testFieldMock.returns(Type.class).getGenericType();
    }


    @Test
    public void singleTarget() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        targetServiceMock.returns(asList("target")).getTargetsForInjection(asList("targetName"), testInstanceMock.getMock());

        injectIntoByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value", Type.class);
        injectionByTypeServiceMock.assertInvoked().injectIntoAllByType(asList("target"), objectToInject, true);
    }

    @Test
    public void multipleTargets() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        targetServiceMock.returns(asList("target1", "target2")).getTargetsForInjection(asList("targetName1", "targetName2"), testInstanceMock.getMock());

        injectIntoByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value", Type.class);
        injectionByTypeServiceMock.assertInvoked().injectIntoAllByType(asList("target1", "target2"), objectToInject, false);
    }

    @Test
    public void noTargets() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        targetServiceMock.returns(Collections.<String>emptyList()).getTargetsForInjection(Collections.<String>emptyList(), testInstanceMock.getMock());

        injectIntoByTypeFieldAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        ObjectToInject objectToInject = new ObjectToInject("value", Type.class);
        injectionByTypeServiceMock.assertInvoked().injectIntoAllByType(Collections.<String>emptyList(), objectToInject, true);
    }


    private static class MyClass {

        @InjectIntoByType(target = "targetName", failWhenNoMatch = true)
        private String field1;

        @InjectIntoByType(target = {"targetName1", "targetName2"}, failWhenNoMatch = false)
        private String field2;

        @InjectIntoByType
        private String field3;
    }

    private static class Type {
    }

}
