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

package org.unitils.database.dbmaintain;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.core.TransactionManager;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static org.unitils.mock.MockUnitils.assertNoMoreInvocations;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandlerTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainSQLHandler dbMaintainSQLHandler;

    private Mock<TransactionManager> transactionManagerMock;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainSQLHandler = new DbMaintainSQLHandler(transactionManagerMock.getMock());
    }


    @Test
    public void startTransaction() {
        dbMaintainSQLHandler.startTransaction(dataSource);

        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
        transactionManagerMock.assertInvoked().startTransaction();
    }

    @Test
    public void closeAllConnections() {
        dbMaintainSQLHandler.closeAllConnections();
        assertNoMoreInvocations();
    }
}
