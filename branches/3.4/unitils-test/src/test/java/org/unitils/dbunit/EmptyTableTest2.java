package org.unitils.dbunit;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.sqlassert.SqlAssert;
import org.unitils.dbunit.annotation.DataSet;


/**
 * EmptyTableTest.
 * 
 * @author Willemijn Wouters
 * 
 * @since
 * 
 */

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class EmptyTableTest2 {
    
    @BeforeClass
    public static void beforeClass() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        Properties config = getCorrectProperties();
        databaseModule.init(config );
        databaseModule.afterInit();
        DbUnitModule dbunitModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DbUnitModule.class);
        dbunitModule.init(config);
        dbunitModule.afterInit();
    }

    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsMoreAttributes.xml")
    public void testFirstContainsMoreAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2l);
        SqlAssert.assertMultipleRowSqlResult("select * from person", new String[]{"2", "Myrthe"}, new String[]{"1", null});
    }
    
    @Test
    @DataSet(value="DbunitDifferentColumnsTest-FirstContainsLessAttributes.xml", databaseName="database1")
    public void testFirstContainsLessAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2l);
        SqlAssert.assertSingleRowSqlResult("select personname from person where personid='1'", new String[]{"Willemijn"});
    }
    
    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsSameAttributes.xml")
    public void testFirstContainsSameAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2l);
        SqlAssert.assertMultipleRowSqlResult("select * from person", new String[]{"3", "Maurits"}, new String[]{"4", "Franciscus"});
    }
    
    @Test
    @DataSet("DbunitDifferentColumnsTest-FirstContainsNoAttributes.xml")
    public void testFirstContainsNoAttributes() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 0L);
    }
    
    @Test
    @DataSet("DbunitDifferentColumnsTest-DifferentColumns.xml")
    public void testDifferentColumns() throws Exception {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2L);
        SqlAssert.assertSingleRowSqlResult("select * from person where personID='9'", new String[]{"9"});
        //SqlAssert.assertMultipleRowSqlResult("select personid, personname from person", new String[0]);
        
        SqlAssert.assertSingleRowSqlResult("select personid, personname from person where personName='Danielle'", new String[]{"Danielle"});
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
