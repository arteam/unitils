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
import static org.unitils.database.transaction.TransactionMode.COMMIT;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.sql.Connection;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.transaction.SpringIntegratingTransactionManager;
import org.unitils.database.transaction.TransactionManager;
import org.unitils.spring.SpringModule;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleSpringIntegratingTransactionManagerTest extends DatabaseModuleTransactionalTest {

    private NoApplicationContextTest noApplicationContextTest;

    private RollbackTest rollbackTest;

    private CommitTest commitTest;

    /**
     * Initializes the test fixture.
     */
    public void setUp() throws Exception {
        super.setUp();

        databaseModule = new DatabaseModule() {
            protected TransactionManager createTransactionManager() {
                return new SpringIntegratingTransactionManager();
            }
        };
        databaseModule.init(configuration);

        noApplicationContextTest = new NoApplicationContextTest();
        rollbackTest = new RollbackTest();
        commitTest = new CommitTest();
    }

    public void testTransactions_springIntegratingTransactionManager_noSpringTransactionManagerConfigured() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay();

        databaseModule.initTransactionManager();
        databaseModule.startTransaction(noApplicationContextTest);
        Connection conn1 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(noApplicationContextTest);
    }

    public void testTransactions_springIntegratingTransactionManager_rollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay();

        mockDataSource = (MockDataSource) getSpringModule().getApplicationContext(rollbackTest).getBean("dataSource");
        databaseModule.initTransactionManager();
        databaseModule.startTransaction(rollbackTest);
        Connection conn1 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(rollbackTest);
    }

    public void testTransactions_springIntegratingTransactionManager_commit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay();

        mockDataSource = (MockDataSource) getSpringModule().getApplicationContext(commitTest).getBean("dataSource");
        databaseModule.initTransactionManager();
        databaseModule.startTransaction(commitTest);
        Connection conn1 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(commitTest);
    }

    private SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

    public static class NoApplicationContextTest {

        public void test() {}
    }

    @SpringApplicationContext("org/unitils/database/TransactionManagerApplicationContext.xml")
    public static class RollbackTest {

        public void test() {}
    }

    @SpringApplicationContext("org/unitils/database/TransactionManagerApplicationContext.xml")
    @Transactional(COMMIT)
    public static class CommitTest {

        public void test() {}
    }

}
