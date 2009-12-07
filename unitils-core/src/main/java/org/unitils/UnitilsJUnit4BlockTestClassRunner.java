package org.unitils;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

public class UnitilsJUnit4BlockTestClassRunner extends BlockJUnit4ClassRunner {

	private boolean beforesRun;
	
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
        Object testObject = super.createTest();
        getTestListener().afterCreateTestObject(testObject);
        return testObject;
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod frameworkMethod, final Object o) {
        return new InvokeMethod(frameworkMethod, o) {
            @Override
            public void evaluate() throws Throwable {
                Throwable threw = null;
                try {
                	getTestListener().beforeTestMethod(o, frameworkMethod.getMethod());
                    super.evaluate();
                } catch (Throwable t) {
                    threw = t;
                } finally {
                    getTestListener().afterTestMethod(o, frameworkMethod.getMethod(), threw);
                }
                if(threw != null) {
                	throw threw;
                }
            }
        };
    }

    @Override
    protected Statement withBefores(final FrameworkMethod method, final Object target,
    		Statement statement) {
    	final Statement befores = super.withBefores(method, target, statement);
    	return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				beforesRun = false;
				getTestListener().beforeTestSetUp(target, method.getMethod());
				beforesRun = true;
				befores.evaluate();
			}
		};
    }
    
    @Override
    protected Statement withAfters(final FrameworkMethod method, final Object target,
    		Statement statement) {
		
    	List<FrameworkMethod> afters= getTestClass().getAnnotatedMethods(After.class);
		return afters.isEmpty() ? statement :
			new RunAfters(statement, afters, target, method);
    }
    
    private class RunAfters  extends Statement {
    	private final Statement fNext;
    	private final Object fTarget;
    	private final List<FrameworkMethod> fAfters;
    	private final FrameworkMethod fMethod;
    	
    	public RunAfters(Statement next, List<FrameworkMethod> afters, Object target, FrameworkMethod method) {
    		fNext= next;
    		fAfters= afters;
    		fTarget= target;
    		fMethod = method;
    	}

    	@Override
    	public void evaluate() throws Throwable {
    		List<Throwable> errors = new ArrayList<Throwable>();
    		errors.clear();
    		try {
    			fNext.evaluate();
    		} catch (Throwable e) {
    			errors.add(e);
    		} finally {
    			if(beforesRun) {
	    			for (FrameworkMethod each : fAfters) {
	    				try {
	    					each.invokeExplosively(fTarget);
	    				} catch (Throwable e) {
	    					errors.add(e);
	    				}
	    			}
    			}
    			getTestListener().afterTestTearDown(fTarget, fMethod.getMethod());
    		}
    		MultipleFailureException.assertEmpty(errors);
    	}
    }
    
}
