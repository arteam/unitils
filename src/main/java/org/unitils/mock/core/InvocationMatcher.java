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

import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * A method invocation matcher that uses a given <code>Method</code> and a list of {@link ArgumentMatcher}s to match an executed <code>Method</code> and its parameters.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InvocationMatcher {

    private Method method;

    private List<ArgumentMatcher> argumentMatchers;

    /**
     * Constructor.
     *
     * @param method           The <code>Method</code> which needs to be matched. Not null.
     * @param argumentMatchers The {@link org.unitils.mock.argumentmatcher.ArgumentMatcher}s that need to be used to match the {@link Invocation}s arguments. The size of the list must be equals to the number of parameters of the given <code>Method</code>.
     */
    public InvocationMatcher(Method method, ArgumentMatcher... argumentMatchers) {
        this(method, asList(argumentMatchers));
    }


    public InvocationMatcher(Method method, List<ArgumentMatcher> argumentMatchers) {
        this.method = method;
        this.argumentMatchers = argumentMatchers;
        if (method.getParameterTypes().length != argumentMatchers.size()) {
            throw new IllegalArgumentException("The number of argument matchers does not match the number of arguments of the given method. Number of argument matchers: " + argumentMatchers.size() + ". Number of arguments " + method.getParameterTypes().length);
        }
    }


    /**
     * Returns whether or not the given {@link Invocation} matches this object's predefined <code>Method</code> and arguments.
     *
     * @param invocation the {@link Invocation} to match.
     * @return true when given {@link Invocation} matches, false otherwise.
     */
    public boolean matches(Invocation invocation) {
        if (!method.equals(invocation.getMethod())) {
            return false;
        }
        List<?> arguments = invocation.getArguments();
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

    public Method getMethod() {
        return method;
    }
}
