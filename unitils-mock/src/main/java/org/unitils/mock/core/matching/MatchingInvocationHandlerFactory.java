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
package org.unitils.mock.core.matching;

import org.unitils.mock.core.BehaviorDefiningInvocations;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.matching.impl.AssertInvokedInSequenceVerifyingMatchingInvocationHandler;
import org.unitils.mock.core.matching.impl.AssertInvokedVerifyingMatchingInvocationHandler;
import org.unitils.mock.core.matching.impl.AssertNotInvokedVerifyingMatchingInvocationHandler;
import org.unitils.mock.core.matching.impl.BehaviorDefiningMatchingInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.report.ScenarioReport;

public class MatchingInvocationHandlerFactory {

    protected Scenario scenario;
    protected MockFactory mockFactory;
    protected ScenarioReport scenarioReport;


    public MatchingInvocationHandlerFactory(Scenario scenario, MockFactory mockFactory, ScenarioReport scenarioReport) {
        this.scenario = scenario;
        this.mockFactory = mockFactory;
        this.scenarioReport = scenarioReport;
    }


    public MatchingInvocationHandler createBehaviorDefiningMatchingInvocationHandler(MockBehavior mockBehavior, boolean oneTimeMatch, BehaviorDefiningInvocations behaviorDefiningInvocations) {
        return new BehaviorDefiningMatchingInvocationHandler(mockBehavior, oneTimeMatch, behaviorDefiningInvocations, mockFactory);
    }

    public MatchingInvocationHandler createAssertInvokedVerifyingMatchingInvocationHandler() {
        return new AssertInvokedVerifyingMatchingInvocationHandler(scenario, mockFactory, scenarioReport);
    }

    public MatchingInvocationHandler createAssertInvokedInSequenceVerifyingMatchingInvocationHandler() {
        return new AssertInvokedInSequenceVerifyingMatchingInvocationHandler(scenario, mockFactory, scenarioReport);
    }

    public MatchingInvocationHandler createAssertNotInvokedVerifyingMatchingInvocationHandler() {
        return new AssertNotInvokedVerifyingMatchingInvocationHandler(scenario, mockFactory, scenarioReport);
    }
}