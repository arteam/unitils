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
package org.unitils.database.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation indicating that this field or method should be initialized with the <code>DataSource</code> that supplies
 * a connection to the unit test database.
 * <p/>
 * If a field is annotated, it should be of type <code>DataSource</code>. This field can be private. Example:
 * <pre><code>
 * '    @DataSource
 *      private DataSource dataSource;
 * </code></pre>
 * If a method is annotated, the method should have 1 DataSource argument. Example:
 * <pre><code>
 * '    @DataSource
 *      void myMethod(DataSource dataSource)
 * </code></pre>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface TestDataSource {
    String value() default "";
}
