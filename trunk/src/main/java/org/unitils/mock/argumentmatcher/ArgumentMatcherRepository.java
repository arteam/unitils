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
package org.unitils.mock.argumentmatcher;

import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.List;

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

    /* The singleton instance */
    private static ThreadLocal<ArgumentMatcherRepository> instance = new InheritableThreadLocal<ArgumentMatcherRepository>() {
        protected ArgumentMatcherRepository initialValue() {
            return new ArgumentMatcherRepository();
        }
    };


    /**
     * @return The singleton instance, not null
     */
    public static ArgumentMatcherRepository getInstance() {
        return instance.get();
    }


    /* The current argument matchers */
    private List<ArgumentMatcher> argumentMatchers = new ArrayList<ArgumentMatcher>();

    /* Determines whether the repository can accept argument matchers */
    private boolean acceptingArgumentMatchers = false;

    /* The begin line-nr of the invocation */
    private int matchInvocationStartLineNr;

    /* The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line) */
    private int matchInvocationEndLineNr;


    /**
     * Registers an argument matcher at the given line nr.
     *
     * @param argumentMatcher The matcher, not null
     * @param lineNr          The line number on which the argument matcher was registered.
     */
    public void registerArgumentMatcher(ArgumentMatcher argumentMatcher, int lineNr) {
        if (!acceptingArgumentMatchers) {
            throw new UnitilsException("Argument matchers cannot be used outside the context of a behavior definition or assert statement");
        }
        matchInvocationEndLineNr = Math.max(matchInvocationEndLineNr, lineNr);
        argumentMatchers.add(argumentMatcher);
    }


    /**
     * Gets the current argument matchers.
     *
     * @return The matchers, not null
     */
    public List<ArgumentMatcher> getArgumentMatchers() {
        return argumentMatchers;
    }


    /**
     * @return The begin line-nr of the invocation
     */
    public int getMatchInvocationStartLineNr() {
        return matchInvocationStartLineNr;
    }


    /**
     * @return The end line-nr of the invocation (could be different from the begin line-nr if the invocation is written on more than 1 line)
     */
    public int getMatchInvocationEndLineNr() {
        return matchInvocationEndLineNr;
    }


    /**
     * From the moment that this method is called until {@link #registerEndOfMatchingInvocation} has been called,
     * argument matchers can be registered.
     *
     * @param lineNr The line number at which the matching invocation starts, i.e. the line number at which the performs, assertInvoked, etc.
     *               statement occurs.
     */
    public void registerStartOfMatchingInvocation(int lineNr) {
        acceptingArgumentMatchers = true;
        matchInvocationStartLineNr = lineNr;
        matchInvocationEndLineNr = lineNr;
    }


    /**
     * Clears the current argument matchers. After this method is called, {@link #registerStartOfMatchingInvocation} must
     * be called again to be able to register argument matchers.
     */
    public void registerEndOfMatchingInvocation() {
        argumentMatchers = new ArrayList<ArgumentMatcher>();
        acceptingArgumentMatchers = false;
    }
}
