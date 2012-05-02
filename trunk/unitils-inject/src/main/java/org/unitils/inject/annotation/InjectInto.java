/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.inject.annotation;

import org.unitils.inject.listener.InjectIntoFieldAnnotationListener;
import org.unitilsnew.core.annotation.AnnotationDefault;
import org.unitilsnew.core.annotation.FieldAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation indicating that the the {@link org.unitils.inject.InjectModule} should try to inject the object assigned to
 * the annotated field to the object defined by the target attribute (or the object(s) assigned to the field annotated
 * with {@link TestedObject}.
 * <p/>
 * Explicit injection is used, which means that the object is injected to the property indicated by the {@link #property()}
 * attribute.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target(FIELD)
@Retention(RUNTIME)
@FieldAnnotation(InjectIntoFieldAnnotationListener.class)
public @interface InjectInto {

    /**
     * The name(s) of the field(s) that references the object to which the object in the annotated field should be injected.
     * If not specified, the targets are defined by the fields annotated with {@link TestedObject}
     *
     * @return the target field, null for tested object
     */
    String[] target() default {};

    /**
     * OGNL expression that defines the property to which the object referenced by the annotated field is injected
     *
     * @return the ognl expression, not null
     */
    String property();

    @AnnotationDefault("inject.autoCreateInnerFields") boolean autoCreateInnerFields() default true;
}
