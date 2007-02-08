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

package org.unitils.spring.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Annotation indicating that the annotated field or the result of the annotated method must be injected into
 * spring's <code>ApplicationContext</code>. The means that, for the duration of this test, the annotated field or
 * method result is used by all spring beans that are wired with the target bean. The target bean is defined by the
 * declared type of the annotated field or the declared result type of the annotated method. If a method is annotated,
 * this method should have no parameters and return a non-void result. This annotation only works when an
 * <code>ApplicationContext</code> is configured using {@link SpringApplicationContext}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface InjectIntoContextByType {
}
