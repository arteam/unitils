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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.argumentmatcher.impl.NotNullArgumentMatcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InvocationMatcherTest extends UnitilsJUnit4 {

    protected Method invocationMatcherTesterMethodOnNothing;
    protected Method invocationMatcherTesterMethodOnInt;
    protected Method invocationMatcherTesterMethodOnObject;
    protected Method invocationMatcherTesterMethodOnTwoObjects;

    protected InvocationMatcher invocationMatcherOnNothing;
    protected InvocationMatcher invocationMatcherOnInt;
    protected InvocationMatcher invocationMatcherOnObject;
    protected InvocationMatcher invocationMatcherOnTwoObjects;

    protected List<?> noArguments;
    protected List<?> intArgument;
    protected List<?> objectArgument;
    protected List<?> twoObjectArguments;

    @Before
    public void setup() {
        final Method[] methods = InvocationMatcherTester.class.getMethods();
        for (Method method : methods) {
            if ("doSomethingWithNothing".equals(method.getName())) {
                invocationMatcherTesterMethodOnNothing = method;
            } else if ("doSomethingWithInt".equals(method.getName())) {
                invocationMatcherTesterMethodOnInt = method;
            } else if ("doSomethingWithObject".equals(method.getName())) {
                invocationMatcherTesterMethodOnObject = method;
            } else if ("doSomethingWithTwoObjects".equals(method.getName())) {
                invocationMatcherTesterMethodOnTwoObjects = method;
            }
        }
        invocationMatcherOnNothing = new InvocationMatcher(invocationMatcherTesterMethodOnNothing);
        invocationMatcherOnInt = new InvocationMatcher(invocationMatcherTesterMethodOnInt, new NotNullArgumentMatcher());
        invocationMatcherOnObject = new InvocationMatcher(invocationMatcherTesterMethodOnObject, new NotNullArgumentMatcher());
        invocationMatcherOnTwoObjects = new InvocationMatcher(invocationMatcherTesterMethodOnTwoObjects, new NotNullArgumentMatcher(), new NotNullArgumentMatcher());

        noArguments = Collections.emptyList();
        intArgument = Arrays.asList(new int[]{0});
        objectArgument = Arrays.asList(new Object[]{new Object()});
        twoObjectArguments = Arrays.asList(new Object[]{new Object(), new Object()});
    }

    @Test
    public void testInvocationMatcherWithDifferentMethod() {
        assertFalse(invocationMatcherOnNothing.matches(new Invocation(null, null, invocationMatcherTesterMethodOnInt, noArguments, null, null)));
    }

    @Test
    public void testInvocationMatcherWithDifferentNumberOfParams() {
        assertFalse(invocationMatcherOnNothing.matches(new Invocation(null, null, invocationMatcherTesterMethodOnNothing, intArgument, null, null)));
        assertFalse(invocationMatcherOnNothing.matches(new Invocation(null, null, invocationMatcherTesterMethodOnNothing, twoObjectArguments, null, null)));
    }

    @Test
    public void testInvocationMatcherWithCorrectParams() {
        assertTrue(invocationMatcherOnNothing.matches(new Invocation(null, null, invocationMatcherTesterMethodOnNothing, noArguments, null, null)));
        assertTrue(invocationMatcherOnInt.matches(new Invocation(null, null, invocationMatcherTesterMethodOnInt, objectArgument, null, null)));
        assertTrue(invocationMatcherOnObject.matches(new Invocation(null, null, invocationMatcherTesterMethodOnObject, objectArgument, null, null)));
        assertTrue(invocationMatcherOnTwoObjects.matches(new Invocation(null, null, invocationMatcherTesterMethodOnTwoObjects, twoObjectArguments, null, null)));
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static class InvocationMatcherTester {
        public void doSomethingWithNothing() {
        }

        public void doSomethingWithInt(int i) {
        }

        public void doSomethingWithObject(Object o) {
        }

        public void doSomethingWithTwoObjects(Object o1, Object o2) {
        }
    }
}
