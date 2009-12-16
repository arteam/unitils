package org.unitils;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

public class UnitilsJUnit3Test_TestClassWithoutSetupAndTearDownMethods extends UnitilsJUnit3TestBase {

	public void test1() {
		registerTestInvocation(TEST_METHOD, "test1");
	}

	public void test2() {
		registerTestInvocation(TEST_METHOD, "test2");
	}

}
