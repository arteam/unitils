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
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestListener;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.unitilsnew.core.TestPhase.*;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerTestPhaseTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListener unitilsTestListener;

    private Mock<WrapperForFieldAnnotationListenerFactory> wrapperForFieldAnnotationListenerFactoryMock;
    private Mock<WrapperForTestAnnotationListenerFactory> wrapperForTestAnnotationListenerFactoryMock;
    private Mock<TestListener> constructionTestListenerMock;
    private Mock<TestListener> injectionTestListenerMock;
    private Mock<TestListener> setupTestListenerMock;
    private Mock<TestListener> executionTestListenerMock;


    @Before
    public void initialize() throws Exception {
        unitilsTestListener = new UnitilsTestListener(new ArrayList<TestListener>(), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());

        constructionTestListenerMock.returns(CONSTRUCTION).getTestPhase();
        injectionTestListenerMock.returns(INJECTION).getTestPhase();
        setupTestListenerMock.returns(SETUP).getTestPhase();
        executionTestListenerMock.returns(EXECUTION).getTestPhase();
    }


    @Test
    public void beforeTestClassSortedOnTestPhase() {
        unitilsTestListener = new UnitilsTestListener(asList(executionTestListenerMock.getMock(), setupTestListenerMock.getMock(), injectionTestListenerMock.getMock(), constructionTestListenerMock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());

        unitilsTestListener.beforeTestClass(null);

        constructionTestListenerMock.assertInvokedInSequence().beforeTestClass(null);
        injectionTestListenerMock.assertInvokedInSequence().beforeTestClass(null);
        setupTestListenerMock.assertInvokedInSequence().beforeTestClass(null);
        executionTestListenerMock.assertInvokedInSequence().beforeTestClass(null);
    }

    @Test
    public void beforeTestSetupSortedOnTestPhase() {
        unitilsTestListener = new UnitilsTestListener(asList(executionTestListenerMock.getMock(), setupTestListenerMock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());
        wrapperForFieldAnnotationListenerFactoryMock.returns(asList(injectionTestListenerMock.getMock())).create(null);
        wrapperForTestAnnotationListenerFactoryMock.returns(asList(constructionTestListenerMock.getMock())).create(null);

        unitilsTestListener.beforeTestClass(null);
        unitilsTestListener.beforeTestSetUp(null, null);

        constructionTestListenerMock.assertInvokedInSequence().beforeTestSetUp(null);
        injectionTestListenerMock.assertInvokedInSequence().beforeTestSetUp(null);
        setupTestListenerMock.assertInvokedInSequence().beforeTestSetUp(null);
        executionTestListenerMock.assertInvokedInSequence().beforeTestSetUp(null);
    }

    @Test
    public void beforeTestMethodSortedOnTestPhase() {
        unitilsTestListener = new UnitilsTestListener(asList(executionTestListenerMock.getMock(), setupTestListenerMock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());
        wrapperForFieldAnnotationListenerFactoryMock.returns(asList(injectionTestListenerMock.getMock())).create(null);
        wrapperForTestAnnotationListenerFactoryMock.returns(asList(constructionTestListenerMock.getMock())).create(null);

        unitilsTestListener.beforeTestClass(null);
        unitilsTestListener.beforeTestSetUp(null, null);
        unitilsTestListener.beforeTestMethod();

        constructionTestListenerMock.assertInvokedInSequence().beforeTestMethod(null);
        injectionTestListenerMock.assertInvokedInSequence().beforeTestMethod(null);
        setupTestListenerMock.assertInvokedInSequence().beforeTestMethod(null);
        executionTestListenerMock.assertInvokedInSequence().beforeTestMethod(null);
    }

    @Test
    public void afterTestMethodSortedOnTestPhase() {
        unitilsTestListener = new UnitilsTestListener(asList(executionTestListenerMock.getMock(), setupTestListenerMock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());
        wrapperForFieldAnnotationListenerFactoryMock.returns(asList(injectionTestListenerMock.getMock())).create(null);
        wrapperForTestAnnotationListenerFactoryMock.returns(asList(constructionTestListenerMock.getMock())).create(null);

        unitilsTestListener.beforeTestClass(null);
        unitilsTestListener.beforeTestSetUp(null, null);
        unitilsTestListener.afterTestMethod(null);

        constructionTestListenerMock.assertInvokedInSequence().afterTestMethod(null, null);
        injectionTestListenerMock.assertInvokedInSequence().afterTestMethod(null, null);
        setupTestListenerMock.assertInvokedInSequence().afterTestMethod(null, null);
        executionTestListenerMock.assertInvokedInSequence().afterTestMethod(null, null);
    }

    @Test
    public void afterTestTearDownSortedOnTestPhase() {
        unitilsTestListener = new UnitilsTestListener(asList(executionTestListenerMock.getMock(), setupTestListenerMock.getMock()), wrapperForFieldAnnotationListenerFactoryMock.getMock(), wrapperForTestAnnotationListenerFactoryMock.getMock());
        wrapperForFieldAnnotationListenerFactoryMock.returns(asList(injectionTestListenerMock.getMock())).create(null);
        wrapperForTestAnnotationListenerFactoryMock.returns(asList(constructionTestListenerMock.getMock())).create(null);

        unitilsTestListener.beforeTestClass(null);
        unitilsTestListener.beforeTestSetUp(null, null);
        unitilsTestListener.afterTestTearDown();

        constructionTestListenerMock.assertInvokedInSequence().afterTestTearDown(null);
        injectionTestListenerMock.assertInvokedInSequence().afterTestTearDown(null);
        setupTestListenerMock.assertInvokedInSequence().afterTestTearDown(null);
        executionTestListenerMock.assertInvokedInSequence().afterTestTearDown(null);
    }
}
