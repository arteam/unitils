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

package org.unitilsnew.database.dbmaintain;

import org.dbmaintain.database.DatabaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.database.annotations.TestDataSource;
import org.unitilsnew.database.core.TransactionManager;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitilsnew.database.SqlAssert.assertTableCount;
import static org.unitilsnew.database.SqlUnitils.executeUpdate;
import static org.unitilsnew.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandlerExecuteUpdateAndCommitTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainSQLHandler dbMaintainSQLHandler;

    private Mock<TransactionManager> transactionManagerMock;
    @TestDataSource
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainSQLHandler = new DbMaintainSQLHandler(transactionManagerMock.getMock());

        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void executeUpdateAndCommit() {
        transactionManagerMock.returns(true).isTransactionActive();

        dbMaintainSQLHandler.executeUpdateAndCommit("insert into my_table(id) values(10)", dataSource);

        assertTableCount(1, "my_table");
        transactionManagerMock.assertInvoked().commit(false);
    }

    @Test
    public void noCommitIfNoTransactionIsActive() {
        dbMaintainSQLHandler.executeUpdateAndCommit("insert into my_table(id) values(10)", dataSource);

        assertTableCount(1, "my_table");
        transactionManagerMock.assertNotInvoked().commit(false);
    }

    @Test
    public void exceptionWhenFailure() {
        try {
            dbMaintainSQLHandler.executeUpdateAndCommit("xx", dataSource);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            assertEquals("Unable to perform database update:\n" +
                    "xx", e.getMessage());
            transactionManagerMock.assertNotInvoked().commit(false);
        }
    }
}
