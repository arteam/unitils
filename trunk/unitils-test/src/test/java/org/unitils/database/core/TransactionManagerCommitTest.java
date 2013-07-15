/*
 * Copyright 2013,  Unitils.org
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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionProvider;
import org.unitils.database.transaction.TransactionProviderManager;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class TransactionManagerCommitTest extends UnitilsJUnit4 {

    /* Tested object */
    private TransactionManager transactionManager;

    private Mock<TransactionProviderManager> transactionProviderManagerMock;
    private Mock<TransactionProvider> transactionProviderMock;
    private Mock<PlatformTransactionManager> platformTransactionManagerMock;
    @Dummy
    private TransactionStatus transactionStatus;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        transactionManager = new TransactionManager(transactionProviderManagerMock.getMock());

        transactionProviderManagerMock.returns(transactionProviderMock).getTransactionProvider();
        transactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager(null, null);
        platformTransactionManagerMock.returns(transactionStatus).getTransaction(null);
    }


    @Test
    public void endTransaction() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        transactionManager.registerDataSource(dataSource);

        transactionManager.commit(true);
        platformTransactionManagerMock.assertInvoked().commit(transactionStatus);
        assertFalse(transactionManager.isTransactionActive());
        assertFalse(transactionManager.isTransactionStarted());
    }

    @Test
    public void keepTransactionRunning() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        transactionManager.registerDataSource(dataSource);

        transactionManager.commit(false);
        platformTransactionManagerMock.assertInvoked().commit(transactionStatus);
        assertFalse(transactionManager.isTransactionActive());
        assertTrue(transactionManager.isTransactionStarted());
    }

    @Test
    public void exceptionWhenNoTransactionRunning() throws Exception {
        try {
            transactionManager.commit(false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to commit. No transaction is currently active. Make sure to call startTransaction to start a transaction.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionIsOnlyMarkedAsStartedButNoDataSourceWasRegistered() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        try {
            transactionManager.commit(false);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to commit. No transaction is currently active. Make sure to call startTransaction to start a transaction.", e.getMessage());
        }
    }
}
