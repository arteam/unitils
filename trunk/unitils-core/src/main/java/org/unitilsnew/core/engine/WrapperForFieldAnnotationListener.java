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
import org.unitilsnew.core.*;
import org.unitilsnew.core.reflect.Annotations;

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
        try {
            fieldAnnotationListener.beforeTestSetUp(testInstance, testField, annotations);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance) {
        try {
            fieldAnnotationListener.beforeTestMethod(testInstance, testField, annotations);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        try {
            fieldAnnotationListener.afterTestMethod(testInstance, testField, annotations, testThrowable);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance, Throwable testThrowable) {
        try {
            fieldAnnotationListener.afterTestTearDown(testInstance, testField, annotations, testThrowable);
        } catch (Exception e) {
            throw new UnitilsException(getExceptionMessage(e), e);
        }
    }


    protected String getExceptionMessage(Exception e) {
        return "Unable to handle field annotation @" + annotations.getType().getSimpleName() + " on field '" + testField.getName() + "'.";
    }
}
