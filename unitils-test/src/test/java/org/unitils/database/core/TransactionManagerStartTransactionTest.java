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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionProvider;
import org.unitils.database.transaction.TransactionProviderManager;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * @author Tim Ducheyne
 */
public class TransactionManagerStartTransactionTest extends UnitilsJUnit4 {

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
    public void transactionMarkedAsStartedWhenNoDataSourceRegistered() throws Exception {
        transactionManager.startTransaction("myTransactionManager");

        assertFalse(transactionManager.isTransactionActive());
        assertTrue(transactionManager.isTransactionStarted());
    }

    @Test
    public void transactionReallyStartedWhenDataSourceRegistered() throws Exception {
        transactionManager.registerDataSource(dataSource);
        transactionManager.startTransaction("myTransactionManager");

        assertTrue(transactionManager.isTransactionActive());
        assertTrue(transactionManager.isTransactionStarted());
        transactionProviderMock.assertInvoked().getPlatformTransactionManager("myTransactionManager", dataSource);
        platformTransactionManagerMock.assertInvoked().getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRED));
    }

    @Test
    public void startTransactionTwice() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        transactionManager.startTransaction("myTransactionManager");

        assertFalse(transactionManager.isTransactionActive());
        assertTrue(transactionManager.isTransactionStarted());
    }

    @Test
    public void startTransactionTwiceWhenDataSourceRegistered() throws Exception {
        transactionManager.registerDataSource(dataSource);
        transactionManager.startTransaction("myTransactionManager");
        transactionManager.startTransaction("myTransactionManager");

        assertTrue(transactionManager.isTransactionActive());
        assertTrue(transactionManager.isTransactionStarted());
        platformTransactionManagerMock.assertInvoked().getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRED));
    }

    @Test
    public void nullTransactionManagerNameSameAsBlankName() throws Exception {
        transactionManager.registerDataSource(dataSource);
        transactionManager.startTransaction(null);

        assertTrue(transactionManager.isTransactionActive());
        assertTrue(transactionManager.isTransactionStarted());
        transactionProviderMock.assertInvoked().getPlatformTransactionManager("", dataSource);
    }

    @Test
    public void exceptionWhenTransactionAlreadyStartedForOtherTransactionManager_currentIsDefault() throws Exception {
        transactionManager.startTransaction(null);
        try {
            transactionManager.startTransaction("otherTransactionManager");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for transaction manager with name 'otherTransactionManager'. A transaction for the default transaction manager is already active for this test.\n" +
                    "A transaction can only be started for 1 transaction manager at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionAlreadyStartedForOtherTransactionManager_newIsDefault() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        try {
            transactionManager.startTransaction(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for the default transaction manager. A transaction for transaction manager with name 'myTransactionManager' is already active for this test.\n" +
                    "A transaction can only be started for 1 transaction manager at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        }
    }
}
