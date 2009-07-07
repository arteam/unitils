package org.unitils.spring;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

import org.testng.annotations.Test;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringUnitilsTestNGTest_TestClass1 extends SpringUnitilsTestNGTest {

	@Test
    public void test1() {
        registerTestInvocation(TEST_METHOD, "test1");
    }


    @Test
    public void test2() {
        registerTestInvocation(TEST_METHOD, "test2");
    }


    @Test(enabled = false)
    public void test3() {
        registerTestInvocation(TEST_METHOD, "test3");
    }
}
