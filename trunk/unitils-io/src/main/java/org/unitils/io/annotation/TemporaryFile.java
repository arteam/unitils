/*
 * Copyright 2011,  Unitils.org
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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for creating a temporary file. According to the setting the file wil be deleted at the end, by default it will be removed.
 * <p/>
 * When adding a value then this value is used as name. The temporary directory where the file is created can be set in the unitils.properties.
 * If left open the default temporary value of the jvm is used (java.io.tmpdir).
 * <p/>
 * It can be overridden with following setting :
 * <p/>
 * <code>
 * IOModule.temp.directory= "the temporary file location"
 * </code><p/>
 * When using maven adding <code>IOModule.temp.directory=target/test-classes/temp</code> could be a option.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface TemporaryFile {

    /**
     * @return The name of the file that will be created and afterward removed (when set correctly). If the file exists the work is considered done.
     */

    String value() default "";

}
