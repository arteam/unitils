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

package org.unitils.io.annotation;

import org.unitils.core.annotation.FieldAnnotation;
import org.unitils.io.listener.TempFileFieldAnnotationListener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for creating a temporary file.
 * <p/>
 * A optional name can be specified for file. If no name is specified, a default name 'class-name'-'method-name'.tmp will be used.
 * <p/>
 * The parent directory for this file can be
 * specified by setting the {@link org.unitils.io.temp.TempService#ROOT_TEMP_DIR_PROPERTY} property.
 * If no root temp dir is specified the default user temp dir will be used.
 * <p/>
 * Watch out: if the file already exists, it will first be deleted.
 * <p/>
 * By default, the file will not be removed after the test. You can set the {@link org.unitils.io.listener.TempFileFieldAnnotationListener#CLEANUP_AFTER_TEST_PROPERTY}
 * property to true if you want unitils to delete the files automatically after each test.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
@Target(FIELD)
@Retention(RUNTIME)
@FieldAnnotation(value = TempFileFieldAnnotationListener.class)
public @interface TempFile {

    /**
     * @return The name for the temp file. If not specified, a default name 'class-name'-'method-name'.tmp will be used.
     */
    String value() default "";

}
