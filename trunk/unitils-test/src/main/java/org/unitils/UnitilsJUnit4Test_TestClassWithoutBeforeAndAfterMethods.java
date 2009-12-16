package org.unitils;

import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This test class is a plain JUnit 4 test without any lifecycle methods to to
 * setup and teardown the test. It ensures that all Unitils methods are called
 * even if the JUnit methods are not present (this test is required due to the
 * new block runner architecture in JUnit 4).
 */
public class UnitilsJUnit4Test_TestClassWithoutBeforeAndAfterMethods extends
		UnitilsJUnit4TestBase {

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
