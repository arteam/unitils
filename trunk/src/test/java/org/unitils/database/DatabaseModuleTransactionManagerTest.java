/*
 * Copyright 2006-2007,  Unitils.org
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

import static org.easymock.EasyMock.*;
import org.junit.After;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;
import static org.unitils.database.util.TransactionMode.*;

import javax.sql.DataSource;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Tests verifying whether the SimpleTransactionManager functions correctly.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleTransactionManagerTest extends DatabaseModuleTransactionalTest {

    private DatabaseModule databaseModule;

    private TransactionsDisabledTest transactionsDisabledTest;

    private RollbackTest rollbackTest;

    private CommitTest commitTest;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        configuration.setProperty("unitils.module.spring.enabled", "false");
        Unitils.getInstance().init(configuration);
        databaseModule = getDatabaseModule();

        transactionsDisabledTest = new TransactionsDisabledTest();
        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();
    }


    /**
     * Cleans up test by resetting the unitils instance.
     */
    @After
    public void tearDown() throws Exception {
        Unitils.getInstance().init();
    }


    /**
     * Tests for a test with transactions disabled
     */
    @Test
    public void testWithTransactionsDisabled() throws Exception {
        mockConnection1.close();
        mockConnection2.close();
        replay(mockConnection1, mockConnection2);

        Method testMethod = TransactionsDisabledTest.class.getMethod("test", new Class[] {});
        databaseModule.startTransactionForTestMethod(transactionsDisabledTest, testMethod);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertNotSame(conn1, conn2);
        databaseModule.endTransactionForTestMethod(transactionsDisabledTest, testMethod);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test with transaction rollback configured
     */
    @Test
    public void testRollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        Method testMethod = RollbackTest.class.getMethod("test", new Class[] {});
        DataSource dataSource = databaseModule.getTransactionalDataSource(rollbackTest);
        databaseModule.startTransactionForTestMethod(rollbackTest, testMethod);
        Connection connection1 = dataSource.getConnection();
        Connection targetConnection1 = ((ConnectionProxy)connection1).getTargetConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        Connection targetConnection2 = ((ConnectionProxy)connection2).getTargetConnection();
        connection2.close();
        assertSame(targetConnection1, targetConnection2);
        databaseModule.endTransactionForTestMethod(rollbackTest, testMethod);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test with transaction commit configured
     */
    @Test
    public void testCommit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        Method testMethod = CommitTest.class.getMethod("test", new Class[] {});
        DataSource dataSource = databaseModule.getTransactionalDataSource(commitTest);
        databaseModule.startTransactionForTestMethod(commitTest, testMethod);
        Connection connection1 = dataSource.getConnection();
        Connection targetConnection1 = ((ConnectionProxy)connection1).getTargetConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        Connection targetConnection2 = ((ConnectionProxy)connection2).getTargetConnection();
        connection2.close();
        assertSame(targetConnection1, targetConnection2);
        databaseModule.endTransactionForTestMethod(commitTest, testMethod);

        verify(mockConnection1, mockConnection2);
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
