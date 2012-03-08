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

package org.unitilsnew.core.listener.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.Annotations;
import org.unitilsnew.core.TestClass;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.listener.TestAnnotationListener;

import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class WrapperForTestAnnotationListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private WrapperForTestAnnotationListener wrapperForTestAnnotationListener;

    private Mock<TestAnnotationListener<Target>> testAnnotationListenerMock;

    @Dummy
    private Annotations<Target> annotations;
    @Dummy
    private TestClass testClass;
    @Dummy
    private TestInstance testInstance;


    @Before
    public void initialize() throws Exception {
        wrapperForTestAnnotationListener = new WrapperForTestAnnotationListener<Target>(annotations, testAnnotationListenerMock.getMock());
    }


    @Test
    public void getTestPhase() {
        testAnnotationListenerMock.returns(CONSTRUCTION).getTestPhase();

        TestPhase result = wrapperForTestAnnotationListener.getTestPhase();
        assertEquals(CONSTRUCTION, result);
    }

    @Test
    public void beforeTestSetUp() {
        wrapperForTestAnnotationListener.beforeTestSetUp(testInstance);
        testAnnotationListenerMock.assertInvoked().beforeTestSetUp(testInstance, annotations);
    }

    @Test
    public void beforeTestMethod() {
        wrapperForTestAnnotationListener.beforeTestMethod(testInstance);
        testAnnotationListenerMock.assertInvoked().beforeTestMethod(testInstance, annotations);
    }

    @Test
    public void afterTestMethod() {
        NullPointerException e = new NullPointerException();

        wrapperForTestAnnotationListener.afterTestMethod(testInstance, e);
        testAnnotationListenerMock.assertInvoked().afterTestMethod(testInstance, annotations, e);
    }

    @Test
    public void afterTestTearDown() {
        wrapperForTestAnnotationListener.afterTestTearDown(testInstance);
        testAnnotationListenerMock.assertInvoked().afterTestTearDown(testInstance, annotations);
    }
}
