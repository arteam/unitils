/*
 * Copyright (c) Smals
 */
package org.unitils.core.junit;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.Statement;
import org.unitils.core.TestListener;


/**
 * test {@link AfterTestMethodStatement}.
 * 
 * @author wiw
 * 
 * @since 3.4.1
 * 
 */
public class AfterTestMethodStatementTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.AfterTestMethodStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateTestListenerIsNull() throws Throwable {
        new AfterTestMethodStatement(null, new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                // TODO Auto-generated method stub
                
            }
        }, TestClass1.class.getMethod("test1"), new TestClass1()).evaluate();
    }
    
    @Test
    public void testEvaluateStatementThrowsException() throws Throwable {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("This is a test exception");
        new AfterTestMethodStatement(new TestListener() {}, new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                throw new Exception("This is a test exception");
                
            }
        }, TestClass2.class.getMethod("test1"), new TestClass2()).evaluate();
        
        
    }
    
    private class TestClass1 {
        
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
    
    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }

}
