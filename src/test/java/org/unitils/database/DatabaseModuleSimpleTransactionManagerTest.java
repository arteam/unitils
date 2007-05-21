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

import static org.easymock.classextension.EasyMock.expect;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.transaction.SimpleTransactionManager;
import org.unitils.database.transaction.TransactionManager;
import static org.unitils.database.transaction.TransactionMode.COMMIT;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.sql.Connection;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleSimpleTransactionManagerTest extends DatabaseModuleTransactionalTest {

    private RollbackTest rollbackTest;

    private CommitTest commitTest;

    /**
     * Initializes the test fixture.
     */
    public void setUp() throws Exception {
        super.setUp();

        databaseModule = new DatabaseModule() {
            protected TransactionManager createTransactionManager() {
                return new SimpleTransactionManager();
            }
        };
        databaseModule.init(configuration);

        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();
    }

    public void testTransactions_simpleTransactionManager_disabled() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(false).andReturn(true).anyTimes();
        mockConnection1.setAutoCommit(true);
        mockConnection1.close();

        expect(mockConnection2.getAutoCommit()).andReturn(false).andReturn(true).anyTimes();
        mockConnection2.setAutoCommit(true);
        mockConnection2.close();
        replay();

        databaseModule.initTransactionManager();
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertNotSame(conn1, conn2);
    }

    public void testTransactions_simpleTransactionManager_rollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay();
        
        databaseModule.initTransactionManager();
        databaseModule.startTransaction(rollbackTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(rollbackTest, RollbackTest.class.getMethod("test"));
    }

    public void testTransactions_simpleTransactionManager_commit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay();

        databaseModule.initTransactionManager();
        databaseModule.startTransaction(commitTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(commitTest, CommitTest.class.getMethod("test"));
    }


    public static class RollbackTest {

        public void test() {}
    }

    @Transactional(COMMIT)
    public static class CommitTest {

        public void test() {}
    }


}
