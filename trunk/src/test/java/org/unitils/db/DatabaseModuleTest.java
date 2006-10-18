package org.unitils.db;

import org.apache.commons.configuration.Configuration;
import static org.easymock.EasyMock.expect;
import org.unitils.UnitilsJUnit3;
import org.unitils.db.annotations.AfterCreateConnection;
import org.unitils.db.annotations.AfterCreateDataSource;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbunit.DatabaseTest;
import static org.unitils.easymock.EasyMockModule.replay;
import org.unitils.easymock.annotation.Mock;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 */
public class DatabaseModuleTest extends UnitilsJUnit3 {

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
        assertSame(mockDataSource, dbTest.getDataSource());
        assertSame(mockConnection, dbTest.getConnection());
    }

    @DatabaseTest
    public static class DbTest {

        private DataSource dataSource;

        private Connection connection;

        @AfterCreateDataSource
        public void afterCreateDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @AfterCreateConnection
        public void afterCreateConnection(Connection conn) {
            this.connection = conn;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public Connection getConnection() {
            return connection;
        }
    }

}
