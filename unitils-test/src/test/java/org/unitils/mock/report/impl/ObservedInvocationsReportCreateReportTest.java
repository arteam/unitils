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
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.proxy.Argument;

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
public class ObservedInvocationsReportCreateReportTest extends UnitilsJUnit4 {

    private ObservedInvocationsReport observedInvocationsReport;

    private Mock<ObservedInvocation> observedInvocationMock1;
    private Mock<ObservedInvocation> observedInvocationMock2;
    private TestClass testObject;


    @Before
    public void initialize() throws Exception {
        observedInvocationsReport = new ObservedInvocationsReport(new ObjectFormatter(), 10);

        testObject = new TestClass();
        Method testMethod1 = TestClass.class.getMethod("testMethod1", String.class, String.class, Properties.class);
        Method testMethod2 = TestClass.class.getMethod("testMethod2");
        StackTraceElement invokedAt1 = new StackTraceElement("class1", "method1", "file1", 111);
        StackTraceElement invokedAt2 = new StackTraceElement("class2", "method2", "file2", 222);

        observedInvocationMock1.returns("proxy1").getProxyName();
        observedInvocationMock2.returns("proxy2").getProxyName();
        observedInvocationMock1.returns(testMethod1).getMethod();
        observedInvocationMock2.returns(testMethod2).getMethod();
        observedInvocationMock1.returns(invokedAt1).getInvokedAt();
        observedInvocationMock2.returns(invokedAt2).getInvokedAt();
    }


    @Test
    public void createReport() {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>("a", "a", String.class));
        arguments.add(new Argument<String>("b", "b", String.class));
        arguments.add(new Argument<Properties>(new Properties(), new Properties(), Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns("xxx").getResultAtInvocationTime();

        String result = observedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(\"a\", \"b\", {})  .....  at class1.method1(file1:111)\n" +
                "2. proxy2.testMethod2() -> \"xxx\"  .....  at class2.method2(file2:222)\n", result);
    }

    @Test
    public void nullValues() {
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>(null, null, String.class));
        arguments.add(new Argument<String>(null, null, String.class));
        arguments.add(new Argument<String>(null, null, String.class));
        observedInvocationMock1.returns(arguments).getArguments();

        String result = observedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), null);
        assertEquals("1. proxy1.testMethod1(null, null, null)  .....  at class1.method1(file1:111)\n" +
                "2. proxy2.testMethod2() -> null  .....  at class2.method2(file2:222)\n", result);
    }

    @Test
    public void largeValuesAreReplacedByNames() {
        Properties largeProperties = new Properties();
        largeProperties.put("value", "large property");
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>("1234567890x", "1234567890x", String.class));
        arguments.add(new Argument<String>("1234567890y", "1234567890y", String.class));
        arguments.add(new Argument<Properties>(largeProperties, largeProperties, Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns("1234567890z").getResultAtInvocationTime();

        String result = observedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(string1, string2, properties1)  .....  at class1.method1(file1:111)\n" +
                "2. proxy2.testMethod2() -> string3  .....  at class2.method2(file2:222)\n", result);
    }

    @Test
    public void sameNameUsedForEqualLargeValue() {
        String largeValue = "1234567890x";
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>(largeValue, largeValue, String.class));
        arguments.add(new Argument<String>(largeValue, largeValue, String.class));
        arguments.add(new Argument<Properties>(null, null, Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns(largeValue).getResultAtInvocationTime();

        String result = observedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(string1, string1, null)  .....  at class1.method1(file1:111)\n" +
                "2. proxy2.testMethod2() -> string1  .....  at class2.method2(file2:222)\n", result);
    }

    @Test
    public void useFieldNamesWhenIdenticalValueIsFoundInTestObjectField() {
        testObject.field1 = "aaa";
        testObject.field2 = new Properties();
        List<Argument<?>> arguments = new ArrayList<Argument<?>>();
        arguments.add(new Argument<String>(testObject.field1, "xxx", String.class));
        arguments.add(new Argument<String>("bbb", "yyy", String.class));
        arguments.add(new Argument<Properties>(testObject.field2, new Properties(), Properties.class));
        observedInvocationMock1.returns(arguments).getArguments();
        observedInvocationMock2.returns(testObject.field1).getResultAtInvocationTime();

        String result = observedInvocationsReport.createReport(asList(observedInvocationMock1.getMock(), observedInvocationMock2.getMock()), testObject);
        assertEquals("1. proxy1.testMethod1(field1, \"yyy\", field2)  .....  at class1.method1(file1:111)\n" +
                "2. proxy2.testMethod2() -> field1  .....  at class2.method2(file2:222)\n", result);
    }

    @Test
    public void noInvocations() {
        String result = observedInvocationsReport.createReport(Collections.<ObservedInvocation>emptyList(), testObject);
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
