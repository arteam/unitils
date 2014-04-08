package org.unitils.database.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;


/**
 * DatabaseConfigurationsTest.
 * 
 * @author wiw
 * 
 * @since 3.3
 * 
 */
public class DatabaseConfigurationsTest {

    private DatabaseConfigurations databaseConfigurations;

    private DatabaseConfiguration config1, config2;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DatabaseConfiguration configDefault = new DatabaseConfiguration("DatabaseDefault", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils", "sa", "", "defaultSchemaName", Arrays.asList("schemaNames"), true, true);
        config1 = new DatabaseConfiguration("Database1", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils", "sa", "", "defaultSchemaName", Arrays.asList("schemaNames"), true, true);
        config2 = new DatabaseConfiguration("Database2", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils", "sa", "", "defaultSchemaName", Arrays.asList("schemaNames"), true, true);
        Map<String, DatabaseConfiguration> mapConfigs = new HashMap<String, DatabaseConfiguration>();
        mapConfigs.put("Database1", config1);
        mapConfigs.put("Database2", config2);
        databaseConfigurations = new DatabaseConfigurations(configDefault, mapConfigs);
    }

    @Test
    public void testGetDatabaseConfigurationDefault() {
        Assert.assertEquals("DatabaseDefault", databaseConfigurations.getDatabaseConfiguration().getDatabaseName());
    }

    @Test
    public void testGetDatabaseConfigurationWithDatabaseName() {
        Assert.assertEquals("DatabaseDefault", databaseConfigurations.getDatabaseConfiguration("").getDatabaseName());
        Assert.assertEquals("Database1", databaseConfigurations.getDatabaseConfiguration("Database1").getDatabaseName());
    }

    @Test(expected = UnitilsException.class)
    public void testGetDatabaseConfigurationWithDatabaseNameException() {
        databaseConfigurations.getDatabaseConfiguration("databaseException");  
    }

    @Test
    public void testGetDatabaseNames() {
        ReflectionAssert.assertReflectionEquals(Arrays.asList("Database1", "Database2"), databaseConfigurations.getDatabaseNames(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testGetDatabaseConfigurations() {
        ReflectionAssert.assertReflectionEquals(Arrays.asList(config1, config2), databaseConfigurations.getDatabaseConfigurations(), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testConfigurationToString() {
        String expected = "database name: 'Database1', driver class name: 'org.hsqldb.jdbcDriver', url: 'jdbc:hsqldb:mem:unitils', user name: 'sa', password: <not shown>, default schema name: 'defaultSchemaName', schema names: [schemaNames]";
        Assert.assertEquals(expected, config1.toString());
    }

}
