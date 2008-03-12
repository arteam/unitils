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

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * This annotation can be used in three different ways: 
 * <ul><li>
 * If its value attribute is specified, the goal is to configure a spring <code>ApplicationContext</code> for this
 * test object.
 * </li><li>
 * If this attributes is not specified and the annotation is put on a field of type <code>ApplicationContext</code> 
 * or a method that takes a single parameter  of type <code>ApplicationContext</code>, the <code>ApplicationContext</code> 
 * for this test object is injected into this field or method. 
 * </li><li>
 * If put on a method with no parameter or a single parameter of type <code>List&lt;String&gt;</code> that returns an 
 * <code>ApplicationContext</code>, this method becomes a custom create for this test class. If it has a parameter, 
 * this method will be invoked with the config locations as parameter. The result of this method should be an instance 
 * of an application context for which the refresh() method was not yet invoked.
 * </li></ul>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface SpringApplicationContext {


    String[] value() default {};

}
