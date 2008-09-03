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

    /* The current argument matchers */
    private static List<ArgumentMatcher> argumentMatchers = new ArrayList<ArgumentMatcher>();


    /**
     * Adds an argument matcher.
     *
     * @param argumentMatcher The matcher, not null
     */
    public static void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
        argumentMatchers.add(argumentMatcher);
    }


    /**
     * Gets the current argument matchers.
     *
     * @return The matchers, not null
     */
    public static List<ArgumentMatcher> getArgumentMatchers() {
        return argumentMatchers;
    }


    /**
     * Clears the current argument matchers.
     */
    public static void resetArgumentMatchers() {
        argumentMatchers.clear();
    }
}
