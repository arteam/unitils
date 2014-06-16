/*
 * Copyright (c) Smals
 */
package org.unitils.dbunit;

import java.util.Properties;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.ModulesRepository;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.SQLUnitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;


/**
 * test {@link org.unitils.dbunit.DbUnitModule}.
 * 
 * @author wiw
 * 
 * @since 1.3.2
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet
public class ExpectedDataSetDifferentColumnsTest {
    
    @BeforeClass
    public static void beforeClass() {
        Properties prop = getCorrectProperties();
        ModulesRepository modulesRepository = Unitils.getInstance().getModulesRepository();
        DatabaseModule databaseModule = modulesRepository.getModuleOfType(DatabaseModule.class);
        databaseModule.init(prop);
        databaseModule.afterInit();
        DbUnitModule dbunitModule = modulesRepository.getModuleOfType(DbUnitModule.class);
        dbunitModule.init(prop);
        dbunitModule.afterInit();
        DataSource dataSource2 = databaseModule.getWrapper("").getDataSource();
        
        SQLUnitils.executeUpdate("CREATE TABLE fruit (id varchar(50), name varchar(50))", dataSource2);
    }
    
    @TestDataSource
    private DataSource dataSource;

    @Test
    @ExpectedDataSet("ExpectedDataSetDifferentColumnsTest-FirstContainsMoreAttributes.xml")
    public void testFirstContainsMoreAttributes() {
        Assert.assertTrue(true);
    }
    
    @Test
    @ExpectedDataSet("ExpectedDataSetDifferentColumnsTest-FirstContainsLessAttributes.xml")
    public void testFirstContainsLessAttributes() throws Exception {
        Assert.assertTrue(true);
    }
    
    @Test
    @ExpectedDataSet("ExpectedDataSetDifferentColumnsTest-FirstContainsSameAttributes.xml")
    public void testFirstContainsSameAttributes() throws Exception {
        Assert.assertTrue(true);
    }
    
    @Test
    @ExpectedDataSet("ExpectedDataSetDifferentColumnsTest-DifferentColumns.xml")
    public void testDifferentColumns() throws Exception {
        Assert.assertTrue(true);
    }
    
    @AfterClass
    public static void afterClass() {
        DataSource dataSource2 = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class).getWrapper("").getDataSource();
        SQLUnitils.executeUpdate("DROP TABLE fruit", dataSource2);
        
        Unitils.getInstance().initSingletonInstance();
    }
    
    private static Properties getCorrectProperties() {
        Unitils.initSingletonInstance();
        
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
