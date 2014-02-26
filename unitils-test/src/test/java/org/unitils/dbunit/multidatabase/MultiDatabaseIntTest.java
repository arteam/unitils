package org.unitils.dbunit.multidatabase;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.database.DataSourceWrapper;
import org.unitils.database.DatabaseModule;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.SQLUnitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.sqlassert.SqlAssert;
import org.unitils.dbunit.DbUnitModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.DataSets;


/**
 * Test Multiple Databases with DBUnit.
 * 
 * @author wiw
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class MultiDatabaseIntTest {
    
    private static final Log LOGGER = LogFactory.getLog(MultiDatabaseIntTest.class);
    
    @BeforeClass
    public static void beforeClass() throws FileNotFoundException, IOException {
        Properties config = getCorrectProperties();
        
        //Unitils.getInstance().init(config);
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        databaseModule.init(config);
        databaseModule.afterInit();
        DbUnitModule dbunitModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DbUnitModule.class);
        dbunitModule.init(config);
        dbunitModule.afterInit();

    }
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    @DataSet(value = "MultiDatabaseIntTest.testOneDataSetDatabase1.xml",  databaseName="database1")
    public void testOneDataSetDatabase1() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 1L, "database1");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 1L, "database1");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 0L, "database1");
    }
    
    @Test
    @DataSet(value = "MultiDatabaseIntTest.testOneDataSetDatabase2.xml",  databaseName="database2")
    public void testOneDataSetDatabase2() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 1L, "database2");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 0L, "database2");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 1L, "database2");
    }
    
    @Test
    @DataSets(value= {@DataSet(value = "MultiDatabaseIntTest.testMultipleDataSetsDatabase1_1.xml", databaseName="database1"), @DataSet(value = "MultiDatabaseIntTest.testMultipleDataSetsDatabase1_2.xml", databaseName="database1")})
    public void testMultipleDataSetsDatabase1() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2L, "database1");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Willemijn'", 1L, "database1");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Myrthe'", 0L, "database1");
        SqlAssert.assertCountSqlResult("select count(*) from person where personname='Maurits'", 1L, "database1");
    }
    
    private static Properties getCorrectProperties() {
        Properties config = (Properties) Unitils.getInstance().getConfiguration().clone();
        config.setProperty("database.names", "database1, database2");
        config.setProperty("database.userName", "sa");
        config.setProperty("database.password", "");
        config.setProperty("database.schemaNames", "public");
        config.setProperty("database.driverClassName.database1", "org.hsqldb.jdbcDriver"); 
        config.setProperty("database.driverClassName.database2", "org.h2.Driver");
        config.setProperty("database.url.database1", "jdbc:hsqldb:mem:unitils1");
        config.setProperty("database.url.database2", "jdbc:h2:~/test");
        config.setProperty("database.dialect.database1", "hsqldb");
        config.setProperty("database.dialect.database2", "h2");
        config.setProperty("database.dbMaintain.enabled", "true");
        config.setProperty("dbMaintainer.autoCreateExecutedScriptsTable", "true");
        config.setProperty("dbMaintainer.autoCreateDbMaintainScriptsTable", "false");
        config.setProperty("updateDataBaseSchema.enabled", "true");
        
        config.setProperty("dbMaintainer.updateSequences.enabled", "true");
        config.setProperty("dbMaintainer.keepRetryingAfterError.enabled","true");
        config.setProperty("org.unitils.dbmaintainer.script.ScriptSource.implClassName", "org.unitils.dbmaintainer.script.impl.DefaultScriptSource");
        config.setProperty("unitils.module.hibernate.enabled", "false");
        config.setProperty("unitils.module.jpa.enabled", "false");
        config.setProperty("unitils.module.spring.enabled", "false");
        config.setProperty("dbMaintainer.script.locations", "src/test/resources/dbscripts");
        config.setProperty("dbMaintainer.fromScratch.enabled", "false");
        return config;
    }

}
