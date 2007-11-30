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
     * Makes the given data source a transactional datasource.
     * <p/>
     * This could for example be used to wrap the given data source for intercepting the creation of connections.
     *
     * @param dataSource The original data source, not null
     * @return The transactional data source, not null
     */
    TransactionalDataSource createTransactionalDataSource(DataSource dataSource);
    

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
    
    
    /**
     * @param testObject The test instance, not null
     * @return Whether or not a transaction has been started, and hasn't been committed nor rollbacked yet
     */
    boolean isTransactionActive(Object testObject);

}
