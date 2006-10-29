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
 */
@SuppressWarnings({"UnusedDeclaration"})
public class DatabaseModuleTest extends UnitilsJUnit3 {

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

    public void testIsDatabaseTest() {

        boolean result = databaseModule.isDatabaseTest(DbTest.class);
        assertTrue(result);
    }

    //todo refactor + add tests
    public void testInitDataSource() throws Exception {

        DbTest dbTest = new DbTest();
        databaseModule.initDatabase(dbTest);
        databaseModule.injectDataSource(dbTest);
        assertSame(mockDataSource, databaseModule.getDataSource());
        assertSame(mockDataSource, dbTest.getDataSourceFromMethod());
        assertSame(mockDataSource, dbTest.getDataSourceFromField());
    }


    @DatabaseTest
    public static class DbTest {

        private DataSource dataSourceFromMethod;

        @TestDataSource
        private DataSource dataSourceFromField;

        @TestDataSource
        public void afterCreateDataSource(javax.sql.DataSource dataSource) {
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
