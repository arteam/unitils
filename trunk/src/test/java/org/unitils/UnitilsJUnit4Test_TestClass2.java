package org.unitils;

import org.junit.*;

import java.util.List;


/**
 * JUnit 4 test class containing 2 test methods
 */
public class UnitilsJUnit4Test_TestClass2 extends UnitilsJUnit4 {

    private static List<String> callList;

    public static void setCallList(List<String> list) {
        callList = list;
    }


    @BeforeClass
    public static void beforeClass() {
        callList.add("[Test]    beforeTestClass   - TestClass2");
    }

    @AfterClass
    public static void afterClass() {
        callList.add("[Test]    afterTestClass    - TestClass2");
    }

    @Before
    public void setUp() {
        callList.add("[Test]    testSetUp         - TestClass2");
    }

    @After
    public void tearDown() {
        callList.add("[Test]    testTearDown      - TestClass2");
    }

    @Test
    public void test1() {
        callList.add("[Test]    testMethod        - TestClass2 - test1");
    }

    @Test
    public void test2() {
        callList.add("[Test]    testMethod        - TestClass2 - test2");
    }
}
