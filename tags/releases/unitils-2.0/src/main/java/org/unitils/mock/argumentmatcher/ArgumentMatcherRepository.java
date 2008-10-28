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


    /**
     * Adds an argument matcher.
     *
     * @param argumentMatcher The matcher, not null
     */
    public void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
        if (!acceptingArgumentMatchers) {
            throw new UnitilsException("Argument matchers cannot be used outside the context of a behavior definition or assert statement");
        }
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
     * Clears the current argument matchers. After this method is called, {@link #acceptArgumentMatchers()} must
     * be called again to be able to register argument matchers.
     */
    public void resetArgumentMatchers() {
        argumentMatchers.clear();
        acceptingArgumentMatchers = false;
    }


    /**
     * Enables the repository to accept argument matchers registrations. If not set,
     * an exception will be raised when the {@link #registerArgumentMatcher} is called.
     */
    public void acceptArgumentMatchers() {
        acceptingArgumentMatchers = true;
    }
}
