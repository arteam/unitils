package org.unitils.db;

import org.apache.commons.configuration.Configuration;
import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbunit.DatabaseTest;
import static org.unitils.easymock.EasyMockModule.replay;
import org.unitils.easymock.annotation.Mock;

import java.sql.Connection;

/**
 */
public class DatabaseModuleTest extends UnitilsJUnit3 {

    private DatabaseModule databaseModule;

    @Mock
    private javax.sql.DataSource mockDataSource = null;

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
            protected javax.sql.DataSource createDataSource() {
                return mockDataSource;
            }
        };

        expect(mockDataSource.getConnection()).andStubReturn(mockConnection);
    }

    public void testIsDatabaseTest() {

        replay();

        boolean result = databaseModule.isDatabaseTest(DbTest.class);
        assertTrue(result);
    }

    //todo refactor + add tests
    public void testInitDataSource() throws Exception {

        replay();

        DbTest dbTest = new DbTest();
        databaseModule.initDatabase(dbTest);
        databaseModule.createTestListener().beforeTestMethod(dbTest, null);
        assertSame(mockDataSource, databaseModule.getDataSource());
        assertSame(mockDataSource, dbTest.getDataSourceFromMethod());
        assertSame(mockDataSource, dbTest.getDataSourceFromField());
    }

    @DatabaseTest
    public static class DbTest {

        private javax.sql.DataSource dataSourceFromMethod;

        @TestDataSource
        private javax.sql.DataSource dataSourceFromField;

        @TestDataSource
        public void afterCreateDataSource(javax.sql.DataSource dataSource) {
            this.dataSourceFromMethod = dataSource;
        }

        public javax.sql.DataSource getDataSourceFromMethod() {
            return dataSourceFromMethod;
        }

        public javax.sql.DataSource getDataSourceFromField() {
            return dataSourceFromField;
        }

    }

}
