/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.dummy.DummyObjectFactory;
import org.unitilsnew.core.engine.Unitils;

import static org.unitils.mock.core.proxy.StackTraceUtils.getInvocationStackTrace;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockUnitils {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(MockUnitils.class);

    protected static DummyObjectFactory dummyObjectFactory = Unitils.getInstanceOfType(DummyObjectFactory.class);


    public static void assertNoMoreInvocations() {
        MockObject.getCurrentScenario().assertNoMoreInvocations(getInvocationStackTrace(MockUnitils.class, false));
    }


    public static <T> T createDummy(Class<T> type) {
        return dummyObjectFactory.createDummy(type);
    }


    public static void logFullScenarioReport() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\n" + scenario.createFullReport());
        }
    }

    public static void logObservedScenario() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\nObserved scenario:\n\n" + scenario.createObservedInvocationsReport());
        }
    }

    public static void logDetailedObservedScenario() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\nDetailed observed scenario:\n\n" + scenario.createDetailedObservedInvocationsReport());
        }
    }

    public static void logSuggestedAsserts() {
        Scenario scenario = getScenario();
        if (scenario != null) {
            logger.info("\n\nSuggested assert statements:\n\n" + scenario.createSuggestedAssertsReport());
        }
    }


    private static Scenario getScenario() {
        return MockObject.getCurrentScenario();
    }
}
