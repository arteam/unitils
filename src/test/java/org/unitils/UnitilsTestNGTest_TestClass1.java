package org.unitils;

import org.testng.annotations.*;
import org.unitils.core.TestListener;

/**
 * TestNG test class containing 2 active and 1 ignored test method
 * <p/>
 * Test class used in the {@link UnitilsTestNGTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_TestClass1 extends UnitilsTestNG {


    @BeforeClass
    public void beforeClass() {
        UnitilsTestNGTest.callList.add("[TestNG]  beforeTestClass   - TestClass1");
    }

    @AfterClass
    public void afterClass() {
        UnitilsTestNGTest.callList.add("[TestNG]  afterTestClass    - TestClass1");
    }

    @BeforeMethod
    public void setUp() {
        UnitilsTestNGTest.callList.add("[TestNG]  testSetUp         - TestClass1");
    }

    @AfterMethod
    public void tearDown() {
        UnitilsTestNGTest.callList.add("[TestNG]  testTearDown      - TestClass1");
    }

    @Test
    public void test1() {
        UnitilsTestNGTest.callList.add("[TestNG]  testMethod        - TestClass1 - test1");
    }

    @Test
    public void test2() {
        UnitilsTestNGTest.callList.add("[TestNG]  testMethod        - TestClass1 - test2");
    }

    @Test(enabled = false)
    public void test3() {
        UnitilsTestNGTest.callList.add("[TestNG]  testMethod        - TestClass1 - test2");
    }

    protected TestListener createTestListener() {
        return new UnitilsTestNGTest.TracingTestListener();
    }

}
