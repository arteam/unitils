/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.mock.core.matching;

import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.StackTraceService;

import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MatchingInvocationBuilder {

    protected ArgumentMatcherRepository argumentMatcherRepository;
    protected ProxyService proxyService;
    protected StackTraceService stackTraceService;

    protected UnitilsException previousMatchingInvocationNotCompletedException;


    public MatchingInvocationBuilder(ArgumentMatcherRepository argumentMatcherRepository, ProxyService proxyService, StackTraceService stackTraceService) {
        this.argumentMatcherRepository = argumentMatcherRepository;
        this.proxyService = proxyService;
        this.stackTraceService = stackTraceService;
    }


    public <T> T startMatchingInvocation(String mockName, Class<T> mockedType, boolean proxyInvocationRequired, MatchingInvocationHandler matchingInvocationHandler) {
        assertPreviousMatchingInvocationCompleted();

        StackTraceElement[] invokedAt = stackTraceService.getInvocationStackTrace(Mock.class);
        if (invokedAt == null) {
            throw new UnitilsException("Unable to start matching invocation. The matching invocation builder only supports calls from a mock object.");
        }
        if (proxyInvocationRequired) {
            this.previousMatchingInvocationNotCompletedException = createPreviousMatchingInvocationNotCompletedException(mockName, invokedAt);
        }

        argumentMatcherRepository.startMatchingInvocation(invokedAt[1].getLineNumber());
        return proxyService.createUninitializedProxy(mockName, new InvocationHandler(matchingInvocationHandler), mockedType);
    }

    public void assertPreviousMatchingInvocationCompleted() {
        if (previousMatchingInvocationNotCompletedException != null) {
            UnitilsException exception = previousMatchingInvocationNotCompletedException;
            reset();
            throw exception;
        }
    }

    public void reset() {
        previousMatchingInvocationNotCompletedException = null;
        argumentMatcherRepository.reset();
    }


    protected UnitilsException createPreviousMatchingInvocationNotCompletedException(String mockName, StackTraceElement[] invokedAt) {
        String methodName = invokedAt[0].getMethodName();
        StackTraceElement[] stackTrace = stackTraceService.getStackTraceStartingFrom(invokedAt, 1);

        UnitilsException exception = new UnitilsException("Invalid syntax: " + mockName + "." + methodName + "() must be followed by a method invocation on the returned proxy. E.g. " + mockName + "." + methodName + "().myMethod();");
        exception.setStackTrace(stackTrace);
        return exception;
    }

    protected Object handleProxyInvocation(ProxyInvocation proxyInvocation, MatchingInvocationHandler matchingInvocationHandler) throws Throwable {
        List<ArgumentMatcher> argumentMatchers = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
        reset();

        return matchingInvocationHandler.handleInvocation(proxyInvocation, argumentMatchers);
    }


    protected class InvocationHandler implements ProxyInvocationHandler {

        protected MatchingInvocationHandler matchingInvocationHandler;


        public InvocationHandler(MatchingInvocationHandler matchingInvocationHandler) {
            this.matchingInvocationHandler = matchingInvocationHandler;
        }


        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            return handleProxyInvocation(proxyInvocation, matchingInvocationHandler);
        }
    }
}
