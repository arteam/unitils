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

package org.unitilsnew.core;

import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class FieldAnnotation<A extends Annotation> extends TestAnnotation<A> {

    protected Field field;


    public FieldAnnotation(Field field, A annotation, List<A> classAnnotations, Configuration configuration) {
        super(annotation, classAnnotations, configuration);
        this.field = field;
    }


    public void setFieldValue(TestInstance testInstance, Object value) {
        testInstance.setFieldValue(field, value);
    }


    public Field getField() {
        return field;
    }
}
