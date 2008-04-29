package org.unitils.mock.core;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.core.report.DefaultScenarioReport;
import org.unitils.mock.core.report.DefaultScenarioView;
import org.unitils.mock.core.report.ScenarioView;
import org.unitils.mock.core.report.SuggestedAssertsView;

public class DefaultReportTest extends UnitilsJUnit4 {

	private Scenario scenario;
	private Method testMethodDoSomething;
	private Method testMethodDoSomethingElse;
	private Object testObject = new TestObject();
	private DefaultScenarioReport report;

	
	@Before
	public void setup() throws Exception {
		scenario = new Scenario();
		testMethodDoSomething = TestObject.class.getMethod("doSomething");
		testMethodDoSomethingElse = TestObject.class.getMethod("doSomethingElse");
		report = new DefaultScenarioReport();
		report.setScenario(scenario);
		report.setScenarioViews(Arrays.asList(new ScenarioView[] { new DefaultScenarioView(), new SuggestedAssertsView()}));
	}
	
	@Test
	public void testDefaultReport() {
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomethingElse, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomethingElse, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		scenario.registerInvocation(new Invocation(testObject, testMethodDoSomething, null, Collections.emptyList(), Thread.currentThread().getStackTrace()));
		report.createReport();
	}
	
	static class TestObject {
		public void doSomething() {
		}
		
		public void doSomethingElse() {
		}
	}	
}
