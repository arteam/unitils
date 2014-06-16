/*
 * Copyright 2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.unitils.TracingTestListener.InvocationSource.TEST;
import static org.unitils.TracingTestListener.InvocationSource.UNITILS;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_CLASS;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_CREATE_TEST_OBJECT;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_TEST_METHOD;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_TEST_TEARDOWN;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_TEST_METHOD;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_TEST_SET_UP;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.unitils.core.TestListener;

/**
 * Test listener that records all method invocations.
 * 
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TracingTestListener extends TestListener {

	public static enum TestFramework {
		JUNIT3, 
		JUNIT4, 
		TESTNG
	}
	
	public interface Invocation {}

	public static enum ListenerInvocation implements Invocation {
		LISTENER_BEFORE_CLASS,
		LISTENER_AFTER_CREATE_TEST_OBJECT, 
		LISTENER_BEFORE_TEST_SET_UP, 
		LISTENER_BEFORE_TEST_METHOD,
		LISTENER_AFTER_TEST_METHOD, 
		LISTENER_AFTER_TEST_TEARDOWN
	}
	
	public static enum TestInvocation implements Invocation {
		TEST_BEFORE_CLASS, 
		TEST_SET_UP,
		TEST_METHOD,
		TEST_TEAR_DOWN, 
		TEST_AFTER_CLASS
	}
	
	public static enum InvocationSource {
		TEST, 
		UNITILS
	}

	
    /* List that will contain a string representation of each method call */
    private List<Call> callList;

    private Invocation exceptionMethod;

    private boolean throwAssertionFailedError;
    
    /**
     * Delegate target for functions that need the original testlistener (with module support).
     */
    private TestListener delegate;
    
    /*
     * Test method that is currently executing
     */
    private Method currentTestMethod;
    
    /*
     * Exception that was thrown during the test method that is currently executing.
     * Is reset in beforeTestSetUp
     */
    private Throwable currentThrowable;

    public TracingTestListener(TestListener delegate) {
        this();
        this.delegate = delegate;
    }   
    

    public TracingTestListener() {
        this.callList = new ArrayList<Call>();
    }


    public List<Call> getCallList() {
        return callList;
    }


    public String getCallListAsString() {
        StringBuffer result = new StringBuffer();
        for (Call call : callList) {
            result.append(call);
            result.append('\n');
        }
        return result.toString();
    }


    public void expectExceptionInMethod(Invocation exceptionMethod, boolean throwAssertionFailedError) {
        this.exceptionMethod = exceptionMethod;
        this.throwAssertionFailedError = throwAssertionFailedError;
    }
    
    
    public void registerTestInvocation(TestInvocation invocation, Class<?> testClass, String methodName) {
        callList.add(new Call(invocation, testClass, methodName));
        throwExceptionIfRequested(invocation);
    }
    
    
    public void registerListenerInvocation(ListenerInvocation listenerInvocation, Class<?> testClass, Object test, Method testMethod, Throwable throwable) {
    	callList.add(new Call(listenerInvocation, testClass, testMethod == null ? null : testMethod.getName(), throwable));
    }
    
    
    @Override
	public void beforeTestClass(Class<?> testClass) {
		registerListenerInvocation(LISTENER_BEFORE_CLASS, testClass, null, null, null);
	}


	@Override
    public void afterCreateTestObject(Object testObject) {
        registerListenerInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testObject.getClass(), testObject, null, null);
        this.delegate.afterCreateTestObject(testObject);
        throwExceptionIfRequested(LISTENER_AFTER_CREATE_TEST_OBJECT);
    }

    @Override
    public void beforeTestSetUp(Object testObject, Method testMethod) {
    	currentTestMethod = testMethod;
    	currentThrowable = null;
    	
        registerListenerInvocation(LISTENER_BEFORE_TEST_SET_UP, testObject.getClass(), testObject, testMethod, null);
        throwExceptionIfRequested(LISTENER_BEFORE_TEST_SET_UP);
    }

    @Override
    public void beforeTestMethod(Object testObject, Method testMethod) {
    	assertEquals(currentTestMethod, testMethod);
    	
        registerListenerInvocation(LISTENER_BEFORE_TEST_METHOD, testObject.getClass(), testObject, testMethod, null);
        throwExceptionIfRequested(LISTENER_BEFORE_TEST_METHOD);
    }

    @Override
    public void afterTestMethod(Object testObject, Method testMethod, Throwable throwable) {
    	assertEquals(currentTestMethod, testMethod);
    	assertTrue(throwable == null || (currentThrowable != null && currentThrowable.equals(throwable)));
    	
    	registerListenerInvocation(LISTENER_AFTER_TEST_METHOD, testObject.getClass(), testObject, testMethod, throwable);
        throwExceptionIfRequested(LISTENER_AFTER_TEST_METHOD);
    }

    @Override
    public void afterTestTearDown(Object testObject, Method testMethod) {
    	registerListenerInvocation(LISTENER_AFTER_TEST_TEARDOWN, testObject.getClass(), testObject, testMethod, null);
        throwExceptionIfRequested(LISTENER_AFTER_TEST_TEARDOWN);
    }

    @Override
    public boolean shouldInvokeTestMethod(Object testObject, Method testMethod) {
        // delegate to the main testlistener, so that the multipart module can do its job (and maybe others in future)
        return delegate.shouldInvokeTestMethod(testObject, testMethod);
    }
    
    

    private void throwExceptionIfRequested(Invocation exceptionMethod) {
        if (this.exceptionMethod == null || !this.exceptionMethod.equals(exceptionMethod)) {
            return;
        }
        if (throwAssertionFailedError) {
            AssertionFailedError error = new AssertionFailedError(exceptionMethod.toString());
            currentThrowable = error;
            throw error;
        }
        RuntimeException exception = new RuntimeException(exceptionMethod.toString());
        currentThrowable = exception;
		throw exception;
    }
            
    public Throwable getCurrentThrowable() {
		return currentThrowable;
	}
    

   public static class Call {

        private InvocationSource invocationSource;
        private Invocation invocation;
        private Class<?> testClass;
        private String testMethod;
        private Throwable throwable;

        public Call(Invocation invocation, Class<?> testClass) {
            this(invocation, testClass, null);
        }

        public Call(Invocation invocation, Class<?> testClass, String testMethod) {
            this(invocation, testClass, testMethod, null);
        }

        public Call(Invocation invocation, Class<?> testClass, String testMethod, Throwable throwable) {
            if (invocation instanceof TestInvocation) {
                this.invocationSource = TEST;
            } else {
                this.invocationSource = UNITILS;
            }
            this.invocation = invocation;
            this.testClass = testClass;
            this.testMethod = testMethod;
            this.throwable = throwable;
        }

        public Invocation getInvocation() {
            return invocation;
        }

        public Class<?> getTestClass() {
            return testClass;
        }

        public String getTestMethod() {
            return testMethod;
        }
        
        

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((invocation == null) ? 0 : invocation.hashCode());
            result = prime
                    * result
                    + ((invocationSource == null) ? 0 : invocationSource
                    .hashCode());
            result = prime * result
                    + ((testClass == null) ? 0 : testClass.hashCode());
            result = prime * result
                    + ((testMethod == null) ? 0 : testMethod.hashCode());
            result = prime * result
                    + ((throwable == null) ? 0 : throwable.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Call other = (Call) obj;
            if (invocation == null) {
                if (other.invocation != null) {
                    return false;
                }
            } else if (!invocation.equals(other.invocation)) {
                return false;
            }
            if (invocationSource == null) {
                if (other.invocationSource != null) {
                    return false;
                }
            } else if (!invocationSource.equals(other.invocationSource)) {
                return false;
            }
            if (testClass == null) {
                if (other.testClass != null) {
                    return false;
                }
            } else if (!testClass.equals(other.testClass)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return invocationSource + " " + invocation + " "
                    + (testClass == null ? "" : testClass.getSimpleName())
                    + (testMethod == null ? "" : " " + testMethod)
                    + (throwable == null ? "" : " " + throwable);
        }
    }
	
}



