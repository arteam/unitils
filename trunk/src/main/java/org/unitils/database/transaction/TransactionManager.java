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
package org.unitils.database.transaction;

import javax.sql.DataSource;


/**
 * Defines the contract for classes that can make sure unit tests managed by unitils are executed in a transaction.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface TransactionManager {


    /**
     * Initializes the transaction manager with the given {@link DataSource}.
     *
     * @param dataSource The data source, not null
     */
    public void setDataSource(DataSource dataSource);


    /**
     * Starts a transaction.
     *
     * @param testObject The test instance, not null
     */
    void startTransaction(Object testObject);


    /**
     * Commits the currently active transaction. This transaction must have been initiated by calling
     * {@link #startTransaction(Object)} with the same testObject within the same thread.
     *
     * @param testObject The test instance, not null
     */
    void commit(Object testObject);


    /**
     * Rolls back the currently active transaction. This transaction must have been initiated by calling
     * {@link #startTransaction(Object)} with the same testObject within the same thread.
     *
     * @param testObject The test instance, not null
     */
    void rollback(Object testObject);


}
