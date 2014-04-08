package org.unitils.core.junit;

import java.lang.reflect.Method;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.EasyMockUnitils;
import org.unitils.easymock.annotation.Mock;


/**
 * Test {@link AfterTestMethodStatement}.
 * 
 * @author Willemijn Wouters
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class AfterTestMethodStatementMockTest {
    
    @Mock
    private TestListener listener;
    
    @Mock
    private Statement statement;
    
    @Test
    public void testEvaluateOk() throws Throwable {
        Method method = TestClass2.class.getMethod("test1");
        TestClass2 testObject = new TestClass2();
        statement.evaluate();
        listener.afterTestMethod(testObject, method, null);
        
        EasyMockUnitils.replay();
        
        new AfterTestMethodStatement(listener, statement, method, testObject).evaluate();
    }
    
    @Test(expected = NullPointerException.class)
    public void testEvaluateStatementAndTestListenerException() throws Throwable {
        Method method = TestClass2.class.getMethod("test1");
        TestClass2 testObject = new TestClass2();
        statement.evaluate();
        EasyMock.expectLastCall().andThrow(new NullPointerException("This is a test NullpointerException"));
        listener.afterTestMethod(testObject, method, null);
        EasyMock.expectLastCall().andThrow(new UnitilsException());
        EasyMockUnitils.replay();
        
        new AfterTestMethodStatement(listener, statement, method, testObject).evaluate();
    }
    
    private class TestClass2 {
        @Test
        public void test1() {
            Assert.assertTrue(true);
        }
    }
}
