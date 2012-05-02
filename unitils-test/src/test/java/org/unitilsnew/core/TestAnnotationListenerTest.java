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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.core.reflect.ClassWrapper;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

import static org.junit.Assert.assertEquals;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class TestAnnotationListenerTest extends UnitilsJUnit4 {

    /* Tested object */
    private TestAnnotationListener<Target> testAnnotationListener;

    @Dummy
    private Annotations<Target> annotations;
    @Dummy
    private ClassWrapper classWrapper;
    @Dummy
    private TestInstance testInstance;


    @Before
    public void initialize() throws Exception {
        testAnnotationListener = new MyTestAnnotationListener<Target>();
    }


    @Test
    public void testPhaseDefaultsToExecutionPhase() {
        TestPhase result = testAnnotationListener.getTestPhase();
        assertEquals(EXECUTION, result);
    }

    @Test
    public void defaultBeforeTestSetUpDoesNothing() {
        testAnnotationListener.beforeTestSetUp(testInstance, annotations);
    }

    @Test
    public void defaultBeforeTestMethodDoesNothing() {
        testAnnotationListener.beforeTestMethod(testInstance, annotations);
    }

    @Test
    public void defaultAfterTestMethodDoesNothing() {
        NullPointerException e = new NullPointerException();
        testAnnotationListener.afterTestMethod(testInstance, annotations, e);
    }

    @Test
    public void defaultAfterTestTearDownDoesNothing() {
        testAnnotationListener.afterTestTearDown(testInstance, annotations);
    }


    private static class MyTestAnnotationListener<A extends Annotation> extends TestAnnotationListener<A> {
    }
}
