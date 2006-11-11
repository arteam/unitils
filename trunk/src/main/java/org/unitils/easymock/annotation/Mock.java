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
package org.unitils.easymock.annotation;

import org.unitils.easymock.util.InvocationOrder;
import org.unitils.easymock.util.Returns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a mock object should be created and set in the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mock {


    /**
     * Determines whether the order of method calls on the mock object should be checked.
     *
     * @return the invocation order setting.
     */
    public InvocationOrder invocationOrder() default InvocationOrder.DEFAULT;


    /**
     * Determines what to do when no return value is recorded for a method.
     *
     * @return the returns setting.
     */
    public Returns returns() default Returns.DEFAULT;


}
