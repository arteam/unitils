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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestInstance;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.report.ScenarioReport;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class LogScenarioReportTestListenerAfterTestMethodTest extends UnitilsJUnit4 {

    private LogScenarioReportTestListener logScenarioReportTestListener;

    private Mock<TestInstance> testInstanceMock;
    private Mock<ScenarioReport> scenarioReportMock;
    @Dummy
    private Properties testObject;


    @Before
    public void initialize() {
        logScenarioReportTestListener = new LogScenarioReportTestListener(scenarioReportMock.getMock());

        testInstanceMock.returns(testObject).getTestObject();
    }


    @Test
    public void logScenarioReportWhenException() {
        logScenarioReportTestListener.afterTestMethod(testInstanceMock.getMock(), new NullPointerException());
        scenarioReportMock.assertInvoked().createReport();
    }

    @Test
    public void ignoreWhenNoException() {
        logScenarioReportTestListener.afterTestMethod(testInstanceMock.getMock(), null);
        scenarioReportMock.assertNotInvoked().createReport();
    }
}