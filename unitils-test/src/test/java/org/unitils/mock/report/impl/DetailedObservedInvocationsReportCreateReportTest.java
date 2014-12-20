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
import org.unitils.UnitilsJUnit4;
import org.unitils.core.util.ObjectFormatter;
import org.unitils.mock.Mock;
import org.unitils.mock.core.BehaviorDefiningInvocation;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.mock.mockbehavior.impl.DefaultValueReturningMockBehavior;
import org.unitils.mock.mockbehavior.impl.ExceptionThrowingMockBehavior;
import org.unitils.mock.mockbehavior.impl.OriginalBehaviorInvokingMockBehavior;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class DetailedObservedInvocationsReportCreateReportTest extends UnitilsJUnit4 {

    private DetailedObservedInvocationsReport detailedObservedInvocationsReport;

    private Mock<ObservedInvocation> observedInvocationMock1;
    private Mock<ObservedInvocation> observedInvocationMock2;
    private Mock<BehaviorDefiningInvocation> behaviorDefiningInvocationMock;
    private TestClass testObject;


    @Before
    public void initialize() throws Exception {
        detailedObservedInvocationsReport = new DetailedObservedInvocationsReport(new ObjectFormatter(), 10);

        testObject = new TestClass();
        Method testMethod1 = TestClass.class.getMethod("testMethod1", String.class, String.class, Properties.class);
        Method testMethod2 = TestClass.class.getMethod("testMethod2");
        StackTraceElement invokedAt1 = new StackTraceElement("class1", "method1", "file1", 111);
        StackTraceElement invokedAt2 = new StackTraceElement("class2", "method2", "file2", 222);
        StackTraceElement invokedAt3 = new StackTraceElement("class3", "method3", "file3", 333);

        observedInvocationMock1.returns("proxy1").getProxyName();
        observedInvocationMock2.returns("proxy2").getProxyName();
        observedInvocationMock1.returns(testMethod1).getMethod();
        observedInvocationMock2.returns(testMethod2).getMethod();
        observedInvocationMock1.returns(invokedAt1).getInvokedAt();
        observedInvocationMock2.returns(invokedAt2).getInvokedAt();
        behaviorDefiningInvocationMock.returns(invokedAt3).getInvokedAt();
    }


    @Test
    public void createReport() {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>("a", "a", String.class));
        arguments.add(new Argument<String>("b", "b", String.class));
        arguments.add(new Argument<Properties>(new Properties(), new Properties(), Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns("xxx").getResultAtInvocationTime();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(\"a\", \"b\", {})\n" +
                "- Observed at class1.method1(file1:111)\n" +
                "\n" +
                "2. proxy2.testMethod2() -> \"xxx\"\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "\n", result);
    }

    @Test
    public void behaviorDefined() {
        observedInvocationMock2.returns(behaviorDefiningInvocationMock).getBehaviorDefiningInvocation();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy2.testMethod2() -> null\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "- Behavior defined at class3.method3(file3:333)\n" +
                "\n", result);
    }

    @Test
    public void noBehaviorDefinedOriginalBehavior() {
        observedInvocationMock2.returns(new OriginalBehaviorInvokingMockBehavior()).getMockBehavior();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy2.testMethod2() -> null\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "- No behavior defined, executed original method behavior.\n" +
                "\n", result);
    }

    @Test
    public void noBehaviorDefinedReturnedDefaultValue() {
        observedInvocationMock2.returns(new DefaultValueReturningMockBehavior()).getMockBehavior();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy2.testMethod2() -> null\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "- No behavior defined, returned default value.\n" +
                "\n", result);
    }

    @Test
    public void noBehaviorDefinedExecutedDefaultBehavior() {
        observedInvocationMock2.returns(new ExceptionThrowingMockBehavior(null, null)).getMockBehavior();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy2.testMethod2() -> null\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "- No behavior defined, executed default behavior.\n" +
                "\n", result);
    }

    @Test
    public void nullValues() {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>(null, null, String.class));
        arguments.add(new Argument<String>(null, null, String.class));
        arguments.add(new Argument<String>(null, null, String.class));
        observedInvocationMock1.returns(arguments).getArguments();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), null);
        assertEquals("1. proxy1.testMethod1(null, null, null)\n" +
                "- Observed at class1.method1(file1:111)\n" +
                "\n" +
                "2. proxy2.testMethod2() -> null\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "\n", result);
    }

    @Test
    public void largeValuesAreReplacedByNames() {
        observedInvocationMock1.returnsAll("a", "b", new Properties()).getArguments();
        Properties largeProperties = new Properties();
        largeProperties.put("value", "large property");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>("1234567890x", "1234567890x", String.class));
        arguments.add(new Argument<String>("1234567890y", "1234567890y", String.class));
        arguments.add(new Argument<Properties>(largeProperties, largeProperties, Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns("1234567890z").getResultAtInvocationTime();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(string1, string2, properties1)\n" +
                "- string1 -> \"1234567890x\"\n" +
                "- string2 -> \"1234567890y\"\n" +
                "- properties1 -> {\"value\"=\"large property\"}\n" +
                "- Observed at class1.method1(file1:111)\n" +
                "\n" +
                "2. proxy2.testMethod2() -> string3\n" +
                "- string3 -> \"1234567890z\"\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "\n", result);
    }

    @Test
    public void sameNameUsedForEqualLargeValue() {
        observedInvocationMock1.returnsAll("a", "b", new Properties()).getArguments();
        String largeValue = "1234567890x";
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>(largeValue, largeValue, String.class));
        arguments.add(new Argument<String>(largeValue, largeValue, String.class));
        arguments.add(new Argument<Properties>(null, null, Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns(largeValue).getResultAtInvocationTime();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(string1, string1, null)\n" +
                "- string1 -> \"1234567890x\"\n" +
                "- Observed at class1.method1(file1:111)\n" +
                "\n" +
                "2. proxy2.testMethod2() -> string1\n" +
                "- string1 -> \"1234567890x\"\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "\n", result);
    }

    @Test
    public void useFieldNamesWhenIdenticalValueIsFoundInTestObjectField() {
        testObject.field1 = "zzz";
        testObject.field2 = new Properties();
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>(testObject.field1, "xxx", String.class));
        arguments.add(new Argument<String>("bbb", "yyy", String.class));
        arguments.add(new Argument<Properties>(testObject.field2, new Properties(), Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns(testObject.field1).getResultAtInvocationTime();

        String result = detailedObservedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(field1, \"yyy\", field2)\n" +
                "- field1 -> \"xxx\"\n" +
                "- field2 -> {}\n" +
                "- Observed at class1.method1(file1:111)\n" +
                "\n" +
                "2. proxy2.testMethod2() -> field1\n" +
                "- field1 -> \"zzz\"\n" +
                "- Observed at class2.method2(file2:222)\n" +
                "\n", result);
    }

    @Test
    public void noInvocations() {
        String result = detailedObservedInvocationsReport.createReport(Collections.<ObservedInvocation>emptyList(), testObject);
        assertEquals("", result);
    }


    public static class TestClass {

        private String field1;
        private Properties field2;

        public void testMethod1(String arg1, String arg2, Properties arg3) {
        }

        public String testMethod2() {
            return null;
        }
    }
}
