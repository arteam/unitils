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
package org.unitils.database.util;

/**
 * Defining the available transaction modes for a test. Defines whether a test must be run in a transaction and, 
 * if yes, what is the commit/rollback behavior.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public enum TransactionMode {

    /**
     * Value indicating that transactions should be disabled, i.e. the test should not be run in a transaction
     */
    DISABLED,

    /**
     * Value indicating that the test should be executed in a transaction, and that this transaction must be committed
     * at the end of the test.
     */
    COMMIT,

    /**
     * Value indicating that the test should be executed in a transaction, and that this transaction must be rollbacked
     * at the end of the test.
     */
    ROLLBACK,

    /**
     * Value indicating that the default behavior is defined by the unitils property
     * <code>DatabaseModule.Transactional.value.default</code> is in use.
     */
    DEFAULT

}
