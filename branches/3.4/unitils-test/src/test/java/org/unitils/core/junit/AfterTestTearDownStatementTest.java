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
 * AfterTestTearDownStatementTest.
 * 
 * @author wiw
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AfterTestTearDownStatementTest {
    
    @Mock
    private Statement statement;
    
    @Mock
    private TestListener listener;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link org.unitils.core.junit.AfterTestTearDownStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateStatementException() throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");
        
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException());
        
        
        EasyMockUnitils.replay();
        new AfterTestTearDownStatement(listener, statement, testObject, testMethod).evaluate();
        
    }
    
    /**
     * Test method for {@link org.unitils.core.junit.AfterTestTearDownStatement#evaluate()}.
     */
    @Test(expected = NullPointerException.class)
    public void testEvaluateAfterTestTearDowntException() throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");
        
        statement.evaluate();
        listener.afterTestTearDown(testObject, testMethod);
        EasyMock.expectLastCall().andThrow(new NullPointerException());
        
        
        EasyMockUnitils.replay();
        new AfterTestTearDownStatement(listener, statement, testObject, testMethod).evaluate();
        
    }
    
    @Test
    public void testEvaluateOk() throws Throwable {
        TestClass2 testObject = new TestClass2();
        Method testMethod = TestClass2.class.getMethod("test1");
        
        statement.evaluate();
        listener.afterTestTearDown(testObject, testMethod);
        
        EasyMockUnitils.replay();
        new AfterTestTearDownStatement(listener, statement, testObject, testMethod).evaluate();
        
    }

    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
