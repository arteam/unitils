package org.unitils.spring.profile;

import static org.unitils.database.SQLUnitils.executeUpdate;

import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModule;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.spring.annotation.ConfigureProfile;
import org.unitils.spring.annotation.SpringApplicationContext;


/**
 * ProfileModuleTest.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProfileModuleTest {

    @TestedObject
    private ProfileModule profileModule;
    
    @Mock
    private AnnotationConfigApplicationContext annoCtx;
    @Mock
    private GenericXmlApplicationContext genCtx;
    
    @Mock
    private StandardEnvironment env;
    
    private static final String PROFILE = "dev";
    private static final String PACKAGEPROFILE = "org.unitils.spring.profile";
    

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        profileModule = new ProfileModule();
        Properties config = new ConfigurationLoader().loadConfiguration();
        profileModule.init(config);
        profileModule.afterInit();
        
    }

    @Test(expected = UnitilsException.class)
    public void testCheckIfEverythingIsInPlaceSpringApplicationContext() {
        Assert.assertTrue("Testclass3", profileModule.checkIfEverythingIsInPlace(TestClass3.class));
        
        Assert.assertFalse("Testclass1 - No ConfigureProfile annotation", profileModule.checkIfEverythingIsInPlace(TestClass1.class)); 
        //No SpringApplicationContext
        profileModule.checkIfEverythingIsInPlace(TestClass2.class);
    }
    
    @Test(expected = UnitilsException.class)
    public void testGetProfileProfileEmpty() {
        profileModule.checkIfEverythingIsInPlace(TestClass4.class);
    }
    
    @Test(expected = UnitilsException.class)
    public void CheckIfEverythingIsInPlaceConfigurationNoPackageProfile() {
        profileModule.checkIfEverythingIsInPlace(TestClass5.class);
    }
    
    @Test
    public void CheckIfEverythingIsInPlaceConfiguration() {
        profileModule.checkIfEverythingIsInPlace(TestClass6.class);
    }
    
    
    @Test
    public void testGetProfile() {
        ConfigureProfile configProfile = TestClass3.class.getAnnotation(ConfigureProfile.class);
        EasyMock.expect(genCtx.getEnvironment()).andReturn(env);
        env.setActiveProfiles(PROFILE);
        genCtx.load(new String[]{"classpath:applicationContext-dao-test.xml"});
        genCtx.refresh();
        
        EasyMockUnitils.replay();
        profileModule.setConfigurationAsTypeSpringApplicationContext(genCtx, configProfile, new String[]{"classpath:applicationContext-dao-test.xml"});
    }
    
    @Test
    public void testGetProfileNotEverythingInPlace() {
        
        EasyMockUnitils.replay();
        profileModule.getProfile(TestClass1.class);
    }
    
    @Test
    public void testGetProfileConfiguration() {
        profileModule.setCtx(annoCtx);
        ConfigureProfile configProfile = TestClass6.class.getAnnotation(ConfigureProfile.class);
        EasyMock.expect(annoCtx.getEnvironment()).andReturn(env);
        env.setActiveProfiles(new String[]{"acc"});
        annoCtx.scan(PACKAGEPROFILE);
        annoCtx.refresh();
        
        EasyMockUnitils.replay();
        profileModule.setConfigurationAsTypeConfiguration(annoCtx, configProfile);
    }
    
    
    
    @Test
    public void testCloseProfile() {
        profileModule.setCtx(annoCtx);
        annoCtx.close();
        EasyMockUnitils.replay();
        profileModule.closeContext();
    }
    
    @Test
    public void testInjectBeansBeansException() throws BeansException, IllegalArgumentException, IllegalAccessException {
        profileModule.setCtx(annoCtx);
        EasyMock.expect(annoCtx.getBean("testClzz")).andThrow(new BeansException("test") {

            /***/
            private static final long serialVersionUID = 2707287891488100116L;
        });
        EasyMockUnitils.replay();
        Assert.assertFalse(profileModule.injectBeans(new TestClass6()));
    }
    
    @Test
    public void testInjectBeansIllegalArgumentException() {
        profileModule.setCtx(annoCtx);
        EasyMock.expect(annoCtx.getBean("testClzz")).andReturn(new TestClass1());
        EasyMockUnitils.replay();
        Assert.assertFalse(profileModule.injectBeans(new TestClass6()));
    }
    
    @Test
    public void testInjectBeansOk() {
        profileModule.setCtx(annoCtx);
        EasyMock.expect(annoCtx.getBean("testClzz")).andReturn(new TestClass4());
        EasyMockUnitils.replay();
        TestClass6 testObject = new TestClass6();
        profileModule.injectBeans(testObject);
        
        Assert.assertTrue(testObject.getTestClzz().getClass().equals(TestClass4.class));
    }
    
    @Test
    public void testGetProfileSpringApplicationContext() {
        profileModule.getProfile(TestClass3.class);
        ReflectionAssert.assertLenientEquals("Test if the actual context is a GenericXmlApplicationContext", GenericXmlApplicationContext.class, profileModule.getCtx().getClass());
        
    }
    
    @Test
    public void testGetProfileWithAnnotations() {
        profileModule.getProfile(TestClass6.class);
        ReflectionAssert.assertLenientEquals("Test if the actual context is a GenericXmlApplicationContext", AnnotationConfigApplicationContext.class, profileModule.getCtx().getClass());
    
    
        //drop table DummyTable
        dropTableDummyTable();
    }
    
    
    private class TestClass1 {
        //just a test class
    }
    
    @ConfigureProfile("dev")
    private class TestClass2 {
        //just a test class
    }
    
    @ConfigureProfile("dev")
    @SpringApplicationContext("classpath:org/unitils/spring/profile/applicationContext-dao-test.xml")
    private class TestClass3 {
        //just a test class
    }
    
    @ConfigureProfile("")
    @SpringApplicationContext("classpath:org/unitils/spring/profile/applicationContext-dao-test.xml")
    private class TestClass4 {
        //just a test class
    }
    
    @ConfigureProfile(value = PROFILE, configuration = TypeConfiguration.CONFIGURATION)
    private class TestClass5 {
        //just a test class
    }
    
    @ConfigureProfile(value = "acc", configuration = TypeConfiguration.CONFIGURATION, packageProfile = PACKAGEPROFILE)
    private class TestClass6 {
        //just a test class
        
        @Autowired
        private TestClass4 testClzz;
        
        
        /**
         * @return the testClzz
         */
        public TestClass4 getTestClzz() {
            return testClzz;
        }
    }
    
    private void dropTableDummyTable() {
        EmbeddedDatabase dataSource = new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.HSQL)
        .build();
        
        executeUpdate("drop table DUMMYTABLE", dataSource);
    }
   

}
