package org.unitils.dbunit;

import org.unitils.UnitilsJUnit3;
import org.unitils.db.DatabaseModule;
import org.unitils.inject.annotation.Inject;
import org.unitils.core.ModulesRepository;
import org.unitils.core.Unitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.EasyMockModule;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.dataset.IDataSet;
import org.easymock.EasyMock;

/**
 * todo fix: injection happens before setUp execution, should be afterwards -> causes tests to fail
 */
public class DbUnitModuleTest extends UnitilsJUnit3 {

    private DbUnitModule dbUnitModule;

    private Unitils unitils;

    @Mock
    private IDatabaseConnection mockDbUnitDatabaseConnection = null;

    @Mock @Inject(target = "unitils", property = "moduleRepository")
    private ModulesRepository mockModulesRepository = null;

    @Mock
    private DatabaseModule mockDatabaseModule = null;

    @Mock
    private DatabaseOperation mockInsertDatabaseOperation = null;

    @Mock
    private IDataSet mockDbUnitDataSet = null;

    protected void setUp() throws Exception {
        super.setUp();
        unitils = Unitils.getInstance();
        dbUnitModule = new DbUnitModule() {
            protected IDatabaseConnection createDbUnitConnection() {
                return mockDbUnitDatabaseConnection;
            }

            protected DatabaseOperation getInsertDatabaseOperation() {
                return mockInsertDatabaseOperation;
            }
        };

        EasyMock.expect(mockModulesRepository.getModule(DatabaseModule.class)).andStubReturn(mockDatabaseModule);
    }

    public void testIsDatabaseTest() throws Exception {
        assertTrue(dbUnitModule.isDatabaseTest(DbTest.class));
    }

    public void testInitDbUnitConnection() {
        dbUnitModule.initDbUnitConnection();
        assertSame(mockDbUnitDatabaseConnection, dbUnitModule.getDbUnitDatabaseConnection());
    }

    public void testInsertTestData() throws Exception {
        mockInsertDatabaseOperation.execute(mockDbUnitDatabaseConnection, mockDbUnitDataSet);
        EasyMockModule.replay();

        dbUnitModule.insertTestData();
    }

    @DatabaseTest
    public static class DbTest {

    }

}
