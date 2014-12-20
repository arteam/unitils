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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.matching.impl.AssertInvokedVerifyingTimesMatchingInvocationHandler;
import org.unitils.mock.report.ScenarioReport;

import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class MatchingInvocationHandlerFactoryCreateAssertInvokedVerifyingTimesMatchingInvocationHandlerTest extends UnitilsJUnit4 {

    private MatchingInvocationHandlerFactory matchingInvocationHandlerFactory;

    @Dummy
    private Scenario scenario;
    @Dummy
    private MockFactory mockFactory;
    @Dummy
    private ScenarioReport scenarioReport;


    @Before
    public void initialize() {
        matchingInvocationHandlerFactory = new MatchingInvocationHandlerFactory(scenario, mockFactory, scenarioReport);
    }


    @Test
    public void createAssertInvokedVerifyingTimesMatchingInvocationHandler() {
        MatchingInvocationHandler result = matchingInvocationHandlerFactory.createAssertInvokedVerifyingTimesMatchingInvocationHandler(5);
        assertTrue(result instanceof AssertInvokedVerifyingTimesMatchingInvocationHandler);
        assertPropertyReflectionEquals("times", 5, result);
        assertPropertyReflectionEquals("scenario", scenario, result);
        assertPropertyReflectionEquals("mockFactory", mockFactory, result);
        assertPropertyReflectionEquals("scenarioReport", scenarioReport, result);
    }
}
