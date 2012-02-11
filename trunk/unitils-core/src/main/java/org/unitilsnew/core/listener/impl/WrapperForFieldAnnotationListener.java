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

import org.unitilsnew.core.FieldAnnotation;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.listener.FieldAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class WrapperForFieldAnnotationListener<A extends Annotation> extends TestListener {

    private FieldAnnotationListener<A> fieldAnnotationListener;
    private Class<A> annotationType;


    public WrapperForFieldAnnotationListener(FieldAnnotationListener<A> fieldAnnotationListener) {
        this.fieldAnnotationListener = fieldAnnotationListener;
        this.annotationType = getAnnotationType(fieldAnnotationListener);
    }


    @Override
    public TestPhase getTestPhase() {
        return fieldAnnotationListener.getTestPhase();
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        List<FieldAnnotation<A>> fieldAnnotations = testInstance.getFieldAnnotations(annotationType);
        for (FieldAnnotation<A> fieldAnnotation : fieldAnnotations) {
            fieldAnnotationListener.beforeTestSetUp(testInstance, fieldAnnotation);
        }
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        List<FieldAnnotation<A>> fieldAnnotations = testInstance.getFieldAnnotations(annotationType);
        for (FieldAnnotation<A> fieldAnnotation : fieldAnnotations) {
            fieldAnnotationListener.beforeTestMethod(testInstance, fieldAnnotation);
        }
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        List<FieldAnnotation<A>> fieldAnnotations = testInstance.getFieldAnnotations(annotationType);
        for (FieldAnnotation<A> fieldAnnotation : fieldAnnotations) {
            fieldAnnotationListener.afterTestMethod(testInstance, testThrowable, fieldAnnotation);
        }
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance) {
        List<FieldAnnotation<A>> fieldAnnotations = testInstance.getFieldAnnotations(annotationType);
        for (FieldAnnotation<A> fieldAnnotation : fieldAnnotations) {
            fieldAnnotationListener.afterTestTearDown(testInstance, fieldAnnotation);
        }
    }


    @SuppressWarnings("unchecked")
    protected Class<A> getAnnotationType(FieldAnnotationListener<A> fieldAnnotationListener) {
//        Map<TypeVariable<?>, Type> result = getTypeArguments(fieldAnnotationListener.getClass(), FieldAnnotationListener.class);
//        return (Class<A>) result.values().iterator().next();
        // todo td implement
        return null;
    }
}
