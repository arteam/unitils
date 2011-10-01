/*
 * Copyright Unitils.org
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
package org.unitils.database.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTransactionManagerTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTransactionManager unitilsTransactionManager = new UnitilsTransactionManager();

    @TestDataSource
    protected DataSource dataSource;
    @Dummy
    protected DataSource otherDataSource;


    @Before
    public void createTestTable() {
        dropTestTable();
        executeUpdate("create table test (val integer)", dataSource);
    }

    @After
    public void dropTestTable() {
        try {
            unitilsTransactionManager.rollback();
        } catch (Exception e) {
            // ingored
        }
        executeUpdateQuietly("drop table test", dataSource);
    }


    @Test
    public void commitTransaction() {
        unitilsTransactionManager.startTransactionForDataSource(dataSource);
        performDatabaseUpdate();
        unitilsTransactionManager.commit();

        assertDatabaseUpdateCommitted();
    }

    @Test
    public void rollbackTransaction() {
        unitilsTransactionManager.startTransactionForDataSource(dataSource);
        performDatabaseUpdate();
        unitilsTransactionManager.rollback();

        assertDatabaseUpdateRolledBack();
    }

    @Test
    public void ignoreWhenStartedMoreThanOnceOnSameDataSource() {
        unitilsTransactionManager.startTransactionForDataSource(dataSource);
        unitilsTransactionManager.startTransactionForDataSource(dataSource);
        performDatabaseUpdate();
        unitilsTransactionManager.rollback();

        assertDatabaseUpdateRolledBack();
    }

    @Test
    public void failWhenStartedMoreThanOnceOnDifferentDataSource() {
        try {
            unitilsTransactionManager.startTransactionForDataSource(dataSource);
            unitilsTransactionManager.startTransactionForDataSource(otherDataSource);
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to start transaction: a transaction for another data source is already active for this test."));
        }
    }


    private void performDatabaseUpdate() {
        executeUpdate("insert into test(val) values(1)", dataSource);
    }

    private void assertDatabaseUpdateCommitted() {
        assertEquals(1, getItemAsLong("select count(*) from test", dataSource));
    }

    private void assertDatabaseUpdateRolledBack() {
        assertEquals(0, getItemAsLong("select count(*) from test", dataSource));
    }
}
