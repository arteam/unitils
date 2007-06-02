/*
 * Copyright 2006 the original author or authors.
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
import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;
import static org.unitils.database.util.TransactionMode.*;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Tests verifying whether the SimpleTransactionManager functions correctly.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleSimpleTransactionManagerTest extends DatabaseModuleTransactionalTest {

    private TransactionsDisabledTest transactionsDisabledTest;

    private RollbackTest rollbackTest;

    private CommitTest commitTest;


    /**
     * Initializes the test fixture.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

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
    @Override
    public void tearDown() throws Exception {
        Unitils.getInstance().init();
    }


    /**
     * Tests for a test with transactions disabled
     */
    public void testWithTransactionsDisabled() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true);
        expect(mockConnection2.getAutoCommit()).andReturn(true);
        mockConnection1.close();
        mockConnection2.close();        
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransaction(transactionsDisabledTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertNotSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(transactionsDisabledTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test with transaction rollback configured
     */
    public void testRollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        DataSource dataSource = databaseModule.getDataSource();
        databaseModule.startTransaction(rollbackTest);
        Connection connection1 = dataSource.getConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        connection2.close();
        assertSame(connection1, connection2);
        databaseModule.commitOrRollbackTransaction(rollbackTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test with transaction commit configured
     */
    public void testCommit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        DataSource dataSource = databaseModule.getDataSource();
        databaseModule.startTransaction(commitTest);
        Connection connection1 = dataSource.getConnection();
        connection1.close();
        Connection connection2 = dataSource.getConnection();
        connection2.close();
        assertSame(connection1, connection2);
        databaseModule.commitOrRollbackTransaction(commitTest);

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
     * Class that plays the role of a unit test, with transaction rollback enabled (=default,
     * so no @Transactional annotation required
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
