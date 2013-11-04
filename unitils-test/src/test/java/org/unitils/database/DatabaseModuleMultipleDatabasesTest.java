/*
 * Copyright (c) Smals
 */
package org.unitils.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.Unitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.reflectionassert.ReflectionAssert;


/**
 * TODO: Description of the class.
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
public class DatabaseModuleMultipleDatabasesTest {


    @TestedObject
    private DatabaseModule module;

    @Before
    public void setUp() throws FileNotFoundException, IOException {
    	
        module = new DatabaseModule();
        File file = new File("src/test/resources/org/unitils/database/config/testconfigMultipleDatabases.properties");
        Properties prop = Unitils.getInstance().getConfiguration();
        prop.load(new FileInputStream(file));
        module.init(prop);
    }
    @Test
    public void testGetdefaultDatabaseWrapper() {
        DataSourceWrapper actual = module.getDefaultDataSourceWrapper();
        ReflectionAssert.assertLenientEquals(getWrapper1(), actual);
    }

    @Test
    public void testGetDatabase1() throws SQLException {
        TestClassDatabase1 obj = new TestClassDatabase1();

        module.injectDataSource(obj);
        Assert.assertNotNull(obj.dataSource);
        Assert.assertEquals("jdbc:hsqldb:mem:unitils1", obj.dataSource.getConnection().getMetaData().getURL());

    }

    @Test
    public void testGetDatabase2() throws SQLException {
        TestClassDatabase2 obj = new TestClassDatabase2();

        module.injectDataSource(obj);
        Assert.assertNotNull(obj.dataSource);
        Assert.assertEquals("jdbc:h2:~/test", obj.dataSource.getConnection().getMetaData().getURL());

    }

    private DataSourceWrapper getWrapper1() {
        DatabaseConfiguration conf = new DatabaseConfiguration("database1", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils1", "sa", null, "public", Arrays.asList("public"), false, true);
        return new DataSourceWrapper(conf);
    }

    private DataSourceWrapper getWrapper2() {
        DatabaseConfiguration conf = new DatabaseConfiguration("database2", "h2", "org.h2.Driver", "jdbc:h2:~/test", "sa", null, "public", Arrays.asList("public"), false, false);
        return new DataSourceWrapper(conf);
    }

    private class TestClassDefaultDatabase {

        @TestDataSource
        private DataSource datasource;
    }

    private class TestClassDatabase1 {

        @TestDataSource("database1")
        private DataSource dataSource;
    }

    private class TestClassDatabase2 {

        @TestDataSource("database2")
        private DataSource dataSource;
    }

}
