/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.mock.core.matching.impl;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.proxy.ProxyInvocation;

import java.util.List;

public class AssertInvokedVerifyingMatchingInvocationHandler implements MatchingInvocationHandler {

    /* The scenario that will record all observed invocations */
    protected Scenario scenario;


    public AssertInvokedVerifyingMatchingInvocationHandler(Scenario scenario) {
        this.scenario = scenario;
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation, List<ArgumentMatcher> argumentMatchers) throws Throwable {
        BehaviorDefiningInvocation behaviorDefiningInvocation = new BehaviorDefiningInvocation(proxyInvocation, null, argumentMatchers);
        scenario.assertInvoked(behaviorDefiningInvocation);
        return null;
    }

}