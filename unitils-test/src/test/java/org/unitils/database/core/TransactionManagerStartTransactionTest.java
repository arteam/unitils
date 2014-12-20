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
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionProvider;
import org.unitils.database.transaction.TransactionProviderManager;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class TransactionManagerStartTransactionTest extends UnitilsJUnit4 {

    /* Tested object */
    private TransactionManager transactionManager;

    private Mock<TransactionProviderManager> transactionProviderManagerMock;
    private Mock<DataSourceService> dataSourceServiceMock;
    private Mock<TransactionProvider> transactionProviderMock;
    private Mock<PlatformTransactionManager> platformTransactionManagerMock;
    @Dummy
    private TransactionStatus transactionStatus;
    @Dummy
    private DataSource dataSource;
    @Dummy
    private DataSource defaultDataSource;


    @Before
    public void initialize() {
        transactionManager = new TransactionManager(transactionProviderManagerMock.getMock(), dataSourceServiceMock.getMock());

        dataSourceServiceMock.returns(defaultDataSource).getDataSource(isNull(String.class));
        dataSourceServiceMock.returns(dataSource).getDataSource("myDatabase");
        transactionProviderManagerMock.returns(transactionProviderMock).getTransactionProvider();
        transactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager("", defaultDataSource);
        transactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager("", dataSource);
        transactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager("myTransactionManager", dataSource);
        transactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager("otherTransactionManager", dataSource);
        platformTransactionManagerMock.returns(transactionStatus).getTransaction(null);
    }

    @Test
    public void startTransaction() throws Exception {
        transactionManager.startTransaction("myDatabase", "myTransactionManager");
        assertTrue(transactionManager.isTransactionActive());
        transactionProviderMock.assertInvoked().getPlatformTransactionManager("myTransactionManager", dataSource);
        platformTransactionManagerMock.assertInvoked().getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRED));
    }

    @Test
    public void ignoreWhenSameTransactionStartedMoreThanOnce() throws Exception {
        transactionManager.startTransaction("myDatabase", "myTransactionManager");
        transactionManager.startTransaction("myDatabase", "myTransactionManager");
        assertTrue(transactionManager.isTransactionActive());
        platformTransactionManagerMock.assertInvoked().getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRED));
        platformTransactionManagerMock.assertNoMoreInvocations();
    }


    @Test
    public void nullTransactionManagerNameSameAsBlankName() throws Exception {
        transactionManager.startTransaction(null, null);
        transactionProviderMock.assertInvoked().getPlatformTransactionManager("", defaultDataSource);
    }

    @Test
    public void exceptionWhenTransactionAlreadyStartedForOtherTransactionManager_currentIsDefault() throws Exception {
        transactionManager.startTransaction("myDatabase", null);
        try {
            transactionManager.startTransaction("myDatabase", "otherTransactionManager");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for transaction manager with name 'otherTransactionManager'. A transaction for the default transaction manager is already active.\n" +
                    "A transaction can only be started for 1 transaction manager at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionAlreadyStartedForOtherTransactionManager_newIsDefault() throws Exception {
        transactionManager.startTransaction("myDatabase", "myTransactionManager");
        try {
            transactionManager.startTransaction("myDatabase", null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for the default transaction manager. A transaction for transaction manager with name 'myTransactionManager' is already active.\n" +
                    "A transaction can only be started for 1 transaction manager at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionAlreadyStartedForOtherDatabase_currentIsDefault() throws Exception {
        transactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager("myTransactionManager", defaultDataSource);

        transactionManager.startTransaction(null, "myTransactionManager");
        try {
            transactionManager.startTransaction("myDatabase", "myTransactionManager");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for database with name 'myDatabase'. A transaction for the default database is already active.\n" +
                    "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionAlreadyStartedForOtherDatabase_newIsDefault() throws Exception {
        transactionManager.startTransaction("myDatabase", "myTransactionManager");
        try {
            transactionManager.startTransaction(null, "myTransactionManager");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to start transaction for the default database. A transaction for database with name 'myDatabase' is already active.\n" +
                    "A transaction can only be started for 1 data source at the same time. If you want a transaction spanning multiple data sources, you will need to set up an XA-transaction manager in a spring context.", e.getMessage());
        }
    }
}
