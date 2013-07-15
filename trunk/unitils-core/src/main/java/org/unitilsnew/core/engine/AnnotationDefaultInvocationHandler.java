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

package org.unitilsnew.core.engine;

import org.unitilsnew.core.annotation.AnnotationDefault;
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class AnnotationDefaultInvocationHandler<A extends Annotation> implements InvocationHandler {

    protected List<A> annotations;
    protected Configuration configuration;


    public AnnotationDefaultInvocationHandler(List<A> annotations, Configuration configuration) {
        this.annotations = annotations;
        this.configuration = configuration;
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (annotations == null || annotations.isEmpty()) {
            return null;
        }
        AnnotationDefault annotationDefaultAnnotation = method.getAnnotation(AnnotationDefault.class);
        if (annotationDefaultAnnotation == null) {
            return method.invoke(annotations.get(0), args);
        }

        Object defaultValue = method.getDefaultValue();
        for (A annotation : annotations) {
            Object result = method.invoke(annotation, args);
            if (result != defaultValue && (defaultValue == null || !defaultValue.equals(result))) {
                return result;
            }
        }

        if (configuration == null) {
            return defaultValue;
        }
        String propertyName = annotationDefaultAnnotation.value();
        if (isBlank(propertyName)) {
            return defaultValue;
        }
        return configuration.getValueOfType(method.getReturnType(), propertyName);
    }
}
