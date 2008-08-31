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
package org.unitils.mock.core;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class BehaviorDefiningInvocation {

    private String mockName;

    private ProxyInvocation proxyInvocation;

    private List<ArgumentMatcher> argumentMatchers;

    private MockBehavior mockBehavior;


    public BehaviorDefiningInvocation(String mockName, ProxyInvocation proxyInvocation, List<ArgumentMatcher> argumentMatchers, MockBehavior mockBehavior) {
        this.mockName = mockName;
        this.proxyInvocation = proxyInvocation;
        this.argumentMatchers = argumentMatchers;
        this.mockBehavior = mockBehavior;

        Method method = proxyInvocation.getMethod();
        if (method.getParameterTypes().length != argumentMatchers.size()) {
            throw new IllegalArgumentException("The number of argument matchers does not match the number of arguments of the given method. Number of argument matchers: " + argumentMatchers.size() + ". Number of arguments " + method.getParameterTypes().length);
        }
    }

    public String getMockName() {
        return mockName;
    }


    public ProxyInvocation getProxyInvocation() {
        return proxyInvocation;
    }


    public List<ArgumentMatcher> getArgumentMatchers() {
        return argumentMatchers;
    }


    public MockBehavior getMockBehavior() {
        return mockBehavior;
    }

    /**
     * Returns whether or not the given {@link ProxyInvocation} matches this object's predefined <code>Method</code> and arguments.
     *
     * @param proxyInvocation the {@link ProxyInvocation} to match.
     * @return true when given {@link org.unitils.mock.proxy.ProxyInvocation} matches, false otherwise.
     */
    public boolean matches(ProxyInvocation proxyInvocation) {
        if (!this.proxyInvocation.getMethod().equals(proxyInvocation.getMethod())) {
            return false;
        }
        List<?> arguments = proxyInvocation.getArguments();
        if (arguments.size() != argumentMatchers.size()) {
            return false;
        }
        for (int i = 0; i < arguments.size(); ++i) {
            if (!argumentMatchers.get(i).matches(arguments.get(i))) {
                return false;
            }
        }
        return true;
    }


}