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

import org.unitilsnew.core.TestAnnotation;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.listener.TestAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.lang.annotation.Annotation;

/**
 * @author Tim Ducheyne
 */
public class WrapperForTestAnnotationListener<A extends Annotation> extends TestListener {

    private TestAnnotationListener<A> testAnnotationListener;
    private Class<A> annotationType;


    public WrapperForTestAnnotationListener(TestAnnotationListener<A> testAnnotationListener) {
        this.testAnnotationListener = testAnnotationListener;
        this.annotationType = getAnnotationType(testAnnotationListener);
    }


    @Override
    public TestPhase getTestPhase() {
        return testAnnotationListener.getTestPhase();
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        TestAnnotation<A> testAnnotation = testInstance.getTestAnnotation(annotationType);
        if (testAnnotation != null) {
            testAnnotationListener.beforeTestSetUp(testInstance, testAnnotation);
        }
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        TestAnnotation<A> testAnnotation = testInstance.getTestAnnotation(annotationType);
        if (testAnnotation != null) {
            testAnnotationListener.beforeTestMethod(testInstance, testAnnotation);
        }
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        TestAnnotation<A> testAnnotation = testInstance.getTestAnnotation(annotationType);
        if (testAnnotation != null) {
            testAnnotationListener.afterTestMethod(testInstance, testThrowable, testAnnotation);
        }
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance) {
        TestAnnotation<A> testAnnotation = testInstance.getTestAnnotation(annotationType);
        if (testAnnotation != null) {
            testAnnotationListener.afterTestTearDown(testInstance, testAnnotation);
        }
    }


    @SuppressWarnings("unchecked")
    protected Class<A> getAnnotationType(TestAnnotationListener testAnnotationListener) {
//        Map<TypeVariable<?>, Type> result = getTypeArguments(testAnnotationListener.getClass(), TestAnnotationListener.class);
//        return (Class<A>) result.values().iterator().next();
        // todo td implement
        return null;
    }
}
