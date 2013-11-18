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
package org.unitils.mock.core;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.util.List;

import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class MatchingInvocation extends ProxyInvocation {

    /* The argument matchers to use when matching the invocation */
    protected List<ArgumentMatcher<?>> argumentMatchers;


    /**
     * @param proxyInvocation  The proxy invocation, not null
     * @param argumentMatchers The argument matchers to use when matching the invocation, not null (there must be one for each argument in the invocation)
     */
    public MatchingInvocation(ProxyInvocation proxyInvocation, List<ArgumentMatcher<?>> argumentMatchers) {
        super(proxyInvocation);
        this.argumentMatchers = argumentMatchers;
    }


    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public String getInnerMockName() {
        return proxyName + "." + method.getName();
    }

    /**
     * Returns whether or not the given {@link org.unitils.mock.core.proxy.ProxyInvocation} matches this object's predefined <code>Method</code> and arguments.
     *
     * @param proxyInvocation the {@link org.unitils.mock.core.proxy.ProxyInvocation} to match.
     * @return A matching score for the invocation, -1 if there is no match
     */
    public int matches(ProxyInvocation proxyInvocation) {
        List<Argument<?>> arguments = proxyInvocation.getArguments();
        if (!getMethod().equals(proxyInvocation.getMethod())) {
            return -1;
        }

        int i = 0;
        int matchingScore = 0;
        for (Argument<?> argument : arguments) {
            ArgumentMatcher.MatchResult matchResult = matches(argument, i++);
            if (matchResult == NO_MATCH) {
                return -1;
            }
            matchingScore += matchResult.getScore();
        }
        return matchingScore;
    }

    public void matched(ProxyInvocation proxyInvocation) {
        int i = 0;
        for (Argument<?> argument : proxyInvocation.getArguments()) {
            matched(argument, i++);
        }
    }


    @SuppressWarnings("unchecked")
    protected <T> ArgumentMatcher.MatchResult matches(Argument<T> argument, int index) {
        ArgumentMatcher<T> argumentMatcher = (ArgumentMatcher<T>) argumentMatchers.get(index);
        return argumentMatcher.matches(argument);
    }

    @SuppressWarnings("unchecked")
    protected <T> void matched(Argument<T> argument, int index) {
        ArgumentMatcher<T> argumentMatcher = (ArgumentMatcher<T>) argumentMatchers.get(index);
        argumentMatcher.matched(argument);
    }
}