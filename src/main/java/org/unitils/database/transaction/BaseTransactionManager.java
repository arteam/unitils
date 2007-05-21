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

import org.unitils.database.DatabaseModule;
import org.unitils.core.Unitils;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseTransactionManager<T extends DataSource> implements TransactionManager {

    protected T dataSource;

    public void startTransaction(Object testObject) {
        if (dataSource != null) {
            doStartTransaction(testObject);
        }
    }

    abstract protected void doStartTransaction(Object testObject);

    public void commit(Object testObject) {
        if (dataSource != null) {
            doCommit(testObject);
        }
    }

    abstract protected void doCommit(Object testObject);

    public void rollback(Object testObject) {
        if (dataSource != null) {
            doRollback(testObject);
        }
    }

    abstract protected void doRollback(Object testObject);

    public DataSource registerDataSource(DataSource originalDataSource) {
        dataSource = wrapDataSource(originalDataSource);
        return dataSource;
    }

    abstract protected T wrapDataSource(DataSource originalDataSource);

}
