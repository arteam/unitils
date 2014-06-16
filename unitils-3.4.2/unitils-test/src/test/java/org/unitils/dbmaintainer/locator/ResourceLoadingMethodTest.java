package org.unitils.dbmaintainer.locator;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.SQLUnitils;
import org.unitils.dbunit.DbUnitModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.util.FileHandler;

/**
 * Unit test for simple App.
 * 
 * @author tdr
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ResourceLoadingMethodTest {

    private static final Logger LOGGER = Logger.getLogger(ResourceLoadingMethodTest.class);

    
    @BeforeClass
    public static void setUp() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        SQLUnitils.executeUpdate("CREATE TABLE dossier (id varchar(50), name varchar(50), Start_date date)", databaseModule.getWrapper("").getDataSource());
    }
    
    
    /** */
    @DataSet("/org/unitils/testdata/exampleResourceData.xml")
    @Test
    public void testLoadingResource() {
        Assert.assertTrue(true);
    }

    /** */
    @Ignore
    @DataSet("../testdata/exampleResourceData.xml")
    @Test
    public void testLoadingDataset() {
        Assert.assertTrue(true);
    }
    
    @Test
    public void testTest() {
        String name = "../testdata/exampleResourceData.xml";
        FileHandler fileHandler = new FileHandler();
        fileHandler.createTempFile(name);
    }

    /** */
    @DataSet
    @Test
    @Ignore
    @ExpectedDataSet
    public void testLoadingResourceDatasetDefault() {
        LOGGER.debug("STVE :" + DbUnitModule.class.getPackage().toString());
        Assert.assertTrue(true);
    }

    /** */
    @DataSet
    @Test
    public void testLoadingDatasetDefault() {

        Assert.assertTrue(true);
    }

    /** */
    @DataSet("/org/unitils/testdata/exampleResourceData.xml")
    public void testLoadingResourceDataFile() {
        Assert.assertTrue(true);
    }

    /** */
    @Test
    @DataSet({
        "/org/unitils/testdata/exampleResourceData.xml", "/org/unitils/testdata/exampleResourceData.xml"
    })
    public void testLoadingResourceMultipleDataFiles() {
        Assert.assertTrue(true);
    }

    /** */
    @Test
    @Ignore
    @DataSet({
        "/org/unitils/testdata/exampleResourceData.xml", "/org/unitils/testdata/exampleResourceData.xml"
    })
    @ExpectedDataSet({
        "/org/unitils/testdata/exampleResourceData.xml", "/org/unitils/testdata/exampleResourceData.xml"
    })
    public void testLoadingExpectedResourceMultipleDataFiles() {
        Assert.assertTrue(true);
    }
    
    /** */
    @DataSet("/org/unitils/testdata/exampleResourceData.xml/")
    @Test
    public void testLoadingResourceWithSlashAtTheEnd() {
        Assert.assertTrue(true);
    }
    
    
    @AfterClass
    public static void afterClass() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        SQLUnitils.executeUpdate("drop table dossier",databaseModule.getWrapper("").getDataSource());
    }
}
