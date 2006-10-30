package org.unitils;

import org.junit.*;

import java.util.List;

/**
 * JUnit 4 test class containing 2 active and 1 ignored test method
 */
public class UnitilsJUnit4Test_TestClass1 extends UnitilsJUnit4 {

    private static List<String> callList;

    public static void setCallList(List<String> list) {
        callList = list;
    }

    @BeforeClass
    public static void beforeClass() {
        callList.add("[Test]    beforeTestClass   - TestClass1");
    }

    @AfterClass
    public static void afterClass() {
        callList.add("[Test]    afterTestClass    - TestClass1");
    }

    @Before
    public void setUp() {
        callList.add("[Test]    testSetUp         - TestClass1");
    }

    @After
    public void tearDown() {
        callList.add("[Test]    testTearDown      - TestClass1");
    }

    @Test
    public void test1() {
        callList.add("[Test]    testMethod        - TestClass1 - test1");
    }

    @Test
    public void test2() {
        callList.add("[Test]    testMethod        - TestClass1 - test2");
    }

    @Ignore
    @Test
    public void test3() {
        callList.add("[Test]    testMethod        - TestClass1 - test2");
    }
}
