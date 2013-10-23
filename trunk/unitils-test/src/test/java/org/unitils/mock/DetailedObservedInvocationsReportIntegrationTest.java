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
package org.unitils.mock;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.report.impl.DetailedObservedInvocationsReport;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.junit.Assert.assertTrue;
import static org.unitils.mock.MockUnitils.getObservedInvocations;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DetailedObservedInvocationsReportIntegrationTest extends UnitilsJUnit4 {

    private DetailedObservedInvocationsReport detailedObservedInvocationsReport;

    private Mock<TestInterface> testMock;


    @Before
    public void initialize() {
        detailedObservedInvocationsReport = new DetailedObservedInvocationsReport();
    }


    @Test
    public void twoMockInvocations() {
        testMock.getMock().testMethod1("value1");
        testMock.getMock().testMethod2();

        String result = detailedObservedInvocationsReport.createReport(getObservedInvocations(), this);
        // method with result and argument
        assertTrue(result.contains("1. testMock.testMethod1(\"value1\") -> null"));
        assertTrue(result.contains("- Observed at org.unitils.mock.DetailedObservedInvocationsReportIntegrationTest.twoMockInvocations(DetailedObservedInvocationsReportIntegrationTest.java:"));
        assertTrue(result.contains("- No behavior defined, returned default value."));
        // void method
        assertTrue(result.contains("2. testMock.testMethod2()"));
        assertTrue(result.contains("- Observed at org.unitils.mock.DetailedObservedInvocationsReportIntegrationTest.twoMockInvocations(DetailedObservedInvocationsReportIntegrationTest.java:"));
    }

    @Test
    public void emptyWhenNoInvocations() {
        String result = detailedObservedInvocationsReport.createReport(getObservedInvocations(), this);
        assertTrue(isEmpty(result));
    }

    @Test
    public void useShortNameWhenArgumentValueLongerThan50() {
        testMock.getMock().testMethod1("01234567890123456789012345678901234567890123456789x");

        String result = detailedObservedInvocationsReport.createReport(getObservedInvocations(), this);
        assertTrue(result.contains("1. testMock.testMethod1(string1) -> null"));
        assertTrue(result.contains("- string1 -> \"01234567890123456789012345678901234567890123456789x\""));
    }

    @Test
    public void useShortNameWhenResultValueLongerThan50() {
        testMock.returns("01234567890123456789012345678901234567890123456789x").testMethod1(null);
        testMock.getMock().testMethod1(null);

        String result = detailedObservedInvocationsReport.createReport(getObservedInvocations(), this);
        assertTrue(result.contains("1. testMock.testMethod1(null) -> string1"));
        assertTrue(result.contains("- string1 -> \"01234567890123456789012345678901234567890123456789x\""));
        assertTrue(result.contains("- Observed at org.unitils.mock.DetailedObservedInvocationsReportIntegrationTest.useShortNameWhenResultValueLongerThan50(DetailedObservedInvocationsReportIntegrationTest.java:"));
        assertTrue(result.contains("- Behavior defined at org.unitils.mock.DetailedObservedInvocationsReportIntegrationTest.useShortNameWhenResultValueLongerThan50(DetailedObservedInvocationsReportIntegrationTest.java:"));
    }

    @Test
    public void useSameNameWhenLargeValueInResultAndArgument() {
        List<String> largeValue = asList("11111", "222222", "333333", "444444", "555555", "666666");
        testMock.returns(largeValue).testMethod3(largeValue);
        testMock.getMock().testMethod3(largeValue);

        String result = detailedObservedInvocationsReport.createReport(getObservedInvocations(), this);
        assertTrue(result.contains("1. testMock.testMethod3(list1) -> list1"));
        assertTrue(result.contains("- list1 -> [\"11111\", \"222222\", \"333333\", \"444444\", \"555555\", \"666666\"]"));
        assertTrue(result.contains("- Observed at org.unitils.mock.DetailedObservedInvocationsReportIntegrationTest.useSameNameWhenLargeValueInResultAndArgument(DetailedObservedInvocationsReportIntegrationTest.java:"));
        assertTrue(result.contains("- Behavior defined at org.unitils.mock.DetailedObservedInvocationsReportIntegrationTest.useSameNameWhenLargeValueInResultAndArgument(DetailedObservedInvocationsReportIntegrationTest.java:"));
    }


    public static interface TestInterface {

        String testMethod1(String arg1);

        void testMethod2();

        List<String> testMethod3(List<String> list);
    }
}