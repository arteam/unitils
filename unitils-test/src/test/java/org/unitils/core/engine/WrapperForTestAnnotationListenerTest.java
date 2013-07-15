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

package org.unitils.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestAnnotationListener;
import org.unitils.core.TestInstance;
import org.unitils.core.TestPhase;
import org.unitils.core.UnitilsException;
import org.unitils.core.reflect.Annotations;
import org.unitils.core.reflect.ClassWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.lang.annotation.Target;

import static org.junit.Assert.*;
import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class WrapperForTestAnnotationListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private WrapperForTestAnnotationListener wrapperForTestAnnotationListener;

    private Mock<TestAnnotationListener<Target>> testAnnotationListenerMock;
    private Mock<Annotations<Target>> annotationsMock;

    @Dummy
    private ClassWrapper classWrapper;
    @Dummy
    private TestInstance testInstance;


    @Before
    public void initialize() throws Exception {
        annotationsMock.returns(Target.class).getType();
        wrapperForTestAnnotationListener = new WrapperForTestAnnotationListener<Target>(annotationsMock.getMock(), testAnnotationListenerMock.getMock());
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
        testAnnotationListenerMock.assertInvoked().beforeTestSetUp(testInstance, annotationsMock.getMock());
    }

    @Test
    public void beforeTestMethod() {
        wrapperForTestAnnotationListener.beforeTestMethod(testInstance);
        testAnnotationListenerMock.assertInvoked().beforeTestMethod(testInstance, annotationsMock.getMock());
    }

    @Test
    public void afterTestMethod() {
        NullPointerException e = new NullPointerException();

        wrapperForTestAnnotationListener.afterTestMethod(testInstance, e);
        testAnnotationListenerMock.assertInvoked().afterTestMethod(testInstance, annotationsMock.getMock(), e);
    }

    @Test
    public void afterTestTearDown() {
        NullPointerException e = new NullPointerException();

        wrapperForTestAnnotationListener.afterTestTearDown(testInstance, e);
        testAnnotationListenerMock.assertInvoked().afterTestTearDown(testInstance, annotationsMock.getMock(), e);
    }


    @Test
    public void beforeTestSetUpException() {
        Exception exception = new NullPointerException("message");
        testAnnotationListenerMock.raises(exception).beforeTestSetUp(null, null);
        try {
            wrapperForTestAnnotationListener.beforeTestSetUp(testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle test annotation @Target.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }

    @Test
    public void beforeTestMethodException() {
        Exception exception = new NullPointerException("message");
        testAnnotationListenerMock.raises(exception).beforeTestMethod(null, null);
        try {
            wrapperForTestAnnotationListener.beforeTestMethod(testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle test annotation @Target.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }

    @Test
    public void afterTestMethodException() {
        Exception exception = new NullPointerException();
        testAnnotationListenerMock.raises(exception).afterTestMethod(null, null, null);
        try {
            wrapperForTestAnnotationListener.afterTestMethod(testInstance, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle test annotation @Target.", e.getMessage());
        }
    }

    @Test
    public void afterTestTearDownException() {
        Exception exception = new NullPointerException("message");
        testAnnotationListenerMock.raises(exception).afterTestTearDown(null, null, null);
        try {
            wrapperForTestAnnotationListener.afterTestTearDown(testInstance, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle test annotation @Target.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }
}
