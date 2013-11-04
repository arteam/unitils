package org.unitils.database.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.config.Configuration;
import org.unitils.reflectionassert.ReflectionAssert;


/**
 * DatabaseConfigurationsFactoryTest.
 * 
 * @author wiw
 * 
 * @since 3.3
 * 
 */
public class DatabaseConfigurationsFactoryTest {

    private DatabaseConfigurationsFactory factory;
    private DatabaseConfiguration config1;
    private DatabaseConfiguration config2;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        Properties config = new Properties();
        
        File file = new File("src\\test\\resources\\org\\unitils\\database\\config\\testconfig.properties");
        config.load(new FileInputStream(file));
        factory = new DatabaseConfigurationsFactory(new Configuration(config));
        config1 = new DatabaseConfiguration("database1", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils1", "sa", null, "public", Arrays.asList("public"), false, true);
        config2 = new DatabaseConfiguration("database2", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils2", "sa", null, "public", Arrays.asList("public"), false, false);
    }

    @Test
    public void testCreateDatabaseConfiguration() {
        ReflectionAssert.assertLenientEquals(config1, factory.createDatabaseConfiguration("database1", true));
    }
    
    @Test
    public void testCreate() {
        Map<String, DatabaseConfiguration> databaseConfigurations = new HashMap<String, DatabaseConfiguration>();
        databaseConfigurations.put("database1", config1);
        databaseConfigurations.put("database2", config2);
        DatabaseConfigurations configs = new DatabaseConfigurations(null, databaseConfigurations);
        ReflectionAssert.assertLenientEquals(configs, factory.create());
    }

}
