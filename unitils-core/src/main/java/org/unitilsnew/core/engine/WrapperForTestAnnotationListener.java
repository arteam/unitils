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

import org.unitils.core.UnitilsException;
import org.unitilsnew.core.TestAnnotationListener;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestListener;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.reflect.Annotations;

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
        try {
            testAnnotationListener.beforeTestSetUp(testInstance, annotations);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        try {
            testAnnotationListener.beforeTestMethod(testInstance, annotations);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        try {
            testAnnotationListener.afterTestMethod(testInstance, annotations, testThrowable);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance, Throwable testThrowable) {
        try {
            testAnnotationListener.afterTestTearDown(testInstance, annotations, testThrowable);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }


    protected String getExceptionMessage(Exception e) {
        return "Unable to handle test annotation @" + annotations.getType().getSimpleName() + ".";
    }
}
