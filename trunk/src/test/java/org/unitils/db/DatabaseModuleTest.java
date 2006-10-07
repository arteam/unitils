package org.unitils.db;

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
            protected DBMaintainer createDbMaintainer() {
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
        assertTrue(databaseModule.isDatabaseTest(DbTest.class));
    }

    public void testInitDataSource() throws Exception {
        mockDbMaintainer.updateDatabase();
        replay();

        DbTest dbTest = new DbTest();
        databaseModule.initDatabase(dbTest);
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
