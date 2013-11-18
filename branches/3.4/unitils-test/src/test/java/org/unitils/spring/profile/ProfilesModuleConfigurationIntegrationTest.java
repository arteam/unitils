package org.unitils.spring.profile;


import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.annotation.ConfigureProfile;


/**
 * ProfilesModuleIntegrationTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@ConfigureProfile(value = "dev", configuration = TypeConfiguration.CONFIGURATION, packageProfile = "org.unitils.spring.profile")
public class ProfilesModuleConfigurationIntegrationTest {
    
    @Autowired
    private DataSource dataSource;

    @Test
    public void test() {
        
        Assert.assertTrue(dataSource instanceof EmbeddedDatabase);
    }

}
