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

import org.unitils.mock.syntax.InvocationMatcherBuilder;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class AssertInvokedMethodInterceptor<T> extends BaseMethodInterceptor<T> {

    private InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();

    private boolean checkIfInvoked;


    public AssertInvokedMethodInterceptor(Scenario scenario, MockObject<T> mockObject, boolean checkIfInvoked) {
        super(mockObject, scenario);
        this.checkIfInvoked = checkIfInvoked;
    }


    public Object handleInvocation(Invocation invocation) throws Throwable {
        invocationMatcherBuilder.registerInvokedMethod(invocation);
        InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher();
        invocationMatcherBuilder.reset();

        Scenario scenario = getScenario();
        if (checkIfInvoked) {
            scenario.assertInvoked(invocationMatcher);
        } else {
            scenario.assertNotInvoked(invocationMatcher);
        }
        return null;
    }

}
