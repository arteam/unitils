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
package org.unitils.database;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.Transactional;
import org.unitils.mock.Mock;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.unitils.database.util.TransactionMode.*;
import static org.unitils.testutil.TestUnitilsConfiguration.getUnitilsConfiguration;

/**
 * Tests verifying whether the SimpleTransactionManager functions correctly.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleTransactionManagerTest extends UnitilsJUnit4 {

    protected Mock<DataSource> mockDataSource;
    protected Mock<Connection> mockConnection1;
    protected Mock<Connection> mockConnection2;


    private DatabaseModule databaseModule;

    private TransactionsDisabledTest transactionsDisabledTest;

    private RollbackTest rollbackTest;

    private CommitTest commitTest;


    @Before
    public void initialize() throws Exception {
        mockDataSource.onceReturns(mockConnection1).getConnection();
        mockDataSource.onceReturns(mockConnection2).getConnection();

        Properties configuration = getUnitilsConfiguration();
        configuration.setProperty("unitils.module.spring.enabled", "false");

        databaseModule = new TestDatabaseModule();
        databaseModule.init(configuration);
        databaseModule.afterInit();

        transactionsDisabledTest = new TransactionsDisabledTest();
        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();
    }


    @Test
    public void transactionsDisabled() throws Exception {
        Method testMethod = TransactionsDisabledTest.class.getMethod("test");
        databaseModule.startTransactionForTestMethod(transactionsDisabledTest, testMethod);

        Connection conn1 = databaseModule.getDataSourceAndActivateTransactionIfNeeded(this).getConnection();
        conn1.close();

        Connection conn2 = databaseModule.getDataSourceAndActivateTransactionIfNeeded(this).getConnection();
        conn2.close();

        assertNotSame(conn1, conn2);
        databaseModule.endTransactionForTestMethod(transactionsDisabledTest, testMethod);

        mockConnection1.assertInvoked().close();
        mockConnection2.assertInvoked().close();
    }

    @Test
    public void rollback() throws Exception {
        mockConnection1.returns(true).getAutoCommit();

        Method testMethod = RollbackTest.class.getMethod("test");
        databaseModule.startTransactionForTestMethod(rollbackTest, testMethod);

        Connection conn1 = databaseModule.getDataSourceAndActivateTransactionIfNeeded(this).getConnection();
        conn1.close();

        databaseModule.endTransactionForTestMethod(rollbackTest, testMethod);

        mockConnection1.assertInvoked().setAutoCommit(false);
        mockConnection1.assertInvoked().rollback();
        mockConnection1.assertInvoked().close();
    }

    @Test
    public void testCommit() throws Exception {
        mockConnection1.returns(false).getAutoCommit();

        Method testMethod = CommitTest.class.getMethod("test");
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


    @Transactional(DISABLED)
    public static class TransactionsDisabledTest {

        public void test() {
        }
    }

    @Transactional(ROLLBACK)
    public static class RollbackTest {

        public void test() {
        }
    }

    @Transactional(COMMIT)
    public static class CommitTest {

        public void test() {
        }
    }


    public class TestDatabaseModule extends DatabaseModule {

        @Override
        public DataSource getDataSource() {
            return mockDataSource.getMock();
        }
    }

}
