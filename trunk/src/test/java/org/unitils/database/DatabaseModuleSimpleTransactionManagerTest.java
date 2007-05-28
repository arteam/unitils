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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.unitils.database.transaction.TransactionMode.COMMIT;
import static org.unitils.database.transaction.TransactionMode.DISABLED;
import static org.unitils.database.transaction.TransactionMode.ROLLBACK;

import java.sql.Connection;

import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.transaction.SimpleTransactionManager;

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

        configuration.setProperty("org.unitils.database.transaction.TransactionManager.implClassName", 
                SimpleTransactionManager.class.getName());
        Unitils.getInstance().init(configuration);
        
        databaseModule = getDatabaseModule();
        databaseModule.initTransactionManager();

        transactionsDisabledTest = new TransactionsDisabledTest();
        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();

    }
    
    
    @Override
    public void tearDown() throws Exception {
        Unitils.getInstance().init();
    }


    /**
     * Tests for a test with transactions disabled
     */
    public void testWithTransactionsDisabled() throws Exception {
        mockConnection1.close();
        mockConnection2.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransactionIfPossible(transactionsDisabledTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertNotSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(transactionsDisabledTest);

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

        databaseModule.startTransactionIfPossible(rollbackTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(rollbackTest);

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

        databaseModule.startTransactionIfPossible(commitTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(commitTest);

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
