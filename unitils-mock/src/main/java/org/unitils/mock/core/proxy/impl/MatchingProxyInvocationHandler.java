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
package org.unitils.mock.core.proxy.impl;

import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.util.StackTraceService;

import java.util.List;


public class MatchingProxyInvocationHandler implements ProxyInvocationHandler {


    protected ArgumentMatcherRepository argumentMatcherRepository;
    protected ProxyService proxyService;
    protected StackTraceService stackTraceService;

    protected MatchingInvocationHandler matchingInvocationHandler;
    protected UnitilsException previousMatchingInvocationNotCompletedException;


    public MatchingProxyInvocationHandler(ArgumentMatcherRepository argumentMatcherRepository, ProxyService proxyService, StackTraceService stackTraceService) {
        this.argumentMatcherRepository = argumentMatcherRepository;
        this.proxyService = proxyService;
        this.stackTraceService = stackTraceService;
    }


    public void startMatchingInvocation(String mockName, boolean proxyInvocationRequired, MatchingInvocationHandler matchingInvocationHandler) {
        assertPreviousMatchingInvocationCompleted();

        StackTraceElement[] invokedAt = stackTraceService.getInvocationStackTrace(Mock.class);
        if (invokedAt == null) {
            throw new UnitilsException("Unable to start matching invocation. Matching invocations are only supported from a Mock instance");
        }
        if (proxyInvocationRequired) {
            this.previousMatchingInvocationNotCompletedException = createPreviousMatchingInvocationNotCompletedException(mockName, invokedAt);
        }

        this.matchingInvocationHandler = matchingInvocationHandler;
        argumentMatcherRepository.startMatchingInvocation(invokedAt[1].getLineNumber());
    }

    public void assertPreviousMatchingInvocationCompleted() {
        if (previousMatchingInvocationNotCompletedException != null) {
            UnitilsException exception = previousMatchingInvocationNotCompletedException;
            reset();
            throw exception;
        }
    }

    public void reset() {
        matchingInvocationHandler = null;
        previousMatchingInvocationNotCompletedException = null;
    }

    public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        try {
            if (matchingInvocationHandler == null) {
                UnitilsException e = new UnitilsException("Unexpected matching proxy invocation. Expected following syntax 'mock'.'matching method'.'method'. E.g. myMock.returns().myMethod();");
                e.setStackTrace(proxyInvocation.getInvokedAtTrace());
                throw e;
            }
            List<ArgumentMatcher> argumentMatchers = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
            return matchingInvocationHandler.handleInvocation(proxyInvocation, argumentMatchers);
        } finally {
            reset();
        }
    }


    protected UnitilsException createPreviousMatchingInvocationNotCompletedException(String mockName, StackTraceElement[] invokedAt) {
        String methodName = invokedAt[0].getMethodName();
        StackTraceElement[] stackTrace = stackTraceService.getStackTraceStartingFrom(invokedAt, 1);

        UnitilsException exception = new UnitilsException("Invalid syntax: " + mockName + "." + methodName + "() must be followed by a method invocation on the returned proxy. E.g. " + mockName + "." + methodName + "().myMethod();");
        exception.setStackTrace(stackTrace);
        return exception;
    }
}