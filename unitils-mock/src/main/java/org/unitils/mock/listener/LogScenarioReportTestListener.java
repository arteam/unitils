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
import org.unitils.mock.core.MockAssertionError;
import org.unitils.mock.report.ScenarioReport;

/**
 * @author Tim Ducheyne
 */
public class LogScenarioReportTestListener extends TestListener {

    protected static Log logger = LogFactory.getLog(LogScenarioReportTestListener.class);

    private ScenarioReport scenarioReport;


    public LogScenarioReportTestListener(ScenarioReport scenarioReport) {
        this.scenarioReport = scenarioReport;
    }


    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        if (testThrowable == null || testThrowable instanceof MockAssertionError) {
            // only log the scenario when there an exception occurred
            return;
        }
        String report = scenarioReport.createReport();
        logger.error("\n\n" + report);
    }
}
