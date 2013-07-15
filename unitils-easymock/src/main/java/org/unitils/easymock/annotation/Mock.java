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
package org.unitils.easymock.annotation;

import org.unitils.core.annotation.FieldAnnotation;
import org.unitils.easymock.listener.MockFieldAnnotationListener;
import org.unitils.easymock.util.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation indicating that a lenient mock object (see {@link org.unitils.easymock.util.LenientMocksControl} should be created
 * and set into the annotated field.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target(FIELD)
@Retention(RUNTIME)
@FieldAnnotation(MockFieldAnnotationListener.class)
public @interface Mock {

    /**
     * Determines whether the order of method calls on the mock object should be checked.
     *
     * @return the invocation order setting.
     */
    InvocationOrder invocationOrder() default InvocationOrder.DEFAULT;

    /**
     * Determines what to do when unexpected method calls occur.
     *
     * @return the calls setting.
     */
    Calls calls() default Calls.DEFAULT;

    /**
     * Determines whether the order of collection elements should be checked.
     *
     * @return the order setting.
     */
    Order order() default Order.DEFAULT;

    /**
     * Determines whether the actual value of a date argument should be checked.
     *
     * @return the dates setting.
     */
    Dates dates() default Dates.DEFAULT;

    /**
     * Determines whether default values of arguments should be checked.
     *
     * @return the default arguments setting.
     */
    Defaults defaults() default Defaults.DEFAULT;
}
