/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core;

import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.proxy.ProxyInvocation;
import org.unitils.mock.proxy.ProxyInvocationHandler;
import static org.unitils.mock.proxy.ProxyUtils.createProxy;
import static org.unitils.util.StackTraceUtils.getInvocationStackTrace;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SyntaxMonitor {

    protected String currentMockName;

    protected String definingMethodName;

    protected StackTraceElement[] invokedAt;


    public <T> T getProxyInstance(String mockName, Class<T> mockedType, ProxyInvocationHandler matchingInvocationHandler) {
        assertNotExpectingInvocation();
        this.currentMockName = mockName;

        this.invokedAt = getInvocationStackTrace(Mock.class);
        this.definingMethodName = invokedAt[0].getMethodName();
        ArgumentMatcherRepository.getInstance().registerStartOfMatchingInvocation(invokedAt[1].getLineNumber());
        return createProxy(mockedType, new InvocationHandler(matchingInvocationHandler));
    }


    public void reset() {
        this.currentMockName = null;
        this.invokedAt = null;
        this.definingMethodName = null;
    }


    public void assertNotExpectingInvocation() {
        if (currentMockName != null && !currentMockName.contains(".")) {
            UnitilsException exception = new UnitilsException("Invalid syntax. " + currentMockName + "." + definingMethodName + "() must be followed by a method invocation on the returned proxy. E.g. " + currentMockName + "." + definingMethodName + "().myMethod();");
            exception.setStackTrace(invokedAt);
            reset();
            throw exception;
        }
        reset();
    }


    protected class InvocationHandler implements ProxyInvocationHandler {

        private ProxyInvocationHandler matchingInvocationHandler;

        public InvocationHandler(ProxyInvocationHandler matchingInvocationHandler) {
            this.matchingInvocationHandler = matchingInvocationHandler;
        }

        @SuppressWarnings({"unchecked"})
        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            ArgumentMatcherRepository.getInstance().registerEndOfMatchingInvocation(proxyInvocation.getLineNumber(), proxyInvocation.getMethod().getName());
            reset();
            Object result = matchingInvocationHandler.handleInvocation(proxyInvocation);
            ArgumentMatcherRepository.getInstance().reset();
            return result;
        }
    }
}
