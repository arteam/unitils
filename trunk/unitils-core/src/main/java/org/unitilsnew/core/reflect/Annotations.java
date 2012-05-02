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

package org.unitilsnew.core.reflect;

import org.unitilsnew.core.config.Configuration;
import org.unitilsnew.core.engine.AnnotationDefaultInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class Annotations<A extends Annotation> {

    protected A annotation;
    protected List<A> classAnnotations;
    protected Configuration configuration;


    public Annotations(A annotation, List<A> classAnnotations, Configuration configuration) {
        this.annotation = annotation;
        this.classAnnotations = classAnnotations;
        this.configuration = configuration;
    }


    @SuppressWarnings("unchecked")
    public A getAnnotationWithDefaults() {
        List<A> allAnnotations = getAllAnnotation();
        if (allAnnotations.isEmpty()) {
            return null;
        }

        ClassLoader classLoader = getClass().getClassLoader();
        Class<?>[] annotationType = new Class<?>[]{allAnnotations.get(0).annotationType()};

        AnnotationDefaultInvocationHandler<A> invocationHandler = new AnnotationDefaultInvocationHandler<A>(allAnnotations, configuration);
        return (A) Proxy.newProxyInstance(classLoader, annotationType, invocationHandler);
    }

    public A getFirstAnnotation() {
        if (annotation != null) {
            return annotation;
        }
        if (classAnnotations != null && !classAnnotations.isEmpty()) {
            return classAnnotations.get(0);
        }
        return null;
    }

    public List<A> getAllAnnotation() {
        List<A> allAnnotations = new ArrayList<A>();
        if (annotation != null) {
            allAnnotations.add(annotation);
        }
        if (classAnnotations != null) {
            allAnnotations.addAll(classAnnotations);
        }
        return allAnnotations;
    }

    public A getAnnotation() {
        return annotation;
    }

    public List<A> getClassAnnotations() {
        return classAnnotations;
    }

    public Class<? extends Annotation> getType() {
        return getFirstAnnotation().annotationType();
    }
}
