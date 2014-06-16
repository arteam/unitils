package org.unitils.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.config.Configuration;
import org.unitils.database.config.DatabaseConfigurationsFactory;
import org.unitils.database.transaction.impl.DefaultUnitilsTransactionManager;


/**
 * Test if the name of the key in the wrappers map is ok.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DatabaseModuleGetCorrectWrapper {
    
    private DatabaseModule sut;

    private DatabaseConfigurationsFactory databaseFactory;
    
    private static final String DEFAULT_DATABASENAME = "database1";
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = getCorrectProperties();
        
        sut = new DatabaseModule();
        sut.init(configuration);
        sut.afterInit();
        
        databaseFactory = new DatabaseConfigurationsFactory(new Configuration(configuration));
    }

    @Test
    public void testGetDefaultWrapperDoesNotExistYet() {
        sut.getWrapper("");
        Assert.assertEquals(1, sut.wrappers.size());
        Entry<String, DataSourceWrapper> entry = sut.wrappers.entrySet().iterator().next();
        Assert.assertEquals(DEFAULT_DATABASENAME, entry.getKey());
        Assert.assertNotNull(entry.getValue());
        
    }
    
    @Test
    public void testGetDefaultWrapperDoesAlreadyExist() throws Exception {
        Map<String, DataSourceWrapper> testWrappers = new HashMap<String, DataSourceWrapper>();
        DataSourceWrapper wrapper = new DataSourceWrapper(databaseFactory.create().getDatabaseConfiguration(), new DefaultUnitilsTransactionManager());
        testWrappers.put(DEFAULT_DATABASENAME, wrapper);
        sut.wrappers = testWrappers;
        
        
        DataSourceWrapper actual = sut.getWrapper("");
        Assert.assertSame(wrapper, actual);
    }
    
    @Test
    public void testGetDefaultWrapperDoesAlreadyExistNameIsNull() throws Exception {
        Map<String, DataSourceWrapper> testWrappers = new HashMap<String, DataSourceWrapper>();
        DataSourceWrapper wrapper = new DataSourceWrapper(databaseFactory.create().getDatabaseConfiguration(), new DefaultUnitilsTransactionManager());
        testWrappers.put(DEFAULT_DATABASENAME, wrapper);
        sut.wrappers = testWrappers;
        
        
        DataSourceWrapper actual = sut.getWrapper(null);
        Assert.assertSame(wrapper, actual);
    }
    
    
    @Test
    public void testGetDefaultWrapperDoesNotExistYetNameIsNull() {
        sut.getWrapper(null);
        Assert.assertEquals(1, sut.wrappers.size());
        Entry<String, DataSourceWrapper> entry = sut.wrappers.entrySet().iterator().next();
        Assert.assertEquals(DEFAULT_DATABASENAME, entry.getKey());
        Assert.assertNotNull(entry.getValue());
    }
    
    
    @Test
    public void testGetWrapperDoesAlreadyExist() throws Exception {
        String databaseName = "database2";
        Map<String, DataSourceWrapper> testWrappers = new HashMap<String, DataSourceWrapper>();
        DataSourceWrapper wrapper = new DataSourceWrapper(databaseFactory.create().getDatabaseConfiguration(databaseName), new DefaultUnitilsTransactionManager());
        testWrappers.put(databaseName, wrapper);
        sut.wrappers = testWrappers;
        
        
        DataSourceWrapper actual = sut.getWrapper(databaseName);
        Assert.assertSame(wrapper, actual);
    }
    
    @Test
    public void testGetWrapperDoesNotExistYet() {
        String databaseName = "database2";
        sut.getWrapper(databaseName);
        Assert.assertEquals(1, sut.wrappers.size());
        Entry<String, DataSourceWrapper> entry = sut.wrappers.entrySet().iterator().next();
        Assert.assertEquals(databaseName, entry.getKey());
        Assert.assertNotNull(entry.getValue());
        
    }
    
    @Test(expected = UnitilsException.class)
    public void testGetWrapperDatabaseNameDoesNotExistInConfiguration() throws Exception {
        String databaseName = "database132";
        sut.getWrapper(databaseName);

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
