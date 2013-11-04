package org.unitils.spring.profile;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.annotation.ConfigureProfile;
import org.unitils.spring.annotation.SpringApplicationContext;

/**
 * ProfilesModuleSpringApplicationContextIntegrationTest.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext("classpath:applicationContext-dao-test.xml")
@ConfigureProfile(value = "dev", configuration = TypeConfiguration.APPLICATIONCONTEXT)
public class ProfilesModuleSpringApplicationContextIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void test() {
        Assert.assertTrue(dataSource instanceof TransactionAwareDataSourceProxy);
    }

}
