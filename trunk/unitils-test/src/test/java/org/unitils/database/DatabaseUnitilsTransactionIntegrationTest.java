/*
 * Copyright 2012,  Unitils.org
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

package org.unitils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.database.core.DataSourceWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.DatabaseUnitils.getDataSourceWrapper;
import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlAssert.assertTableEmpty;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitilsTransactionIntegrationTest {


    @Before
    public void initialize() {
        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void commit() throws Exception {
        DatabaseUnitils.startTransaction();

        DataSourceWrapper dataSourceWrapper = getDataSourceWrapper();
        dataSourceWrapper.executeUpdate("insert into my_table(id) values (111)");

        DatabaseUnitils.commitTransaction();

        assertTableCount(1, "my_table");
    }

    @Test
    public void commitAndTransactionStartedAfterGetDataSource() throws Exception {
        DataSourceWrapper dataSourceWrapper = getDataSourceWrapper();
        DatabaseUnitils.startTransaction();

        dataSourceWrapper.executeUpdate("insert into my_table(id) values (111)");
        DatabaseUnitils.commitTransaction();

        assertTableCount(1, "my_table");
    }

    @Test
    public void rollback() throws Exception {
        DatabaseUnitils.startTransaction();
        DataSourceWrapper dataSourceWrapper = getDataSourceWrapper();

        dataSourceWrapper.executeUpdate("insert into my_table(id) values (111)");
        DatabaseUnitils.rollbackTransaction();

        assertTableEmpty("my_table");
    }

    @Test
    public void rollbackAndTransactionStartedAfterGetDataSource() throws Exception {
        DataSourceWrapper dataSourceWrapper = getDataSourceWrapper();
        DatabaseUnitils.startTransaction();

        dataSourceWrapper.executeUpdate("insert into my_table(id) values (111)");
        DatabaseUnitils.rollbackTransaction();

        assertTableEmpty("my_table");
    }

    @Test
    public void exceptionWhenCommitWithoutStartTransaction() throws Exception {
        try {
            DatabaseUnitils.commitTransaction();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to commit. No transaction is currently active. Make sure to call startTransaction to start a transaction.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenRollbackWithoutStartTransaction() throws Exception {
        try {
            DatabaseUnitils.rollbackTransaction();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to rollback. No transaction is currently active. Make sure to call startTransaction to start a transaction.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenStartTransactionMoreThanOnce() throws Exception {
        try {
            DatabaseUnitils.startTransaction();
            DatabaseUnitils.startTransaction();
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction. A transaction is already active. Make sure to call commit or rollback to end the transaction.", e.getMessage());
        } finally {
            DatabaseUnitils.rollbackTransaction();
        }
    }

    @Test
    public void exceptionWhenUsingMoreThanOneDataSourceDuringTransaction() throws Exception {
        try {
            DatabaseUnitils.startTransaction();
            DatabaseUnitils.getDataSource("database1");
            DatabaseUnitils.getDataSource("database2");
        } catch (UnitilsException e) {
            assertEquals("Unable to register data source. A transaction for another data source is already active for this test.\n" +
                    "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        } finally {
            DatabaseUnitils.rollbackTransaction();
        }
    }

    @Test
    public void exceptionWhenUsingMoreThanOneTransactionManager() throws Exception {
        try {
            DatabaseUnitils.startTransaction("myTransactionManager");
            DatabaseUnitils.startTransaction("otherTransactionManager");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for transaction manager with name 'otherTransactionManager'. A transaction for transaction manager with name 'myTransactionManager' is already active for this test.\n" +
                    "A transaction can only be started for 1 transaction manager at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        } finally {
            DatabaseUnitils.rollbackTransaction();
        }
    }
}
