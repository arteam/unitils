/*
 * Copyright Unitils.org
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
package org.unitils.dataset.annotation;

import org.unitils.dataset.annotation.handler.MarkerForAssertDataSetAnnotation;
import org.unitils.dataset.annotation.handler.impl.InlineAssertDataSetAnnotationHandler;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
@MarkerForAssertDataSetAnnotation(InlineAssertDataSetAnnotationHandler.class)
public @interface InlineAssertDataSet {

    String[] value() default {};

    /**
     * By default, the database content of the tables that were in the expected data set will be outputted to the log.
     * For performance reasons or when large tables are involved, it is possible to skip this logging by
     * setting this property to false.
     *
     * @return True for logging the database content, false otherwise
     */
    boolean logDatabaseContentOnAssertionError() default true;

}