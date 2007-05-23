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
package org.unitils.database.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.unitils.database.transaction.TransactionMode.DEFAULT;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.unitils.database.transaction.TransactionMode;

/**
 * Annotation enabling to specify if tests should be run in a transaction and, if yes, whether at the end of the test,
 * the transaction should be <i>committed</i> or <i>rollbacked</i>.
 * <p/>
 * This annotation can be specified on a class, and is valid for all tests in the annotated class and its subclasses. A
 * class level annotation overrides the settings of a superclass annotation.
 * <p/>
 * The value attribute defines whether the annotated test(s) run in a transaction and, if yes, what will be the
 * commit/rollback behavior. The default behavior is defined by the unitils property
 * <code>DatabaseModule.Transactional.value.default</code>. This configured default will be used when the value property
 * is unspecified or explicitly set to {@link TransactionMode#DEFAULT}.
 *
 * @see TransactionMode
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface Transactional {

    /**
     * Defines whether the annotated test(s) run in a transaction and, if yes, what will be commit/rollback behavior.
     * The default behavior is defined by the unitils property <code>DatabaseModule.Transactional.value.default</code>.
     * This configured default will be used when the value property
     * is unspecified or explicitly set to {@link TransactionMode#DEFAULT}.
     *
     * @return The TransactionMode
     */
    TransactionMode value() default DEFAULT;
}
