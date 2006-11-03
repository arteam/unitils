package org.unitils;

import org.testng.annotations.*;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * TestNG test class containing 2 active and 1 ignored test method
 * <p/>
 * Test class used in the {@link UnitilsInvocationTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_TestClass1 extends UnitilsTestNG {

    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    @BeforeClass
    public void beforeClass() {
        addTestInvocation("beforeTestClass", null);
    }

    @AfterClass
    public void afterClass() {
        addTestInvocation("afterTestClass", null);
    }

    @BeforeMethod
    public void setUp() {
        addTestInvocation("testSetUp", null);
    }

    @AfterMethod
    public void tearDown() {
        addTestInvocation("testTearDown", null);
    }

    @Test
    public void test1() {
        addTestInvocation("testMethod", "test1");
    }

    @Test
    public void test2() {
        addTestInvocation("testMethod", "test2");
    }

    @Test(enabled = false)
    public void test3() {
        addTestInvocation("testMethod", "test3");
    }


    private void addTestInvocation(String invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.addTestInvocation(invocation, this, testMethodName);
        }
    }


    @Override
    protected Unitils getUnitils() {
        if (tracingTestListener != null) {
            return new Unitils() {

                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
        return super.getUnitils();
    }

}
