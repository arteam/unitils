/*
 * Copyright (c) Smals
 */
package org.unitils.core.junit;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.TestListener;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;


/**
 * TODO: Description of the class.
 * 
 * @author wiw
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class BeforeTestClassStatementTest {
    
    @Mock
    private TestListener listener;

    @Mock
    private Statement statement;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.BeforeTestClassStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnTestListener() throws Throwable {
        Class<?> clazz = TestClass2.class;
        
        listener.beforeTestClass(clazz);
        EasyMock.expectLastCall().andThrow(new NullPointerException());
        
        EasyMockUnitils.replay();
        new BeforeTestClassStatement(clazz, listener, statement).evaluate();
    }
    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnStatement() throws Throwable {
        Class<?> clazz = TestClass2.class;
        
        listener.beforeTestClass(clazz);
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException());
        
        EasyMockUnitils.replay();
        new BeforeTestClassStatement(clazz, listener, statement).evaluate();
    }
    
    @Test
    public void testEvaluateOk() throws Throwable {
        Class<?> clazz = TestClass2.class;
        
        listener.beforeTestClass(clazz);
        statement.evaluate();

        EasyMockUnitils.replay();
        new BeforeTestClassStatement(clazz, listener, statement).evaluate();
    }
    
    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }

}
