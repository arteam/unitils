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
package org.unitils.mock.core.matching;

import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import static org.unitils.mock.argumentmatcher.ArgumentMatcherPositionFinder.getArgumentMatcherIndexes;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.argumentmatcher.impl.DefaultArgumentMatcher;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;
import static org.unitils.mock.core.proxy.ProxyUtils.createProxy;
import static org.unitils.mock.core.proxy.StackTraceUtils.getInvocationStackTrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MatchingInvocationBuilder {

    protected String currentMockName;

    protected String definingMethodName;

    protected StackTraceElement[] invokedAt;


    public <T> T startMatchingInvocation(String mockName, Class<T> mockedType, MatchingInvocationHandler matchingInvocationHandler) {
        assertNotExpectingInvocation();
        this.currentMockName = mockName;

        this.invokedAt = getInvocationStackTrace(Mock.class);
        this.definingMethodName = invokedAt[0].getMethodName();
        ArgumentMatcherRepository.getInstance().registerStartOfMatchingInvocation(invokedAt[1].getLineNumber());
        return createProxy(mockName, mockedType, new InvocationHandler(matchingInvocationHandler));
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


    protected Object handleProxyInvocation(ProxyInvocation proxyInvocation, MatchingInvocationHandler matchingInvocationHandler) throws Throwable {
        ArgumentMatcherRepository.getInstance().registerEndOfMatchingInvocation(proxyInvocation.getLineNumber(), proxyInvocation.getMethod().getName());
        reset();

        List<ArgumentMatcher> argumentMatchers = createArgumentMatchers(proxyInvocation);
        Object result = matchingInvocationHandler.handleInvocation(proxyInvocation, argumentMatchers);
        ArgumentMatcherRepository.getInstance().reset();
        return result;
    }


    protected List<ArgumentMatcher> createArgumentMatchers(ProxyInvocation proxyInvocation) {
        List<ArgumentMatcher> result = new ArrayList<ArgumentMatcher>();

        ArgumentMatcherRepository argumentMatcherRepository = ArgumentMatcherRepository.getInstance();
        int matchInvocationStartLineNr = argumentMatcherRepository.getMatchInvocationStartLineNr();
        int matchInvocationEndLineNr = argumentMatcherRepository.getMatchInvocationEndLineNr();
        int matchInvocationIndex = argumentMatcherRepository.getMatchInvocationIndex();
        List<Integer> argumentMatcherIndexes = getArgumentMatcherIndexes(proxyInvocation, matchInvocationStartLineNr, matchInvocationEndLineNr, matchInvocationIndex);

        int argumentIndex = 0;
        Iterator<ArgumentMatcher> argumentMatcherIterator = ArgumentMatcherRepository.getInstance().getArgumentMatchers().iterator();
        for (Object argument : proxyInvocation.getArguments()) {
            if (argumentMatcherIndexes.contains(argumentIndex++)) {
                result.add(argumentMatcherIterator.next());
            } else {
                result.add(new DefaultArgumentMatcher(argument));
            }
        }
        argumentMatcherRepository.reset();
        return result;
    }

    protected class InvocationHandler implements ProxyInvocationHandler {

        private MatchingInvocationHandler matchingInvocationHandler;

        public InvocationHandler(MatchingInvocationHandler matchingInvocationHandler) {
            this.matchingInvocationHandler = matchingInvocationHandler;
        }

        public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
            return handleProxyInvocation(proxyInvocation, matchingInvocationHandler);
        }
    }
}
