package org.unitils.dbunit;

import java.util.Properties;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DefaultMetadataHandler;
import org.dbunit.database.IMetadataHandler;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;


/**
 * Test if the {@link DbUnitModule} picks the correct {@link IMetadataHandler}.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class DbUnitModuleTestDefaultDatabaseMetaHandler {

    private DbUnitModule sut;
    
    private String schema;
    
    private Properties configuration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        sut = new DbUnitModule();
        configuration = new ConfigurationLoader().loadConfiguration();
        sut.init(configuration);
        schema = "public";
        
    }

    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#createDbUnitConnection(java.lang.String)}.
     */
    @Test
    public void testCreateDefaultDbUnitConnection() {
        DbUnitDatabaseConnection connection = sut.createDbUnitConnection(schema);
        DatabaseConfig databaseConfig = connection.getConfig();
        Object metaHandler = databaseConfig.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);
        
        Assert.assertNotNull(metaHandler);
        Assert.assertThat(metaHandler, CoreMatchers.instanceOf(DefaultMetadataHandler.class));
    }
    
    /**
     * Test method for {@link org.unitils.dbunit.DbUnitModule#createDbUnitConnection(java.lang.String)}.
     */
    @Test
    public void testCreateDbUnitConnection() {
        Properties tempConfig = (Properties) configuration.clone();
        tempConfig.setProperty("org.dbunit.database.IMetadataHandler.implClassName", "org.dbunit.ext.mysql.MySqlMetadataHandler");
        sut.init(tempConfig);
        
        DbUnitDatabaseConnection connection = sut.createDbUnitConnection(schema);
        DatabaseConfig databaseConfig = connection.getConfig();
        Object metaHandler = databaseConfig.getProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER);
        
        Assert.assertNotNull(metaHandler);
        Assert.assertThat(metaHandler, CoreMatchers.instanceOf(MySqlMetadataHandler.class));
    }

    @Test
    public void testGetCorrectDatabaseMetaHandler() {
        Properties prop = new Properties();
        prop.setProperty("org.dbunit.database.IMetadataHandler.implClassName", "org.dbunit.database.DefaultMetadataHandler");
        sut.init(prop);
        
        IMetadataHandler metaHandler1 = sut.getDefaultDatabaseMetaHandler();
        Assert.assertNotNull(metaHandler1);
        Assert.assertThat(metaHandler1, CoreMatchers.instanceOf(DefaultMetadataHandler.class));
        
        prop = new Properties();
        prop.setProperty("org.dbunit.database.IMetadataHandler.implClassName", "org.dbunit.ext.mysql.MySqlMetadataHandler");
        sut.init(prop);
        
        metaHandler1 = sut.getDefaultDatabaseMetaHandler();
        
        Assert.assertNotNull(metaHandler1);
        Assert.assertThat(metaHandler1, CoreMatchers.instanceOf(MySqlMetadataHandler.class));
    }
    
}
