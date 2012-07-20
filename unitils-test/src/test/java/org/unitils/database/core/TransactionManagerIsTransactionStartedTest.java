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
import org.unitils.database.transaction.TransactionProvider;
import org.unitils.database.transaction.TransactionProviderManager;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TransactionManagerIsTransactionStartedTest extends UnitilsJUnit4 {

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
    public void started() throws Exception {
        transactionManager.startTransaction("myTransactionManager");

        boolean result = transactionManager.isTransactionStarted();
        assertTrue(result);
    }

    @Test
    public void notStarted() throws Exception {
        boolean result = transactionManager.isTransactionStarted();
        assertFalse(result);
    }

    @Test
    public void ended() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        transactionManager.registerDataSource(dataSource);
        transactionManager.rollback(true);

        boolean result = transactionManager.isTransactionStarted();
        assertFalse(result);
    }

    @Test
    public void keptOpen() throws Exception {
        transactionManager.startTransaction("myTransactionManager");
        transactionManager.registerDataSource(dataSource);
        transactionManager.rollback(false);

        boolean result = transactionManager.isTransactionStarted();
        assertTrue(result);
    }
}
