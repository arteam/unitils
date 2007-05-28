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
import static org.springframework.jdbc.datasource.DataSourceUtils.getConnection;
import static org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection;
import static org.unitils.database.transaction.TransactionMode.COMMIT;
import static org.unitils.database.transaction.TransactionMode.DISABLED;
import static org.unitils.database.transaction.TransactionMode.ROLLBACK;

import java.sql.Connection;

import org.unitils.core.Unitils;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.transaction.AutoDetectTransactionManager;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * Tests verifying whether the AutoDetectTransactionManager functions correctly.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleAutoDetectTransactionManagerTest extends DatabaseModuleTransactionalTest {

    private TransactionsDisabledTest transactionsDisabledTest;

    private NoApplicationContextRollbackTest noApplicationContextRollbackTest;

    private NoApplicationContextCommitTest noApplicationContextCommitTest;

    private ApplicationContextRollbackTest applicationContextRollbackTest;

    private ApplicationContextCommitTest applicationContextCommitTest;

    /**
     * Initializes the test fixture.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        configuration.setProperty("org.unitils.database.transaction.TransactionManager.implClassName", 
                AutoDetectTransactionManager.class.getName());
        Unitils.getInstance().init(configuration);
        
        databaseModule = getDatabaseModule();
        databaseModule.initTransactionManager();

        transactionsDisabledTest = new TransactionsDisabledTest();
        noApplicationContextRollbackTest = new NoApplicationContextRollbackTest();
        noApplicationContextCommitTest = new NoApplicationContextCommitTest();
        applicationContextRollbackTest = new ApplicationContextRollbackTest();
        applicationContextCommitTest = new ApplicationContextCommitTest();
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
     * Tests with a test that has no ApplicationContext configured, rolling back the transaction
     */
    public void testNoApplicationContext_rollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransactionIfPossible(noApplicationContextRollbackTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(noApplicationContextRollbackTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test that has no ApplicationContext configured, committing the transaction
     */
    public void testNoApplicationContext_commit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andReturn(true).andReturn(false).anyTimes();
        mockConnection1.setAutoCommit(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransactionIfPossible(noApplicationContextCommitTest);
        Connection conn1 = databaseModule.getDataSource().getConnection();
        conn1.close();
        Connection conn2 = databaseModule.getDataSource().getConnection();
        conn2.close();
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(noApplicationContextCommitTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test that has an ApplicationContext configured, rolling back the transaction
     */
    public void testSpringConfigured_rollback() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransactionIfPossible(applicationContextRollbackTest);
        Connection conn1 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(applicationContextRollbackTest);

        verify(mockConnection1, mockConnection2);
    }


    /**
     * Tests with a test that has an ApplicationContext configured, committing the transaction
     */
    public void testSpringConfigured_commit() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.commit();
        mockConnection1.close();
        replay(mockConnection1, mockConnection2);

        databaseModule.startTransactionIfPossible(applicationContextCommitTest);
        Connection conn1 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = getConnection(databaseModule.getDataSource());
        releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransactionIfPossible(applicationContextCommitTest);

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
    public static class NoApplicationContextRollbackTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with transaction commit enabled
     */
    @Transactional(COMMIT)
    public static class NoApplicationContextCommitTest {

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
    public static class ApplicationContextRollbackTest {

        public void test() {
        }
    }


    /**
     * Class that plays the role of a unit test, with a TransactionManager configured in a spring
     * application context, with transaction commit enabled
     */
    @SpringApplicationContext("org/unitils/database/TransactionManagerApplicationContext.xml")
    @Transactional(COMMIT)
    public static class ApplicationContextCommitTest {

        public void test() {
        }
    }

}
