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
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.core.Unitils;
import org.unitils.database.transaction.SpringIntegratingTransactionManager;
import org.unitils.database.transaction.TransactionManager;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.spring.SpringModule;
import org.unitils.spring.annotation.SpringApplicationContext;

import java.sql.Connection;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseModuleAutoDetectTransactionManagerTest extends DatabaseModuleTransactionalTest {

    private SpringConfiguredTest springConfiguredTest;

    private SimpleTransactionManagerTest simpleTransactionManagerTest;

    protected void setUp() throws Exception {
        super.setUp();

        databaseModule = new DatabaseModule() {
            protected TransactionManager createTransactionManager() {
                return new SpringIntegratingTransactionManager();
            }
        };
        databaseModule.init(configuration);

        springConfiguredTest = new SpringConfiguredTest();
        simpleTransactionManagerTest = new SimpleTransactionManagerTest();
    }


    public void testTransactions_springConfigured() throws Exception {
        expect(mockConnection1.getAutoCommit()).andStubReturn(false);
        expect(mockConnection1.isReadOnly()).andStubReturn(false);
        mockConnection1.rollback();
        mockConnection1.close();
        replay();

        mockDataSource = (MockDataSource) getSpringModule().getApplicationContext(springConfiguredTest).getBean("dataSource");
        databaseModule.initTransactionManager();
        databaseModule.startTransaction(springConfiguredTest);
        Connection conn1 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        Connection conn2 = DataSourceUtils.getConnection(databaseModule.getDataSource());
        DataSourceUtils.releaseConnection(conn1, databaseModule.getDataSource());
        assertSame(conn1, conn2);
        databaseModule.commitOrRollbackTransaction(springConfiguredTest, SpringConfiguredTest.class.getMethod("test"));
    }

    private SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

    @SpringApplicationContext("org/unitils/database/TransactionManagerApplicationContext.xml")
    public static class SpringConfiguredTest {

        public void test() {}
    }

    public static class SimpleTransactionManagerTest {

        public void test() {}
    }
}
