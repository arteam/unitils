/*
 * Copyright 2006-2007,  Unitils.org
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

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.core.MockObject;
import static org.unitils.mock.core.MockObject.getCurrentScenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for the report that shows the suggested assert statements.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SuggestedAssertsReportTest {

    /* class under test */
    private SuggestedAssertsReport suggestedAssertsReport;

    /* Test mock that uses the scenario */
    private Mock<TestInterface> testMock;

    /* Proxy of the testMock */
    private TestInterface testProxy;

    /* Test data, which is a field of this class: if used, the suggested assert must refer to this field */
    private String testDataStr = "someString";


    /**
     * Initializes the test.
     */
    @Before
    public void setUp() {
        suggestedAssertsReport = new SuggestedAssertsReport();
        testMock = new MockObject<TestInterface>("testMock", TestInterface.class, this);
        testProxy = testMock.getMock();
    }


    /**
     * Simple values like strings and integers must be showed directly in the suggested assert
     */
    @Test
    public void simpleValues() {
        testProxy.testMethodString("someValue");
        testProxy.testMethodInt(2);
        testProxy.testMethodInteger(3);

        String report = suggestedAssertsReport.createReport(this, getCurrentScenario().getObservedInvocations());

        assertTrue(report.contains("testMock.assertInvoked().testMethodString(\"someValue\");"));
        assertTrue(report.contains("testMock.assertInvoked().testMethodInt(2);"));
        assertTrue(report.contains("testMock.assertInvoked().testMethodInteger(3);"));
    }

    /**
     * If an argument refers to the same object as a field of the test object, the test object's field must be used
     */
    @Test
    public void testObjectFields() {
        testProxy.testMethodString(testDataStr);

        String report = suggestedAssertsReport.createReport(this, getCurrentScenario().getObservedInvocations());

        assertTrue(report.contains("testMock.assertInvoked().testMethodString(testDataStr);"));
    }

    /**
     * Objects that are not simple values are replaced by null in the suggested assert
     */
    @Test
    public void objects() {
        testProxy.testMethodObject(new ArrayList<String>());

        String report = suggestedAssertsReport.createReport(this, getCurrentScenario().getObservedInvocations());

        assertTrue(report.contains("testMock.assertInvoked().testMethodObject(null);"));
    }

    /**
     * Methods that return something are not included in the report
     */
    @Test
    public void onlySuggestAssertsForVoids() {
        testProxy.testMethodReturnsString();

        String report = suggestedAssertsReport.createReport(this, getCurrentScenario().getObservedInvocations());

        assertFalse(report.contains("testMock.assertInvoked().testMethodReturnsString()"));
    }

    /**
     * Methods which have been stubbed are not included in the report
     */
    @Test
    public void dontSuggestAssertsForStubbedMethods() {
        testMock.raises(UnitilsException.class).testMethodString("someValue");
        try {
            testProxy.testMethodString("someValue");
        } catch (UnitilsException e) {
            // Expected flow
        }
        testProxy.testMethodString("otherValue");

        String report = suggestedAssertsReport.createReport(this, getCurrentScenario().getObservedInvocations());

        assertFalse(report.contains("testMock.assertInvoked().testMethodString(\"someValue\");"));
        assertTrue(report.contains("testMock.assertInvoked().testMethodString(\"otherValue\");"));
    }

    public static interface TestInterface {

        public String testMethodReturnsString();

        public void testMethodString(String arg1);

        public void testMethodInt(int arg1);

        public void testMethodInteger(Integer arg1);

        public void testMethodObject(List<String> arg1);
    }

}
