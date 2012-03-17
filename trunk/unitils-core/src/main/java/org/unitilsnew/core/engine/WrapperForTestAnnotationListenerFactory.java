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

import org.unitilsnew.core.Annotations;
import org.unitilsnew.core.TestAnnotationListener;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.annotation.TestAnnotation;
import org.unitilsnew.core.context.Context;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 */
public class WrapperForTestAnnotationListenerFactory {

    protected Context context;


    public WrapperForTestAnnotationListenerFactory(Context context) {
        this.context = context;
    }


    public List<WrapperForTestAnnotationListener> create(TestInstance testInstance) {
        List<Annotation> classAnnotations = testInstance.getClassAnnotations();
        List<Annotation> methodAnnotations = testInstance.getMethodAnnotations();
        Set<Class<? extends Annotation>> testAnnotationTypes = getTestAnnotationTypes(classAnnotations, methodAnnotations);

        List<WrapperForTestAnnotationListener> testListeners = new ArrayList<WrapperForTestAnnotationListener>();
        for (Class<? extends Annotation> testAnnotationType : testAnnotationTypes) {
            WrapperForTestAnnotationListener testListener = createWrapperForTestAnnotationListener(testAnnotationType, testInstance);
            testListeners.add(testListener);
        }
        return testListeners;
    }


    protected Set<Class<? extends Annotation>> getTestAnnotationTypes(List<Annotation> classAnnotations, List<Annotation> methodAnnotations) {
        Set<Class<? extends Annotation>> testAnnotationTypes = new HashSet<Class<? extends Annotation>>();
        for (Annotation classAnnotation : classAnnotations) {
            Class<? extends Annotation> classAnnotationType = classAnnotation.annotationType();
            if (classAnnotationType.isAnnotationPresent(TestAnnotation.class)) {
                testAnnotationTypes.add(classAnnotationType);
            }
        }
        for (Annotation methodAnnotation : methodAnnotations) {
            Class<? extends Annotation> methodAnnotationType = methodAnnotation.annotationType();
            if (methodAnnotationType.isAnnotationPresent(TestAnnotation.class)) {
                testAnnotationTypes.add(methodAnnotation.annotationType());
            }
        }
        return testAnnotationTypes;
    }

    @SuppressWarnings("unchecked")
    protected <A extends Annotation> WrapperForTestAnnotationListener<A> createWrapperForTestAnnotationListener(Class<A> annotationType, TestInstance testInstance) {
        List<A> classAnnotations = testInstance.getClassAnnotations(annotationType);
        A annotation = testInstance.getMethodAnnotation(annotationType);
        TestAnnotation testAnnotation = annotationType.getAnnotation(TestAnnotation.class);

        Class<? extends TestAnnotationListener<A>> testAnnotationListenerType = (Class<TestAnnotationListener<A>>) testAnnotation.value();
        TestAnnotationListener<A> testAnnotationListener = context.getInstanceOfType(testAnnotationListenerType);

        Annotations<A> annotations = new Annotations<A>(annotation, classAnnotations, context.getConfiguration());
        return new WrapperForTestAnnotationListener<A>(annotations, testAnnotationListener);
    }
}
