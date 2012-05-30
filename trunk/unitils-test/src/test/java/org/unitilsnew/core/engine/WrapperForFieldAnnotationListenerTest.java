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
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.annotation.Target;

import static org.junit.Assert.*;
import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class WrapperForFieldAnnotationListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private WrapperForFieldAnnotationListener wrapperForFieldAnnotationListener;

    private Mock<FieldAnnotationListener<Target>> fieldAnnotationListenerMock;
    private Mock<Annotations<Target>> annotationsMock;
    private Mock<TestField> testFieldMock;

    @Dummy
    private ClassWrapper classWrapper;
    @Dummy
    private TestInstance testInstance;


    @Before
    public void initialize() throws Exception {
        annotationsMock.returns(Target.class).getType();
        testFieldMock.returns("fieldName").getName();
        wrapperForFieldAnnotationListener = new WrapperForFieldAnnotationListener<Target>(testFieldMock.getMock(), annotationsMock.getMock(), fieldAnnotationListenerMock.getMock());
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
        fieldAnnotationListenerMock.assertInvoked().beforeTestSetUp(testInstance, testFieldMock.getMock(), annotationsMock.getMock());
    }

    @Test
    public void beforeTestMethod() {
        wrapperForFieldAnnotationListener.beforeTestMethod(testInstance);
        fieldAnnotationListenerMock.assertInvoked().beforeTestMethod(testInstance, testFieldMock.getMock(), annotationsMock.getMock());
    }

    @Test
    public void afterTestMethod() {
        NullPointerException e = new NullPointerException();

        wrapperForFieldAnnotationListener.afterTestMethod(testInstance, e);
        fieldAnnotationListenerMock.assertInvoked().afterTestMethod(testInstance, testFieldMock.getMock(), annotationsMock.getMock(), e);
    }

    @Test
    public void afterTestTearDown() {
        wrapperForFieldAnnotationListener.afterTestTearDown(testInstance);
        fieldAnnotationListenerMock.assertInvoked().afterTestTearDown(testInstance, testFieldMock.getMock(), annotationsMock.getMock());
    }


    @Test
    public void beforeTestSetUpException() {
        Exception exception = new NullPointerException("message");
        fieldAnnotationListenerMock.raises(exception).beforeTestSetUp(null, null, null);
        try {
            wrapperForFieldAnnotationListener.beforeTestSetUp(testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle field annotation @Target on field 'fieldName'.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }

    @Test
    public void beforeTestMethodException() {
        Exception exception = new NullPointerException("");
        fieldAnnotationListenerMock.raises(exception).beforeTestMethod(null, null, null);
        try {
            wrapperForFieldAnnotationListener.beforeTestMethod(testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle field annotation @Target on field 'fieldName'.", e.getMessage());
        }
    }

    @Test
    public void afterTestMethodException() {
        Exception exception = new NullPointerException();
        fieldAnnotationListenerMock.raises(exception).afterTestMethod(null, null, null, null);
        try {
            wrapperForFieldAnnotationListener.afterTestMethod(testInstance, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle field annotation @Target on field 'fieldName'.", e.getMessage());
        }
    }

    @Test
    public void afterTestTearDownException() {
        Exception exception = new NullPointerException("message");
        fieldAnnotationListenerMock.raises(exception).afterTestTearDown(null, null, null);
        try {
            wrapperForFieldAnnotationListener.afterTestTearDown(testInstance);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertEquals("Unable to handle field annotation @Target on field 'fieldName'.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }
}
