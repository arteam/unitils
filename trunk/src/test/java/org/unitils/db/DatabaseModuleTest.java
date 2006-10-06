package org.unitils.db;

import org.easymock.EasyMock;
import org.unitils.UnitilsJUnit3;
import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbunit.DatabaseTest;
import org.unitils.easymock.EasyMockModule;
import org.unitils.easymock.annotation.Mock;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Filip Neven
 */
public class DatabaseModuleTest extends UnitilsJUnit3 {

    private DatabaseModule databaseModule;

    @Mock
    private DataSourceFactory mockDataSourceFactory = null;

    @Mock
    private DataSource mockDataSource = null;

    @Mock
    private Connection mockConnection = null;

    @Mock
    private DBMaintainer mockDbMaintainer = null;

    public void setUp() throws Exception {
        super.setUp();

        databaseModule = new DatabaseModule() {

            protected DataSourceFactory getDataSourceFactory() {
                return mockDataSourceFactory;
            }

            protected DBMaintainer createDbMaintainer() {
                return mockDbMaintainer;
            }
        };

        EasyMock.expect(mockDataSource.getConnection()).andStubReturn(mockConnection);
    }

    public void testIsDatabaseTest() {
        assertTrue(databaseModule.isDatabaseTest(DbTest.class));
    }

    public void testInitDataSource() throws Exception {
        EasyMock.expect(mockDataSourceFactory.createDataSource()).andReturn(mockDataSource);
        mockDbMaintainer.updateDatabase();
        EasyMockModule.replay();

        databaseModule.initDatabase(new DbTest());
        assertSame(mockDataSource, databaseModule.getDataSource());
    }

    public void testGetConnection() {
        EasyMock.expect(mockDataSourceFactory.createDataSource()).andReturn(mockDataSource);
        EasyMockModule.replay();

        Connection conn = databaseModule.getCurrentConnection();
        assertSame(mockConnection, conn);
    }

    @DatabaseTest
    public static class DbTest {

    }

}
