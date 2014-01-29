package org.unitils.database.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.config.Configuration;
import org.unitils.reflectionassert.ReflectionAssert;


/**
 * DatabaseConfigurationsFactoryMultipleDatabaseTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */

public class DatabaseConfigurationsFactoryMultipleDatabaseTest {
private static final Log LOGGER = LogFactory.getLog(DatabaseConfigurationsFactoryMultipleDatabaseTest.class);
    
    private DatabaseConfigurationsFactory factory;
    private DatabaseConfiguration config1;
    private DatabaseConfiguration config2;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        
        Properties config = new Properties();
        
        File file = new File("src/test/resources/org/unitils/database/config/testconfigMultipleDatabases.properties");
        LOGGER.info(file.getAbsolutePath());
        config.load(new FileInputStream(file));
        factory = new DatabaseConfigurationsFactory(new Configuration(config));
        config1 = new DatabaseConfiguration("database1", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils1", "sa", null, "public", Arrays.asList("public"), false, true);
        config2 = new DatabaseConfiguration("database2", "h2", "org.h2.Driver", "jdbc:h2:~/test", "sa", null, "public", Arrays.asList("public"), false, false);
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
