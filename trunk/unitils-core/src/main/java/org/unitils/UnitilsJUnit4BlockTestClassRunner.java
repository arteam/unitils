package org.unitils;

import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

public class UnitilsJUnit4BlockTestClassRunner extends BlockJUnit4ClassRunner {

	private Object testObject;
	
    public UnitilsJUnit4BlockTestClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    private TestListener getTestListener() {
    	return Unitils.getInstance().getTestListener();
    }
    
    @Override
    protected Statement classBlock(RunNotifier runNotifier) {
    	final Statement defaultStatement = super.classBlock(runNotifier);
    	return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				getTestListener().beforeTestClass(getTestClass().getJavaClass());
				defaultStatement.evaluate();
			}
		};
    }

    @Override
    protected Object createTest() throws Exception {
        testObject = super.createTest();
        getTestListener().afterCreateTestObject(testObject);
        return testObject;
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod frameworkMethod, final Object o) {
        return new InvokeMethod(frameworkMethod, o) {
            @Override
            public void evaluate() throws Throwable {
                getTestListener().beforeTestMethod(o, frameworkMethod.getMethod());
                Throwable threw = null;
                try {
                    super.evaluate();
                } catch (Throwable t) {
                    threw = t;
                } finally {
                    getTestListener().afterTestMethod(o, frameworkMethod.getMethod(), threw);
                }
            }
        };
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod frameworkMethod) {
    	final Statement defaultStatement = super.methodBlock(frameworkMethod);
    	return new Statement() {
			@Override
			public void evaluate() throws Throwable {
                getTestListener().beforeTestSetUp(testObject, frameworkMethod.getMethod());
				defaultStatement.evaluate();
                getTestListener().afterTestTearDown(testObject, frameworkMethod.getMethod());
			}
		};
    }
    
}
