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
package org.unitils.mock.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for indicating that a method is to be called after a mock was created. This method can for example
 * do some extra configuration or install the instance in a service locator.
 * <p/>
 * The method should have following signature: <code>void myMethod(Object mock, String name, Class type)</code>
 * <p/>
 * The passed object is the created mock.
 * The name is the name of the mock, typically the name of the field.
 * The type is the class type of the mocked instance.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AfterCreateMock {
}

