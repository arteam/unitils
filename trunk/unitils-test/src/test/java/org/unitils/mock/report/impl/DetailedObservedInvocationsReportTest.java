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
package org.unitils.mock.report.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the usage of test fields in mock invocations. The names of the fields should be shown in the report (same as for large value).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DetailedObservedInvocationsReportTest {

    /* class under test */
    private DetailedObservedInvocationsReport detailedObservedInvocationsReport;

    private Mock<TestInterface> testMock;


    @Before
    public void initialize() {
        detailedObservedInvocationsReport = new DetailedObservedInvocationsReport(this);
        // todo td implement
//        testMock = new MockObject<TestInterface>("testMock", TestInterface.class, this);
    }


    @Test
    public void twoMockInvocations() {
        testMock.getMock().testMethod1("value1");
        testMock.getMock().testMethod2();

        // todo td implement
//        String result = detailedObservedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;

        // method with result and argument
        assertTrue(result.contains("1. testMock.testMethod1(\"value1\") -> null"));
        assertTrue(result.contains("- Observed at org.unitils.mock.report.impl.DetailedObservedInvocationsReportTest.twoMockInvocations(DetailedObservedInvocationsReportTest.java:"));
        assertTrue(result.contains("- No behavior defined, returned default value."));
        // void method
        assertTrue(result.contains("2. testMock.testMethod2()"));
        assertTrue(result.contains("- Observed at org.unitils.mock.report.impl.DetailedObservedInvocationsReportTest.twoMockInvocations(DetailedObservedInvocationsReportTest.java:"));
    }

    @Test
    public void noInvocations() {
        // todo td implement
//        String result = detailedObservedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;
        assertTrue(isEmpty(result));
    }

    @Test
    public void largeArgumentValue_shouldBeReplacedByShortName() {
        testMock.getMock().testMethod1("012345678901234567891");

        // todo td implement
//        String result = detailedObservedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;
        assertTrue(result.contains("1. testMock.testMethod1(string1) -> null"));
        assertTrue(result.contains("- string1 -> \"012345678901234567891\""));
    }

    @Test
    public void largeResultValue_shouldBeReplacedByShortName() {
        testMock.returns("012345678901234567891").testMethod1(null);
        testMock.getMock().testMethod1(null);

        // todo td implement
//        String result = detailedObservedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;
        assertTrue(result.contains("1. testMock.testMethod1(null) -> string1"));
        assertTrue(result.contains("- string1 -> \"012345678901234567891\""));
        assertTrue(result.contains("- Observed at org.unitils.mock.report.impl.DetailedObservedInvocationsReportTest.largeResultValue_shouldBeReplacedByShortName(DetailedObservedInvocationsReportTest.java:"));
        assertTrue(result.contains("- Behavior defined at org.unitils.mock.report.impl.DetailedObservedInvocationsReportTest.largeResultValue_shouldBeReplacedByShortName(DetailedObservedInvocationsReportTest.java:"));
    }

    @Test
    public void sameInstanceLargeValueInResultAndArgument_shouldUseSameName() {
        List<String> largeValue = asList("11111", "222222", "333333");
        testMock.returns(largeValue).testMethod3(largeValue);
        testMock.getMock().testMethod3(largeValue);

        // todo td implement
//        String result = detailedObservedInvocationsReport.createReport(getCurrentScenario().getObservedInvocations());
        String result = null;
        assertTrue(result.contains("1. testMock.testMethod3(list1) -> list1"));
        assertTrue(result.contains("- list1 -> [\"11111\", \"222222\", \"333333\"]"));
        assertTrue(result.contains("- Observed at org.unitils.mock.report.impl.DetailedObservedInvocationsReportTest.sameInstanceLargeValueInResultAndArgument_shouldUseSameName(DetailedObservedInvocationsReportTest.java:"));
        assertTrue(result.contains("- Behavior defined at org.unitils.mock.report.impl.DetailedObservedInvocationsReportTest.sameInstanceLargeValueInResultAndArgument_shouldUseSameName(DetailedObservedInvocationsReportTest.java:"));
        // make sure the list is only outputted once
        assertFalse(result.contains("- list1 -> [\"11111\", \"222222\", \"333333\"]\n" +
                "- list1 -> [\"11111\", \"222222\", \"333333\"]"));
    }


    public static interface TestInterface {

        String testMethod1(String arg1);

        void testMethod2();

        List<String> testMethod3(List<String> list);
    }

}