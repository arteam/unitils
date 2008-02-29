package org.unitils.spring.util;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.spring.SpringModule;

public class SpringUnitilsAdaptorTestExecutionListener implements TestExecutionListener {

	public void prepareTestInstance(TestContext testContext) throws Exception {
		registerTestContext(testContext);
		getTestListener().afterCreateTestObject(testContext.getTestInstance());
	}
	

	public void beforeTestSetUp(TestContext testContext) throws Exception {
		getTestListener().beforeTestSetUp(testContext.getTestInstance(), testContext.getTestMethod());
	}

	
	public void beforeTestMethod(TestContext testContext) throws Exception {
		getTestListener().beforeTestMethod(testContext.getTestInstance(), testContext.getTestMethod());
	}
	
	
	public void afterTestMethod(TestContext testContext) throws Exception {
		getTestListener().afterTestMethod(testContext.getTestInstance(), testContext.getTestMethod(), testContext.getTestException());
	}


	public void afterTestTearDown(TestContext testContext) throws Exception {
		getTestListener().afterTestTearDown(testContext.getTestInstance(), testContext.getTestMethod());
	}
	
	
	private void registerTestContext(TestContext testContext) {
		//getSpringModule().registerTestContext(testContext);
	}

	
	private SpringModule getSpringModule() {
		return getUnitils().getModulesRepository().getModuleOfType(SpringModule.class);
	}


	/**
	 * @return The Unitils test listener
	 */
	protected TestListener getTestListener() {
		return getUnitils().getTestListener();
	}
	
	
	/**
     * Returns the default singleton instance of Unitils
     *
     * @return the Unitils instance, not null
     */
    protected Unitils getUnitils() {
        return Unitils.getInstance();
    }

}
