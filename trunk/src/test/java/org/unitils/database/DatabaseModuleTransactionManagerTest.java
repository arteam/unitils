/*
 * Copyright 2008,  Unitils.org
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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.datasource.UnitilsDataSource;
import static org.unitils.database.util.TransactionMode.*;
import org.unitils.mock.Mock;
import static org.unitils.mock.MockUnitils.createMock;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

/**
 * Tests verifying whether the SimpleTransactionManager functions correctly.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleTransactionManagerTest extends UnitilsJUnit4 {

    TransactionsDisabledTest transactionsDisabledTest;

    RollbackTest rollbackTest;

    CommitTest commitTest;

    DatabaseModule databaseModule;

    Mock<DataSource> mockDataSource;

    Mock<Connection> mockConnection1, mockConnection2;

    /* The unitils configuration */
    protected Properties configuration;

    /**
     * Initializes the mocked datasource and connections.
     */
    @Before
    public void initialize() throws Exception {
        final Mock<UnitilsDataSource> mockUnitilsDataSource = createMock(UnitilsDataSource.class);
        mockUnitilsDataSource.returns(mockDataSource.getMock()).getDataSource();

        mockDataSource.onceReturns(mockConnection1.getMock()).getConnection();
        mockDataSource.onceReturns(mockConnection2.getMock()).getConnection();

        configuration = new ConfigurationLoader().loadConfiguration();
        databaseModule = new DatabaseModule() {
            @Override
            protected void initUnitilsDataSources() {
                this.defaultUnitilsDataSource = mockUnitilsDataSource.getMock();
            }
        };
        databaseModule.init(configuration);
        databaseModule.afterInit();

        transactionsDisabledTest = new TransactionsDisabledTest();
        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();
    }

    /**
     * Tests for a test with transactions disabled
     */
    @Test
    public void testWithTransactionsDisabled() throws Exception {
        Method testMethod = TransactionsDisabledTest.class.getMethod("test", new Class[]{});
        databaseModule.startTransactionForTestMethod(transactionsDisabledTest, testMethod);
        Connection conn1 = databaseModule.getDefaultUnitilsDataSourceAndActivateTransactionIfNeeded().getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDefaultUnitilsDataSourceAndActivateTransactionIfNeeded().getDataSource().getConnection();
        conn2.close();
        assertNotSame(conn1, conn2);
        databaseModule.endTransactionForTestMethod(transactionsDisabledTest, testMethod);

        mockConnection1.assertInvoked().close();
        mockConnection2.assertInvoked().close();
    }


    /**
     * Tests with a test with transaction rollback configured
     */
    @Test
    public void testRollback() throws Exception {
        mockConnection1.onceReturns(true).getAutoCommit();
        mockConnection1.onceReturns(false).getAutoCommit();

        Method testMethod = RollbackTest.class.getMethod("test", new Class[]{});
        DataSource dataSource = databaseModule.getTransactionalDataSourceAndActivateTransactionIfNeeded(rollbackTest);
        databaseModule.startTransactionForTestMethod(rollbackTest, testMethod);
        Connection connection1 = dataSource.getConnection();
        Connection targetConnection1 = ((ConnectionProxy) connection1).getTargetConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        Connection targetConnection2 = ((ConnectionProxy) connection2).getTargetConnection();
        connection2.close();
        assertSame(targetConnection1, targetConnection2);
        databaseModule.endTransactionForTestMethod(rollbackTest, testMethod);

        mockConnection1.assertInvoked().setAutoCommit(false);
        mockConnection1.assertInvoked().rollback();
        mockConnection1.assertInvoked().close();
    }


    /**
     * Tests with a test with transaction commit configured
     */
    @Test
    public void testCommit() throws Exception {
        mockConnection1.onceReturns(true).getAutoCommit();
        mockConnection1.returns(false).getAutoCommit();

        Method testMethod = CommitTest.class.getMethod("test", new Class[]{});
        DataSource dataSource = databaseModule.getTransactionalDataSourceAndActivateTransactionIfNeeded(commitTest);
        databaseModule.startTransactionForTestMethod(commitTest, testMethod);
        Connection connection1 = dataSource.getConnection();
        Connection targetConnection1 = ((ConnectionProxy) connection1).getTargetConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        Connection targetConnection2 = ((ConnectionProxy) connection2).getTargetConnection();
        connection2.close();
        assertSame(targetConnection1, targetConnection2);
        databaseModule.endTransactionForTestMethod(commitTest, testMethod);

        mockConnection1.assertInvoked().setAutoCommit(false);
        mockConnection1.assertInvoked().commit();
        mockConnection1.assertInvoked().close();
    }


    /**
     * Class that plays the role of a unit test, with transactions disabled
     */
    @Transactional(DISABLED)
    public static class TransactionsDisabledTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with transaction rollback enabled (=default, so no
     *
     * @Transactional annotation required
     */
    @Transactional(ROLLBACK)
    public static class RollbackTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with transaction commit enabled
     */
    @Transactional(COMMIT)
    public static class CommitTest {

        public void test() {
        }
    }

}
