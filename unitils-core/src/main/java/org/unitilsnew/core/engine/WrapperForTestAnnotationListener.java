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

import org.unitilsnew.core.*;

import java.lang.annotation.Annotation;

/**
 * @author Tim Ducheyne
 */
public class WrapperForTestAnnotationListener<A extends Annotation> extends TestListener {

    protected Annotations<A> annotations;
    protected TestAnnotationListener<A> testAnnotationListener;


    public WrapperForTestAnnotationListener(Annotations<A> annotations, TestAnnotationListener<A> testAnnotationListener) {
        this.annotations = annotations;
        this.testAnnotationListener = testAnnotationListener;
    }


    @Override
    public TestPhase getTestPhase() {
        return testAnnotationListener.getTestPhase();
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        testAnnotationListener.beforeTestSetUp(testInstance, annotations);
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        testAnnotationListener.beforeTestMethod(testInstance, annotations);
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        testAnnotationListener.afterTestMethod(testInstance, annotations, testThrowable);
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance) {
        testAnnotationListener.afterTestTearDown(testInstance, annotations);
    }
}
