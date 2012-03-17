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

package org.unitilsnew.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.*;

import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class WrapperForFieldAnnotationListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private WrapperForFieldAnnotationListener wrapperForFieldAnnotationListener;

    private Mock<FieldAnnotationListener<Target>> fieldAnnotationListenerMock;

    @Dummy
    private TestField testField;
    @Dummy
    private Annotations<Target> annotations;
    @Dummy
    private TestClass testClass;
    @Dummy
    private TestInstance testInstance;


    @Before
    public void initialize() throws Exception {
        wrapperForFieldAnnotationListener = new WrapperForFieldAnnotationListener<Target>(testField, annotations, fieldAnnotationListenerMock.getMock());
    }


    @Test
    public void getTestPhase() {
        fieldAnnotationListenerMock.returns(CONSTRUCTION).getTestPhase();

        TestPhase result = wrapperForFieldAnnotationListener.getTestPhase();
        assertEquals(CONSTRUCTION, result);
    }

    @Test
    public void beforeTestSetUp() {
        wrapperForFieldAnnotationListener.beforeTestSetUp(testInstance);
        fieldAnnotationListenerMock.assertInvoked().beforeTestSetUp(testInstance, testField, annotations);
    }

    @Test
    public void beforeTestMethod() {
        wrapperForFieldAnnotationListener.beforeTestMethod(testInstance);
        fieldAnnotationListenerMock.assertInvoked().beforeTestMethod(testInstance, testField, annotations);
    }

    @Test
    public void afterTestMethod() {
        NullPointerException e = new NullPointerException();

        wrapperForFieldAnnotationListener.afterTestMethod(testInstance, e);
        fieldAnnotationListenerMock.assertInvoked().afterTestMethod(testInstance, testField, annotations, e);
    }

    @Test
    public void afterTestTearDown() {
        wrapperForFieldAnnotationListener.afterTestTearDown(testInstance);
        fieldAnnotationListenerMock.assertInvoked().afterTestTearDown(testInstance, testField, annotations);
    }
}
