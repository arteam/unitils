package org.unitils.spring.profile;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.unitils.UnitilsJUnit4TestClassRunner;


/**
 * ProfileModuleInjectBeans.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ProfileModuleInjectBeans {
    
    private ProfileModule module;
    
    @Before
    public void init() {
        this.module = new ProfileModule();
    }

    @Test
    public void testClassNoFields() {
        TestClassNoFields obj = new TestClassNoFields();
        Assert.assertTrue(module.injectBeans(obj));
    }
    
    @Test
    public void testAutowiredNotAccessible() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        module.setCtx(getAppContext());
        
        TestclassWithAutowired obj = new TestclassWithAutowired();
        Assert.assertFalse(module.injectBeans(obj));
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
}
