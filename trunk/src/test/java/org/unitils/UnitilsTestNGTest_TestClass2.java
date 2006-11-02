package org.unitils;

import org.testng.annotations.*;
import org.unitils.core.TestListener;

/**
 * TestNG test class containing 2 test methods
 * <p/>
 * Test class used in the {@link UnitilsInvocationTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_TestClass2 extends UnitilsTestNG {


    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    @BeforeClass
    public void beforeClass() {
        tracingTestListener.addTestInvocation("beforeTestClass", this, null);
    }

    @AfterClass
    public void afterClass() {
        tracingTestListener.addTestInvocation("afterTestClass", this, null);
    }

    @BeforeMethod
    public void setUp() {
        tracingTestListener.addTestInvocation("testSetUp", this, null);
    }

    @AfterMethod
    public void tearDown() {
        tracingTestListener.addTestInvocation("testTearDown", this, null);
    }

    @Test
    public void test1() {
        tracingTestListener.addTestInvocation("testMethod", this, "test1");
    }

    @Test
    public void test2() {
        tracingTestListener.addTestInvocation("testMethod", this, "test2");
    }


    @Override
    protected TestListener createTestListener() {
        return tracingTestListener;
    }
}
