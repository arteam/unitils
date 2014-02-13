/*
 * Copyright (c) Smals
 */
package org.unitils.core.junit;

import java.lang.reflect.Method;

import org.easymock.classextension.EasyMock;
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
 * test {@link BeforeTestSetUpStatement}.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class BeforeTestSetUpStatementTest {

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

    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnListener() throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");

        listener.beforeTestSetUp(testObject, testMethod);
        EasyMock.expectLastCall().andThrow(new NullPointerException());
        
        EasyMockUnitils.replay();
        new BeforeTestSetUpStatement(testObject, testMethod, listener, statement).evaluate();
        
    }
    
    @Test(expected = NullPointerException.class)
    public void testEvaluateExceptionOnStatement() throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");

        listener.beforeTestSetUp(testObject, testMethod);
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException());
        
        EasyMockUnitils.replay();
        new BeforeTestSetUpStatement(testObject, testMethod, listener, statement).evaluate();
        
    }
    
    @Test
    public void testEvaluateOk() throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");

        listener.beforeTestSetUp(testObject, testMethod);
        statement.evaluate();
 
        EasyMockUnitils.replay();
        new BeforeTestSetUpStatement(testObject, testMethod, listener, statement).evaluate();
        
    }
    

    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
