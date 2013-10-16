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
import org.unitils.mock.report.impl.SuggestedAssertsReport;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.mock.MockUnitils.getObservedInvocations;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SuggestedAssertsReportIntegrationTest extends UnitilsJUnit4 {

    private SuggestedAssertsReport suggestedAssertsReport;

    private Mock<TestInterface> testMock;
    private TestInterface testProxy;
    private String testDataStr = "someString";


    @Before
    public void initialize() {
        suggestedAssertsReport = new SuggestedAssertsReport();
        testProxy = testMock.getMock();
    }


    @Test
    public void simpleValues() {
        testProxy.testMethodString("someValue");
        testProxy.testMethodInt(2);
        testProxy.testMethodInteger(3);

        String report = suggestedAssertsReport.createReport(this, getObservedInvocations());
        assertTrue(report.contains("testMock.assertInvoked().testMethodString(\"someValue\");"));
        assertTrue(report.contains("testMock.assertInvoked().testMethodInt(2);"));
        assertTrue(report.contains("testMock.assertInvoked().testMethodInteger(3);"));
    }

    @Test
    public void useFieldNamesInReport() {
        testProxy.testMethodString(testDataStr);

        String report = suggestedAssertsReport.createReport(this, getObservedInvocations());
        assertTrue(report.contains("testMock.assertInvoked().testMethodString(testDataStr);"));
    }

    @Test
    public void objectsThatAreNotSimpleValuesAreReplacedByNull() {
        testProxy.testMethodObject(new ArrayList<String>());

        String report = suggestedAssertsReport.createReport(this, getObservedInvocations());
        assertTrue(report.contains("testMock.assertInvoked().testMethodObject(null);"));
    }

    @Test
    public void onlySuggestAssertsForVoidMethods() {
        testProxy.testMethodReturnsString();

        String report = suggestedAssertsReport.createReport(this, getObservedInvocations());
        assertFalse(report.contains("testMock.assertInvoked().testMethodReturnsString()"));
    }


    public static interface TestInterface {

        String testMethodReturnsString();

        void testMethodString(String arg1);

        void testMethodInt(int arg1);

        void testMethodInteger(Integer arg1);

        void testMethodObject(List<String> arg1);
    }
}
