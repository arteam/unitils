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

package org.unitils.mock.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.TestInstance;
import org.unitils.core.TestListener;
import org.unitils.mock.core.Scenario;

/**
 * @author Tim Ducheyne
 */
public class LogScenarioReportTestListener extends TestListener {

    protected static Log logger = LogFactory.getLog(LogScenarioReportTestListener.class);

    private Scenario scenario;


    public LogScenarioReportTestListener(Scenario scenario) {
        this.scenario = scenario;
    }


    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        if (testThrowable != null) {
            logger.error("\n\n" + scenario.createFullReport());
        }
    }
}
