package org.unitils;

import org.junit.*;


/**
 * JUnit 4 test class containing 2 test methods
 */
public class UnitilsJUnit4Test_TestClass2 extends UnitilsJUnit4 {

    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    @BeforeClass
    public static void beforeClass() {
        addTestInvocation("beforeTestClass", UnitilsJUnit4Test_TestClass2.class, null);
    }

    @AfterClass
    public static void afterClass() {
        addTestInvocation("afterTestClass", UnitilsJUnit4Test_TestClass2.class, null);
    }

    @Before
    public void setUp() {
        addTestInvocation("testSetUp", this, null);
    }

    @After
    public void tearDown() {
        addTestInvocation("testTearDown", this, null);
    }

    @Test
    public void test1() {
        addTestInvocation("testMethod", this, "test1");
    }

    @Test
    public void test2() {
        addTestInvocation("testMethod", this, "test2");
    }


    private static void addTestInvocation(String invocation, Object test, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.addTestInvocation(invocation, test, testMethodName);
        }
    }
}
