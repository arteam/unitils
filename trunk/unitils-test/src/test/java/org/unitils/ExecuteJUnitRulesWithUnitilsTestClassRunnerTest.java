package org.unitils;

import static junit.framework.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

@RunWith(UnitilsJUnit4BlockTestClassRunner.class)
public class ExecuteJUnitRulesWithUnitilsTestClassRunnerTest {

	@Rule
	public TestName testName = new TestName();
	
	@Test
	public void test() {
		assertEquals("test", testName.getMethodName());
	}
	
}
