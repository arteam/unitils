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
package org.unitils.mock.core.matching.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.MatchingInvocation;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.report.ScenarioReport;

import java.lang.reflect.Method;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class AssertVerifyingMatchingInvocationHandlerHandleInvocationTest extends UnitilsJUnit4 {

    private PartialMock<AssertVerifyingMatchingInvocationHandler> assertVerifyingMatchingInvocationHandler;

    private Mock<Scenario> scenarioMock;
    private Mock<MockFactory> mockServiceMock;
    private Mock<ScenarioReport> scenarioReportMock;
    private MatchingInvocation matchingInvocation;
    @Dummy
    private Mock<Map> chainedMock;
    @Dummy
    private Map chainedProxy;


    @Before
    public void initialize() throws Exception {
        AssertVerifyingMatchingInvocationHandler mockPrototype = new AssertInvokedInSequenceVerifyingMatchingInvocationHandler(scenarioMock.getMock(), mockServiceMock.getMock(), scenarioReportMock.getMock());
        assertVerifyingMatchingInvocationHandler = MockUnitils.createPartialMock(mockPrototype, this);

        StackTraceElement element1 = new StackTraceElement("class1", "method1", "file1", 111);
        StackTraceElement element2 = new StackTraceElement("class2", "method2", "file2", 222);
        StackTraceElement[] stackTrace = new StackTraceElement[]{element1, element2};

        Method method = MyInterface.class.getMethod("method");
        ProxyInvocation proxyInvocation = new ProxyInvocation("mock", null, method, emptyList(), emptyList(), stackTrace);
        matchingInvocation = new MatchingInvocation(proxyInvocation, null);

        scenarioReportMock.returns("scenario report").createReport();
    }


    @Test
    public void mockChainingStartedWhenAssertionOk() {
        assertVerifyingMatchingInvocationHandler.returns(null).performAssertion(matchingInvocation);
        mockServiceMock.returns(chainedMock).createChainedMock(matchingInvocation);
        assertVerifyingMatchingInvocationHandler.returns(chainedProxy).performChainedAssertion(chainedMock);

        Object result = assertVerifyingMatchingInvocationHandler.getMock().handleInvocation(matchingInvocation);
        assertSame(chainedProxy, result);
    }

    @Test
    public void assertionErrorWhenAssertionFailed() {
        assertVerifyingMatchingInvocationHandler.returns("assertion failed").performAssertion(matchingInvocation);
        try {
            assertVerifyingMatchingInvocationHandler.getMock().handleInvocation(matchingInvocation);
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("assertion failed\n" +
                    "Asserted at class1.method1(file1:111)\n" +
                    "\n" +
                    "scenario report", e.getMessage());
        }
    }

    @Test
    public void nullProxyWhenUnableToCreateChainedMock() {
        assertVerifyingMatchingInvocationHandler.returns(null).performAssertion(matchingInvocation);
        mockServiceMock.returns(null).createChainedMock(matchingInvocation);

        Object result = assertVerifyingMatchingInvocationHandler.getMock().handleInvocation(matchingInvocation);
        assertNull(result);
    }


    private static interface MyInterface {

        Map method();
    }
}
