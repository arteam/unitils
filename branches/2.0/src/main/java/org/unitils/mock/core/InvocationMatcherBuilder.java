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

import static org.unitils.mock.core.ArgumentMatcherPositionFinder.getArgumentMatcherIndexes;
import org.unitils.mock.core.argumentmatcher.EqualsArgumentMatcher;
import org.unitils.mock.core.argumentmatcher.LenEqArgumentMatcher;
import static org.unitils.util.ReflectionUtils.getClassWithName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class InvocationMatcherBuilder {

    private MockObject<?> mockObject;

    private List<ArgumentMatcher> argumentMatchers, registeredArgumentMatchers;

    private Invocation invocation;

    private static InvocationMatcherBuilder instance;

    public static InvocationMatcherBuilder getInstance() {
        if (instance == null) {
            instance = new InvocationMatcherBuilder();
        }
        return instance;
    }


    private InvocationMatcherBuilder() {
        reset();
    }


    public <T> void registerMockObject(MockObject<T> mockObject) {
        this.mockObject = mockObject;
    }


    public void registerInvokedMethod(Invocation invocation) {
        this.invocation = invocation;

        argumentMatchers = new ArrayList<ArgumentMatcher>();
        Class<?> testClass = getClassWithName(invocation.getInvokedAt().getClassName());
        String testMethodName = invocation.getInvokedAt().getMethodName();
        Method proxyMethod = invocation.getProxyMethod();
        int lineNr = invocation.getInvokedAt().getLineNumber();

        List<Integer> argumentMatcherIndexes = getArgumentMatcherIndexes(testClass, testMethodName, proxyMethod, lineNr, 1);
        Iterator<ArgumentMatcher> argumentMatcherIterator = registeredArgumentMatchers.iterator();
        for (int argumentIndex = 0; argumentIndex < invocation.getArguments().size(); argumentIndex++) {
            if (argumentMatcherIndexes != null && argumentMatcherIndexes.contains(argumentIndex)) {
                argumentMatchers.add(argumentMatcherIterator.next());
            } else {
                Object argument = invocation.getArguments().get(argumentIndex);
                if (argument instanceof Number) {
                    argumentMatchers.add(new EqualsArgumentMatcher(argument));
                } else {
                    argumentMatchers.add(new LenEqArgumentMatcher(argument));
                }
            }
        }
    }


    public void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
        registeredArgumentMatchers.add(argumentMatcher);
    }


    public InvocationMatcher createInvocationMatcher() {
        return new InvocationMatcher(invocation.getProxyMethod(), argumentMatchers);
    }


    public void reset() {
        this.registeredArgumentMatchers = new ArrayList<ArgumentMatcher>();
        this.invocation = null;
    }

}
