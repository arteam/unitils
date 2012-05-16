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

package org.unitilsnew.database.core;

import org.junit.After;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.database.annotations.TestDataSource;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TransactionManagerRollbackTest extends UnitilsJUnit4 {

    /* Tested object */
    private TransactionManager transactionManager = new TransactionManager();

    @TestDataSource
    private DataSource dataSource;


    @After
    public void cleanup() {
        TransactionManager.dataSourceTransactionManagers.clear();
    }


    @Test
    public void transactionRunning() throws Exception {
        transactionManager.startTransaction();
        transactionManager.registerDataSource(dataSource);

        transactionManager.rollback();
        assertFalse(transactionManager.startTransactionWhenDataSourceIsRegistered);
        assertNull(transactionManager.transactionStatus);
    }

    @Test
    public void exceptionWhenNoTransactionRunning() throws Exception {
        try {
            transactionManager.rollback();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to rollback. No transaction is currently active. Make sure to call startTransaction to start a transaction.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionIsOnlyMarkedAsStartedButNoDataSourceWasRegistered() throws Exception {
        transactionManager.startTransaction();
        try {
            transactionManager.rollback();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to rollback. No transaction is currently active. Make sure to call startTransaction to start a transaction.", e.getMessage());
        }
    }
}
