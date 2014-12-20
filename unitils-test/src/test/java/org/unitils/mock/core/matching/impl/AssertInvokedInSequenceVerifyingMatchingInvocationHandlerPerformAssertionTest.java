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
import org.unitils.mock.core.MatchingInvocation;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class AssertInvokedInSequenceVerifyingMatchingInvocationHandlerPerformAssertionTest extends UnitilsJUnit4 {

    private AssertInvokedInSequenceVerifyingMatchingInvocationHandler assertInvokedInSequenceVerifyingMatchingInvocationHandler;

    private Mock<Scenario> scenarioMock;
    private Mock<MatchingInvocation> matchingInvocationMock;
    private Mock<ObservedInvocation> observedInvocationMock;
    private Mock<ObservedInvocation> outOfSequenceInvocationMock;


    @Before
    public void initialize() throws Exception {
        assertInvokedInSequenceVerifyingMatchingInvocationHandler = new AssertInvokedInSequenceVerifyingMatchingInvocationHandler(scenarioMock.getMock(), null, null);

        Method testMethod1 = TestInterface.class.getMethod("testMethod1");
        Method testMethod2 = TestInterface.class.getMethod("testMethod2");
        Method testMethod3 = TestInterface.class.getMethod("testMethod3");
        matchingInvocationMock.returns(testMethod1).getMethod();
        observedInvocationMock.returns(testMethod2).getMethod();
        observedInvocationMock.returns(new StackTraceElement("class2", "method2", "file2", 222)).getInvokedAt();
        outOfSequenceInvocationMock.returns(testMethod3).getMethod();
        outOfSequenceInvocationMock.returns(new StackTraceElement("class3", "method3", "file3", 333)).getInvokedAt();
    }


    @Test
    public void nullWhenInvocationFoundInSequence() {
        scenarioMock.returns(observedInvocationMock).verifyInvocation(matchingInvocationMock.getMock());
        scenarioMock.returns(null).verifyInvocationInSequence(observedInvocationMock.getMock());

        String result = assertInvokedInSequenceVerifyingMatchingInvocationHandler.performAssertion(matchingInvocationMock.getMock());
        assertNull(result);
    }

    @Test
    public void errorMessageWhenNoInvocationFound() {
        scenarioMock.returns(null).verifyInvocation(matchingInvocationMock.getMock());

        String result = assertInvokedInSequenceVerifyingMatchingInvocationHandler.performAssertion(matchingInvocationMock.getMock());
        assertEquals("Expected invocation of TestInterface.testMethod1(), but it didn't occur.", result);
    }

    @Test
    public void errorMessageWhenInvocationFoundOutOfSequence() {
        scenarioMock.returns(observedInvocationMock).verifyInvocation(matchingInvocationMock.getMock());
        scenarioMock.returns(outOfSequenceInvocationMock).verifyInvocationInSequence(observedInvocationMock.getMock());

        String result = assertInvokedInSequenceVerifyingMatchingInvocationHandler.performAssertion(matchingInvocationMock.getMock());
        assertEquals("Invocation of TestInterface.testMethod1() invoked at class2.method2(file2:222)\n" +
                "was expected to be performed after TestInterface.testMethod3() invoked at class3.method3(file3:333)\n" +
                "but actually occurred before it.", result);
    }


    private interface TestInterface {

        void testMethod1();

        void testMethod2();

        void testMethod3();
    }
}