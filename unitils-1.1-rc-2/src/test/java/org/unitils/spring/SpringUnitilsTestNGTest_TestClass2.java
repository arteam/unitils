package org.unitils.spring;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

import org.testng.annotations.Test;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringUnitilsTestNGTest_TestClass2 extends SpringUnitilsTestNGTest {

	@Test
    public void test1() {
        registerTestInvocation(TEST_METHOD, "test1");
    }


    @Test
    public void test2() {
        registerTestInvocation(TEST_METHOD, "test2");
    }
    
}
