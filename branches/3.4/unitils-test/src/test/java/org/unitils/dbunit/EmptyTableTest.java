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
public class EmptyTableTest {
    
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
    @DataSet(value="EmptyTableTest.xml", databaseName="database1")
    public void testEmptyTable() {
        SqlAssert.assertCountSqlResult("select count(*) from person", 2L);
        SqlAssert.assertSingleRowSqlResult("select * from person where personID='6'", new String[]{"6", "Willemijn"});
        SqlAssert.assertSingleRowSqlResult("select * from person where personID='8'", new String[]{"8", "Myrthe"});
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
