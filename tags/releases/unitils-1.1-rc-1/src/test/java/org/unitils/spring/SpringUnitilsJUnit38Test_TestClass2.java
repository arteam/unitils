package org.unitils.spring;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

public class SpringUnitilsJUnit38Test_TestClass2 extends SpringUnitilsJUnit38Test {

	public void test1() {
        registerTestInvocation(TEST_METHOD, "test1");
    }


    public void test2() {
        registerTestInvocation(TEST_METHOD, "test2");
    }
    
}
