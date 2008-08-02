/*
 * Copyright 2006-2007,  Unitils.org
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * This annotation can be used on fields or single-argument void methods, in order to inject a bean
 * from a spring <code>ApplicationContext</code>. The value attribute indicates the id of the bean
 * from the <code>ApplicationContext</code>. An <code>ApplicationContext</code> has to be configured
 * for this test using the {@link SpringApplicationContext} annotation.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface SpringBean {

    String value();
}
