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
package org.unitils.mock.syntax;

import org.unitils.mock.action.Action;
import org.unitils.mock.action.impl.ExceptionThrowingAction;
import org.unitils.mock.action.impl.ValueReturningAction;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.Invocation;
import org.unitils.mock.core.InvocationMatcher;
import org.unitils.mock.core.MockBehavior;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockBehaviorBuilder {

    private Action action;

    private InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();

    private static MockBehaviorBuilder instance;

    public static MockBehaviorBuilder getInstance() {
        if (instance == null) {
            instance = new MockBehaviorBuilder();
        }
        return instance;
    }


    private MockBehaviorBuilder() {
    }


    public void registerReturnValue(Object returnValue) {
        registerPerformedAction(new ValueReturningAction(returnValue));
    }


    public void registerThrownException(Throwable exception) {
        registerPerformedAction(new ExceptionThrowingAction(exception));
    }


    public void registerPerformedAction(Action action) {
        this.action = action;
    }


    public void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
        invocationMatcherBuilder.registerArgumentMatcher(argumentMatcher);
    }


    public MockBehavior createMockBehavior(Invocation invocation) {
        InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher(invocation);
        MockBehavior mockBehavior = new MockBehavior(invocationMatcher, action);
        action = null;
        return mockBehavior;
    }


}
