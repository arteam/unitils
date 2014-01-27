package org.unitils.spring.profile;

import static org.unitils.database.SQLUnitils.executeUpdate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.unitils.UnitilsJUnit4TestClassRunner;


/**
 * ProfileModuleInjectBeans.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProfileModuleInjectBeans {
    
    private ProfileModule module;
    
    @BeforeClass
    public static void initClass() {
    	createTestTables();
    }
    
    @Before
    public void init() {
    	module = new ProfileModule();
       // createTestTables();
    }
    
    @After
    public void tearDown() {
        //dropTestTables();
    }

    @Test
    public void testClassNoFields() {
        TestClassNoFields obj = new TestClassNoFields();
        Assert.assertTrue(module.injectBeans(obj));
        
        dropTestTables();
    }
    
    @Test
    public void testAutowiredNotAccessible() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        module.setCtx(getAppContext());
        
        TestclassWithAutowired obj = new TestclassWithAutowired();
        Assert.assertFalse(module.injectBeans(obj));
        
        dropTestTables();
    }

    private class TestClassNoFields {
        //no fields available
    }
    private class TestclassWithAutowired {
        
        @Autowired
        private TestClassNoFields field1;
    }
    
    private AnnotationConfigApplicationContext getAppContext() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().setActiveProfiles("dev");
        ctx.scan("org.unitils.spring.profile"); // register all @Configuration classes
        ctx.refresh();
        return ctx;
    }
    
    private void dropTestTables() {
        EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
        .build();
        executeUpdate("drop table DOSSIER", dataSource);
    }
    
    private static void createTestTables() {
    	EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
        .build();
    	executeUpdate("CREATE TABLE dossier (id varchar(50), name varchar(50), Start_date date)", dataSource);
    }
}
