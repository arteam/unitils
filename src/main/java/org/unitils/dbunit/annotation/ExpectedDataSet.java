/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbunit.annotation;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Indicates that after having executed the annotated test method, the contents of the unit test database should be
 * equal to the contents of either the default result datafile (ClassName.methodName-result.xml), or a file with a
 * custom specified name.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ExpectedDataSet {

    String value() default "";

}
