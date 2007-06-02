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
import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;
import static org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;
import static org.unitils.database.util.TransactionMode.*;
import org.unitils.spring.annotation.SpringApplicationContext;

import java.sql.Connection;

/**
 * Tests verifying whether the SpringIntegratingTransactionManagerTest functions correctly.
 *
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
    @Override
    public void setUp() throws Exception {
        super.setUp();

        Unitils.getInstance().init(configuration);
        databaseModule = getDatabaseModule();

        noApplicationContextTest = new NoApplicationContextTest();
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
     * Tests for a test with transactions enabled but no spring transaction manager configured in
     * an application context associated with the test.
     */
    public void testNoSpringTransactionManagerConfigured() throws Exception {
        mockConnection1.close();
        mockConnection2.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransaction(noApplicationContextTest);
        Connection conn1 = getConnection(databaseModule.getDataSource());
        Connection conn2 = getConnection(databaseModule.getDataSource());
        assertNotSame(conn1, conn2);
        releaseConnection(conn1, databaseModule.getDataSource());
        releaseConnection(conn2, databaseModule.getDataSource());
        databaseModule.commitOrRollbackTransaction(noApplicationContextTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test that has an ApplicationContext configured, rolling back the transaction
     */
    public void testRollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.rollback();
        mockConnection1.close();
        mockConnection2.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransaction(rollbackTest);
        Connection conn1 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(rollbackTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test that has an ApplicationContext configured, committing the transaction
     */
    public void testCommit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransaction(commitTest);
        Connection conn1 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(commitTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Class that plays the role of a unit test, with transaction rollback enabled, but no
     * spring application context configured, so no transaction will be started
     */
    @Transactional(DISABLED)
    public static class NoApplicationContextTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with a TransactionManager configured in a spring
     * application context, with transaction rollback enabled (=default, so no @Transactional
     * annotation required
     */
    @SpringApplicationContext("org/unitils/database/TransactionManagerApplicationContext.xml")
    @Transactional(ROLLBACK)
    public static class RollbackTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with a TransactionManager configured in a spring
     * application context, with transaction commit enabled
     */
    @SpringApplicationContext("org/unitils/database/TransactionManagerApplicationContext.xml")
    @Transactional(COMMIT)
    public static class CommitTest {

        public void test() {
        }
    }

}
