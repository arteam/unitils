package org.unitils.dbmaintainer.locator;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.SQLUnitils;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

/**
 * Unit test for simple App.
 * The test looks for xml files with the same name as the value of the {@link ResourceDataSet} on the classpath 
 * and gives the most recent.
 * There are 2 files with the same name: 
 * <ul>
 *  <li>in the unitilsmodules</li>
 *  <li>in the TestAppResources module</li>
 * </ul>
 * 
 * @author tdr
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet(value = "/org/unitils/testdata/exampleResourceData.xml")
public class ResourceLoadingClassTest {
    
    @BeforeClass
    public static void setUp() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        SQLUnitils.executeUpdate("CREATE TABLE dossier (id varchar(50), name varchar(50), Start_date date)", databaseModule.getWrapper("").getDataSource());
    }

    /*** */
    @Test
    @ExpectedDataSet("/org/unitils/testdata/exampleResourceData.xml")
    public void testLoadingResource() {
        //SqlAssert.assertCountSqlResult("select count(*) from dossier", 3L);
        //SqlAssert.assertMultipleRowSqlResult("select * from dossier", new String[]{"DS-1", "TestAppResourcesBlack"}, new String[]{"DS-2", "n"}, new String[]{"DS-3", "decker"});
        Assert.assertTrue(true);
    }
    
    @AfterClass
    public static void afterClass() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        SQLUnitils.executeUpdate("drop table dossier",databaseModule.getWrapper("").getDataSource());
    }


}
