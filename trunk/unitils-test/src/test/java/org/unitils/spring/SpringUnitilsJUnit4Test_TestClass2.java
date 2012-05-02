package org.unitils.spring;

import org.junit.*;

import static org.unitilsnew.TracingTestListener.TestInvocation.*;

public class SpringUnitilsJUnit4Test_TestClass2 extends SpringUnitilsJUnit4TestBase {

    @BeforeClass
    public static void beforeClass() {
        registerTestInvocation(TEST_BEFORE_CLASS, SpringUnitilsJUnit4Test_TestClass2.class, null);
    }


    @AfterClass
    public static void afterClass() {
        registerTestInvocation(TEST_AFTER_CLASS, SpringUnitilsJUnit4Test_TestClass2.class, null);
    }


    @Before
    public void setUp() {
        registerTestInvocation(TEST_SET_UP, this.getClass(), null);
    }

    @After
    public void tearDown() {
        registerTestInvocation(TEST_TEAR_DOWN, this.getClass(), null);
    }


    @Test
    public void test1() {
        registerTestInvocation(TEST_METHOD, this.getClass(), "test1");
    }


    @Test
    public void test2() {
        registerTestInvocation(TEST_METHOD, this.getClass(), "test2");
    }

}
