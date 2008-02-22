package org.unitils.integrationtest;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.reporters.TextReporter;
import org.unitils.UnitilsTestNG;

public class TestNGTest extends UnitilsTestNG {

	@BeforeMethod
	public void before(Method m) {
		System.out.println("BeforeMethod " + m.getName());
	}
	
	@Test public void test() {
		System.out.println("executing");
		Assert.assertEquals("test", "test");
	}
	
	@Test public void test1() {
		System.out.println("executing");
		Assert.assertEquals("test", "test1");
	}
	
	@AfterMethod
	public void after(Method m) {
		System.out.println("AfterMethod " + m.getName());
	}
	
	public static void main(String[] args) {
		TestListenerAdapter tla = new TextReporter("test", 3);
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { TestNGTest.class});
		testng.addListener(tla);
		testng.run();
	}
}
