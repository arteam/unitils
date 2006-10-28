package org.unitils;

import org.testng.annotations.*;
import org.unitils.core.TestListener;

/**
 * TestNG test class containing 2 test methods
 * <p/>
 * Test class used in the {@link UnitilsTestNGTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_TestClass2 extends UnitilsTestNG {


    @BeforeClass
    public void beforeClass() {
        UnitilsTestNGTest.callList.add("[TestNG]  beforeTestClass   - TestClass2");
    }

    @AfterClass
    public void afterClass() {
        UnitilsTestNGTest.callList.add("[TestNG]  afterTestClass    - TestClass2");
    }

    @BeforeMethod
    public void setUp() {
        UnitilsTestNGTest.callList.add("[TestNG]  testSetUp         - TestClass2");
    }

    @AfterMethod
    public void tearDown() {
        UnitilsTestNGTest.callList.add("[TestNG]  testTearDown      - TestClass2");
    }

    @Test
    public void test1() {
        UnitilsTestNGTest.callList.add("[TestNG]  testMethod        - TestClass2 - test1");
    }

    @Test
    public void test2() {
        UnitilsTestNGTest.callList.add("[TestNG]  testMethod        - TestClass2 - test2");
    }

    protected TestListener createTestListener() {
        return new UnitilsTestNGTest.TracingTestListener();
    }
}
