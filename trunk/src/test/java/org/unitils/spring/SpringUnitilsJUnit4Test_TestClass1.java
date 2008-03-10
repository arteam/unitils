package org.unitils.spring;

import static org.unitils.TracingTestListener.TestInvocation.TEST_AFTER_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_BEFORE_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;
import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SpringUnitilsJUnit4Test_TestClass1 extends SpringUnitilsJUnit4Test {

	@BeforeClass
    public static void beforeClass() {
        registerTestInvocation(TEST_BEFORE_CLASS, SpringUnitilsJUnit4Test_TestClass1.class, null);
    }


    @AfterClass
    public static void afterClass() {
        registerTestInvocation(TEST_AFTER_CLASS, SpringUnitilsJUnit4Test_TestClass1.class, null);
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


    @Ignore
    @Test
    public void test3() {
        registerTestInvocation(TEST_METHOD, this.getClass(), "test3");
    }
}
