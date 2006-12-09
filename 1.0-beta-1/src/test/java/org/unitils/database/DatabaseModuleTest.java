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

import org.apache.commons.configuration.Configuration;
import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import org.unitils.database.annotations.DatabaseTest;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.easymock.annotation.Mock;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Tests for the DatabaseModule
 */
@SuppressWarnings({"UnusedDeclaration"})
public class DatabaseModuleTest extends UnitilsJUnit3 {

    /**
     * Tested object
     */
    private DatabaseModule databaseModule;

    @Mock
    private DataSource mockDataSource = null;

    @Mock
    private Connection mockConnection = null;

    @Mock
    private DBMaintainer mockDbMaintainer = null;

    public void setUp() throws Exception {
        super.setUp();

        databaseModule = new DatabaseModule() {

            @Override
            protected DBMaintainer createDbMaintainer(Configuration configuration) {
                return mockDbMaintainer;
            }

            @Override
            protected DataSource createDataSource() {
                return mockDataSource;
            }
        };

        databaseModule.registerDatabaseTestAnnotation(DatabaseTest.class);
        expect(mockDataSource.getConnection()).andStubReturn(mockConnection);
    }

    /**
     * Tests if isDatabaseTest returns true for a class annotated with DatabaseTest
     */
    public void testIsDatabaseTest() {

        boolean result = databaseModule.isDatabaseTest(DbTest.class);
        assertTrue(result);
    }

    /**
     * Test the injection of the dataSource into a test object
     *
     * @throws Exception
     */
    public void testInjectDataSource() throws Exception {

        DbTest dbTest = new DbTest();
        databaseModule.injectDataSource(dbTest);
        assertSame(mockDataSource, databaseModule.getDataSource());
        assertSame(mockDataSource, dbTest.getDataSourceFromMethod());
        assertSame(mockDataSource, dbTest.getDataSourceFromField());
    }

    /**
     * Object that plays the role of database test object in this class's tests.
     */
    @DatabaseTest
    public static class DbTest {

        /**
         * DataSource that can be injected by calling the method setDataSource
         */
        private DataSource dataSourceFromMethod;

        /**
         * Field on which a DataSource should be injected
         */
        @TestDataSource
        private DataSource dataSourceFromField;

        /**
         * Method on which a DataSource should be injected
         *
         * @param dataSource not null
         */
        @TestDataSource
        public void setDataSource(javax.sql.DataSource dataSource) {
            this.dataSourceFromMethod = dataSource;
        }

        public DataSource getDataSourceFromMethod() {
            return dataSourceFromMethod;
        }

        public javax.sql.DataSource getDataSourceFromField() {
            return dataSourceFromField;
        }

    }

}
