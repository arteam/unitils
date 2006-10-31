package org.unitils.db;

import org.apache.commons.configuration.Configuration;
import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.easymock.annotation.LenientMock;

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

    @LenientMock
    private DataSource mockDataSource = null;

    @LenientMock
    private Connection mockConnection = null;

    @LenientMock
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
     * @throws Exception
     */
    public void testInjectDataSource() throws Exception {

        DbTest dbTest = new DbTest();
        databaseModule.initDatabase(dbTest);
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
         * @param dataSource
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
