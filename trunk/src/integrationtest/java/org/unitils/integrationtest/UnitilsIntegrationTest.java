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
package org.unitils.integrationtest;

import static org.unitils.inject.util.InjectionUtils.injectIntoStatic;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.SQLUnitils;
import org.unitils.integrationtest.dao.hibernate.HibernateSpringTest;
import org.unitils.integrationtest.dao.hibernate.HibernateTest;
import org.unitils.util.FileUtils;
import org.unitils.util.ReflectionUtils;

/**
 * Integration tests that verify whether unitils behaves correctly in different test environment configurations.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsIntegrationTest {

	private static Log logger = LogFactory.getLog(UnitilsIntegrationTest.class);
	
	@BeforeClass
	public static void initConfiguration() {
		System.setProperty("unitils.configuration.customFileName", "org/unitils/integrationtest/unitils-integrationtest.properties");
		FileUtils.copyClassPathResource("/org/unitils/integrationtest/dao/dbscripts/01_createPersonTable.sql", "C:/Temp/unitilsintegrationtests");
	}
	
	@Before
	public void cleanDatabase() {
		injectIntoStatic(false, UnitilsJUnit4TestClassRunner.class, "beforeAllCalled");
		DatabaseUnitils.cleanSchemas();
	}
	
	/*@Test
	public void testJpa_Commit() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "commit");
		System.setProperty("transactionManager.type", "simple");
		Unitils.initSingletonInstance();
		runTest(HibernateJpaTest.class, "testFindById");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateJpaTest.class, "testPersist");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testJpa_Rollback() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
		System.setProperty("transactionManager.type", "simple");
		Unitils.initSingletonInstance();
		runTest(HibernateJpaTest.class, "testFindById");
		Assert.assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateJpaTest.class, "testPersist");
		Assert.assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testHibernate_NoTransaction() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "disabled");
		System.setProperty("transactionManager.type", "simple");
		Unitils.initSingletonInstance();
		runTest(HibernateTest.class, "testFindById");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateTest.class, "testPersist");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}*/
	
	@Test
	public void testHibernate_Disabled() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "disabled");
		System.setProperty("transactionManager.type", "simple");
		Unitils.initSingletonInstance();
		injectIntoStatic(Unitils.getInstance().createTestListener(), UnitilsJUnit4TestClassRunner.class, "testListener");
		runTest(HibernateTest.class, "testFindById");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateTest.class, "testPersist");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testHibernate_Commit() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "commit");
		System.setProperty("transactionManager.type", "simple");
		Unitils.initSingletonInstance();
		injectIntoStatic(Unitils.getInstance().createTestListener(), UnitilsJUnit4TestClassRunner.class, "testListener");
		runTest(HibernateTest.class, "testFindById");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateTest.class, "testPersist");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testHibernate_Rollback() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
		System.setProperty("transactionManager.type", "simple");
		Unitils.initSingletonInstance();
		injectIntoStatic(Unitils.getInstance().createTestListener(), UnitilsJUnit4TestClassRunner.class, "testListener");
		runTest(HibernateTest.class, "testFindById");
		Assert.assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateTest.class, "testPersist");
		Assert.assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testHibernateSpring_Disabled() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "disabled");
		System.setProperty("transactionManager.type", "spring");
		Unitils.initSingletonInstance();
		injectIntoStatic(Unitils.getInstance().createTestListener(), UnitilsJUnit4TestClassRunner.class, "testListener");
		runTest(HibernateSpringTest.class, "testFindById");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateSpringTest.class, "testPersist");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testHibernateSpring_Commit() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "commit");
		System.setProperty("transactionManager.type", "spring");
		Unitils.initSingletonInstance();
		injectIntoStatic(Unitils.getInstance().createTestListener(), UnitilsJUnit4TestClassRunner.class, "testListener");
		runTest(HibernateSpringTest.class, "testFindById");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateSpringTest.class, "testPersist");
		Assert.assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}
	
	@Test
	public void testHibernateSpring_Rollback() throws Exception {
		System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
		System.setProperty("transactionManager.type", "spring");
		Unitils.initSingletonInstance();
		injectIntoStatic(Unitils.getInstance().createTestListener(), UnitilsJUnit4TestClassRunner.class, "testListener");
		runTest(HibernateSpringTest.class, "testFindById");
		Assert.assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
		runTest(HibernateSpringTest.class, "testPersist");
		Assert.assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDataSource()));
	}

	protected void runTest(Class<?> testClass, final String testMethodName) throws InitializationError {
		Result result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());
	    
        UnitilsJUnit4TestClassRunner testRunner = new UnitilsJUnit4TestClassRunner(testClass) {

			@Override
			protected List<Method> getTestMethods() {
				return Arrays.asList(ReflectionUtils.getMethodWithName(getTestClass().getJavaClass(), testMethodName));
			}
        	
        };
        testRunner.run(runNotifier);
        
        for (Failure failure : result.getFailures()) {
        	logger.error("Failure exception", failure.getException());
        }
        Assert.assertEquals(0, result.getFailureCount());
        Assert.assertEquals(0, result.getIgnoreCount());
	}
	
}
