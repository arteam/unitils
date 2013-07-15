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

import org.unitils.core.FieldAnnotationListener;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.annotation.FieldAnnotation;
import org.unitils.core.context.Context;
import org.unitils.core.reflect.Annotations;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class WrapperForFieldAnnotationListenerFactory {

    protected Context context;


    public WrapperForFieldAnnotationListenerFactory(Context context) {
        this.context = context;
    }


    public List<WrapperForFieldAnnotationListener> create(TestInstance testInstance) {
        List<WrapperForFieldAnnotationListener> testListeners = new ArrayList<WrapperForFieldAnnotationListener>(3);

        List<TestField> testFields = testInstance.getTestFields();
        for (TestField testField : testFields) {
            List<Annotation> annotations = testField.getAnnotations();
            for (Annotation annotation : annotations) {
                WrapperForFieldAnnotationListener testListener = createWrapperForFieldAnnotationListener(annotation, testInstance, testField);
                if (testListener != null) {
                    testListeners.add(testListener);
                }
            }
        }
        return testListeners;
    }


    @SuppressWarnings("unchecked")
    protected <A extends Annotation> WrapperForFieldAnnotationListener<A> createWrapperForFieldAnnotationListener(A annotation, TestInstance testInstance, TestField testField) {
        Class<A> annotationType = (Class<A>) annotation.annotationType();
        List<A> classAnnotations = testInstance.getClassAnnotations(annotationType);
        FieldAnnotation fieldAnnotation = annotationType.getAnnotation(FieldAnnotation.class);
        if (fieldAnnotation == null) {
            return null;
        }
        Class<? extends FieldAnnotationListener<A>> fieldAnnotationListenerType = (Class<FieldAnnotationListener<A>>) fieldAnnotation.value();
        FieldAnnotationListener<A> fieldAnnotationListener = context.getInstanceOfType(fieldAnnotationListenerType);

        Annotations<A> annotations = new Annotations<A>(annotation, classAnnotations, context.getConfiguration());
        return new WrapperForFieldAnnotationListener<A>(testField, annotations, fieldAnnotationListener);
    }
}
