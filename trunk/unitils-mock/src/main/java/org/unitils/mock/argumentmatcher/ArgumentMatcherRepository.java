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
package org.unitils.mock.argumentmatcher;

import org.unitils.core.UnitilsException;
import org.unitils.mock.argumentmatcher.impl.DefaultArgumentMatcher;
import org.unitils.mock.core.proxy.CloneService;
import org.unitils.mock.core.proxy.ProxyInvocation;

import java.lang.reflect.Method;
import java.util.*;

/**
 * A repository for holding the current set of argument matchers.
 * <p/>
 * Argument matchers are placed inline in method invocations. Java will evaluate them before the method is performed.
 * E.g. method1(notNull()) => not null will be called before method1.
 * <p/>
 * For this we need to store the current argument matchers so that they can be linked to the method invocation that
 * will follow.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ArgumentMatcherRepository {

    protected ArgumentMatcherPositionFinder argumentMatcherPositionFinder;
    protected CloneService cloneService;

    /* The current argument matchers */
    protected List<ArgumentMatcher> argumentMatchers = new ArrayList<ArgumentMatcher>();
    /* The begin line-nr of the matching invocation, -1 if not started */
    protected int matchInvocationLineNr = -1;
    /* The name of the previous matching method we handled */
    protected Map<Method, Integer> indexesPerMatchingMethods = new HashMap<Method, Integer>(2);
    /* The line nr of the previous method we handled */
    protected int previousMatchingLineNr = -1;


    public ArgumentMatcherRepository(ArgumentMatcherPositionFinder argumentMatcherPositionFinder, CloneService cloneService) {
        this.argumentMatcherPositionFinder = argumentMatcherPositionFinder;
        this.cloneService = cloneService;
    }


    /**
     * From the moment that this method is called until {@link #finishMatchingInvocation} has been called,
     * argument matchers can be registered.
     *
     * @param lineNr The line number at which the matching invocation starts, i.e. the line number at which the performs, assertInvoked, etc.
     *               statement occurs.
     */
    public void startMatchingInvocation(int lineNr) {
        argumentMatchers.clear();
        matchInvocationLineNr = lineNr;
    }

    /**
     * Registers an argument matcher at the given line nr.
     *
     * @param argumentMatcher The matcher, ignored when null
     */
    public void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
        if (argumentMatcher == null) {
            // nothing to register
            return;
        }
        if (matchInvocationLineNr == -1) {
            throw new UnitilsException("Unable to register argument matcher. Argument matchers can only be used when defining behavior for a mock (e.g. returns) or when doing an assert on a mock. Argument matcher: " + argumentMatcher);
        }
        argumentMatchers.add(argumentMatcher);
    }

    /**
     * Finish the matching invocation and returns all registered argument matchers. A default argument matcher is
     * returned for an argument when no argument matcher was used.
     * <p/>
     * An exception is raised when the matching invocation was not started
     *
     * @param proxyInvocation The matching invocation, not null
     * @return The argument matchers, empty when none found
     */
    public List<ArgumentMatcher> finishMatchingInvocation(ProxyInvocation proxyInvocation) {
        Method method = proxyInvocation.getMethod();
        int proxyInvocationLineNr = proxyInvocation.getLineNumber();
        List<?> arguments = proxyInvocation.getArguments();

        if (matchInvocationLineNr == -1) {
            UnitilsException exception = new UnitilsException("Unable to finish matching invocation: the matching invocation was not started first. Proxy method: " + method.getName());
            exception.setStackTrace(proxyInvocation.getInvokedAtTrace());
            throw exception;
        }

        Integer index = null;
        if (proxyInvocationLineNr != previousMatchingLineNr) {
            indexesPerMatchingMethods.clear();
        } else {
            index = indexesPerMatchingMethods.get(method);
        }
        if (index == null) {
            index = 1;
        } else {
            index++;
        }
        indexesPerMatchingMethods.put(method, index);
        previousMatchingLineNr = proxyInvocationLineNr;

        List<Integer> argumentMatcherIndexes = argumentMatcherPositionFinder.getArgumentMatcherIndexes(proxyInvocation, matchInvocationLineNr, proxyInvocationLineNr, index);
        List<ArgumentMatcher> result = createArgumentMatchers(arguments, argumentMatcherIndexes);

        argumentMatchers.clear();
        matchInvocationLineNr = -1;
        return result;
    }

    /**
     * Clears the current argument matchers. After this method is called, {@link #startMatchingInvocation} must
     * be called again to be able to register argument matchers.
     */
    public void reset() {
        argumentMatchers.clear();
        matchInvocationLineNr = -1;

        previousMatchingLineNr = -1;
        indexesPerMatchingMethods.clear();
    }


    protected List<ArgumentMatcher> createArgumentMatchers(List<?> arguments, List<Integer> argumentMatcherIndexes) {
        List<ArgumentMatcher> result = new ArrayList<ArgumentMatcher>();

        int argumentIndex = 0;
        Iterator<ArgumentMatcher> argumentMatcherIterator = argumentMatchers.iterator();
        for (Object argument : arguments) {
            ArgumentMatcher argumentMatcher;
            if (argumentMatcherIndexes.contains(argumentIndex++)) {
                argumentMatcher = argumentMatcherIterator.next();
            } else {
                argumentMatcher = createDefaultArgumentMatcher(argument);
            }
            result.add(argumentMatcher);
        }
        return result;
    }

    protected ArgumentMatcher createDefaultArgumentMatcher(Object value) {
        Object clonedValue = cloneService.createDeepClone(value);
        return new DefaultArgumentMatcher(value, clonedValue);
    }
}
