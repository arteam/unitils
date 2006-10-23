package org.unitils.dbunit;

import static org.easymock.EasyMock.expect;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.easymock.EasyMock;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ModulesRepository;
import org.unitils.db.DatabaseModule;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.easymock.annotation.LenientMock;

/**
 * todo fix: injection happens before setUp execution, should be afterwards -> causes tests to fail
 */
@SuppressWarnings({"UnusedDeclaration"})
public class DbUnitModuleTest extends UnitilsJUnit3 {

    private DbUnitModule dbUnitModule;


    @LenientMock
    private IDatabaseConnection mockDbUnitDatabaseConnection;

    @LenientMock
    private ModulesRepository mockModulesRepository;

    @LenientMock
    private DatabaseModule mockDatabaseModule;

    @LenientMock
    private DatabaseOperation mockInsertDatabaseOperation;

    @LenientMock
    private IDataSet mockDbUnitDataSet;


    protected void setUp() throws Exception {
        super.setUp();

        dbUnitModule = new DbUnitModule() {
            protected IDatabaseConnection createDbUnitConnection() {
                return mockDbUnitDatabaseConnection;
            }

            protected DatabaseOperation getInsertDatabaseOperation() {
                return mockInsertDatabaseOperation;
            }
        };

        expect(mockModulesRepository.getFirstModule(DatabaseModule.class)).andStubReturn(mockDatabaseModule);
    }


    public void testIsDatabaseTest() throws Exception {

        assertTrue(dbUnitModule.isDatabaseTest(new DbTest()));
    }


    public void testInitDbUnitConnection() {
        dbUnitModule.initDbUnitConnection();
        assertSame(mockDbUnitDatabaseConnection, dbUnitModule.getDbUnitDatabaseConnection());
    }


    //Todo implement
    public void testInsertTestData() throws Exception {

        dbUnitModule.insertTestData(DbTest.class, DbTest.class.getDeclaredMethod("testMethod"));
    }


    @DatabaseTest
    public static class DbTest {

        public void testMethod() {
        }
    }

}
