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
package org.unitils.mock.core;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.core.report.DefaultScenarioReport;

public class DefaultReportTest extends UnitilsJUnit4 {

	Scenario scenario;
	Method testMethodDoSomething;
	Method testMethodDoSomethingElse;
	MockObject<Object> mockObject = new MockObject<Object>("testMockObject", Object.class, false, scenario);
	DefaultScenarioReport report;

	
	@Before
	public void setup() throws Exception {
		scenario = new Scenario();
		testMethodDoSomething = TestObject.class.getMethod("doSomething");
		testMethodDoSomethingElse = TestObject.class.getMethod("doSomethingElse");
		report = new DefaultScenarioReport();
		report.setScenario(scenario);
	}
	
	@Test
	public void testDefaultReport() {
		scenario.registerInvocation(new Invocation(mockObject, testMethodDoSomething, Collections.emptyList(), ProxyUtils.getProxiedMethodInvokedAt(Thread.currentThread().getStackTrace())));
		scenario.registerInvocation(new Invocation(mockObject, testMethodDoSomethingElse, Collections.emptyList(), ProxyUtils.getProxiedMethodInvokedAt(Thread.currentThread().getStackTrace())));
		scenario.registerInvocation(new Invocation(mockObject, testMethodDoSomethingElse, Collections.emptyList(), ProxyUtils.getProxiedMethodInvokedAt(Thread.currentThread().getStackTrace())));
		scenario.registerInvocation(new Invocation(mockObject, testMethodDoSomething, Collections.emptyList(), ProxyUtils.getProxiedMethodInvokedAt(Thread.currentThread().getStackTrace())));
		report.createReport();
	}
	
	static class TestObject {
		public void doSomething() {
		}
		
		public void doSomethingElse() {
		}
	}	
}
