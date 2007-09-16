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
package org.unitils.inject.annotation;

import org.unitils.inject.util.Restore;

import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the the {@link org.unitils.inject.InjectModule} should try to inject the object assigned to
 * the annotated field to a static property of the class defined by the target attribute.
 * <p/>
 * Explicit injection is used, which means that the object is injected to the property indicated by the {@link #property()}
 * attribute.
 * <p/>
 * You can also specify what action needs to be performed after the test. Suppose, for example, that you want to
 * mock a singleton instance by injecting the mock into the static <code>singleton</code> variable. After the test
 * was performed, you typically want to restore the old (real) singleton value, so that the next test can run with
 * the normal instance. This can be done by setting the resetType to the OLD_VALUE value. You can also specify that
 * the static instance should be cleared by setting it to null (or 0) or just leave the injected value.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface InjectIntoStatic {

    /**
     * The target class to which the object referenced by the annotated field is injected
     *
     * @return the target class, null for tested object
     */
	Class<?> target();

    /**
     * OGNL expression that defines the property to which the object referenced by the annotated fiel is injected
     *
     * @return the ognl expression, not null
     */
    String property();

    /**
     * The action that needs to be performed after the test was performed. Should the old value be put back,
     * should it be set to a java default value (null) or should nothing be done.
     *
     * @return the reset type, not null
     */
    Restore restore() default Restore.DEFAULT;

}
