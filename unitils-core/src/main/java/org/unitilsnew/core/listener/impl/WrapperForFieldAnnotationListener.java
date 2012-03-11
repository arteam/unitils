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

import org.unitilsnew.core.Annotations;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.listener.FieldAnnotationListener;
import org.unitilsnew.core.listener.TestListener;

import java.lang.annotation.Annotation;

/**
 * @author Tim Ducheyne
 */
public class WrapperForFieldAnnotationListener<A extends Annotation> extends TestListener {

    protected TestField testField;
    protected Annotations<A> annotations;
    protected FieldAnnotationListener<A> fieldAnnotationListener;


    public WrapperForFieldAnnotationListener(TestField testField, Annotations<A> annotations, FieldAnnotationListener<A> fieldAnnotationListener) {
        this.testField = testField;
        this.annotations = annotations;
        this.fieldAnnotationListener = fieldAnnotationListener;
    }


    @Override
    public TestPhase getTestPhase() {
        return fieldAnnotationListener.getTestPhase();
    }

    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        fieldAnnotationListener.beforeTestSetUp(testInstance, testField, annotations);
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        fieldAnnotationListener.beforeTestMethod(testInstance, testField, annotations);
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        fieldAnnotationListener.afterTestMethod(testInstance, testField, annotations, testThrowable);
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance) {
        fieldAnnotationListener.afterTestTearDown(testInstance, testField, annotations);
    }
}
