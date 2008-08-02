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

import static junit.framework.Assert.assertEquals;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.SQLUnitils;
import org.unitils.integrationtest.persistence.hibernate.HibernateSpringTest;
import org.unitils.integrationtest.persistence.hibernate.HibernateTest;
import org.unitils.integrationtest.persistence.jdbc.JdbcTest;
import org.unitils.integrationtest.persistence.jpa.HibernateJpaSpringTest;
import org.unitils.integrationtest.persistence.jpa.HibernateJpaTest;
import org.unitils.integrationtest.persistence.jpa.OpenJpaTest;
import org.unitils.integrationtest.persistence.jpa.ToplinkJpaTest;
import org.unitils.orm.jpa.JpaUnitils;
import org.unitils.util.FileUtils;
import org.unitils.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Integration tests that verify whether unitils behaves correctly in different test environment configurations.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsIntegrationTest {

    private static Log logger = LogFactory.getLog(UnitilsIntegrationTest.class);

    public static final String RESULT_FILENAME = "C:/Temp/springclassusagetest/testresult.txt";

    private static boolean succeeded = true;

//	private static PrintWriter logFileWriter;

    //private static final ClassUsageLoggingClassLoader classUsageLoggingClassLoader = new ClassUsageLoggingClassLoader();

    /*static {
         Thread.currentThread().setContextClassLoader(classUsageLoggingClassLoader);
     }*/

    @BeforeClass
    public static void initConfiguration() throws FileNotFoundException {
//		logFileWriter = new PrintWriter("C:/Temp/error.txt");
        // Make sure we use unitils-integrationtest.properties as configuration file
        System.setProperty("unitils.configuration.customFileName", "org/unitils/integrationtest/unitils-integrationtest.properties");
        // Copy the db creation scripts to the temp directory
        FileUtils.copyClassPathResource("/org/unitils/integrationtest/persistence/dbscripts/01_createPersonTable.sql", "C:/Temp/unitilsintegrationtests");
    }

    @AfterClass
    public static void logSuccess() throws IOException {
        if (succeeded) registerResult(true);
//		logFileWriter.close();
    }

//	@AfterClass
//	public static void logLoadedClasses() {
//		System.out.println("-------------- Found classes -----------------");

//		for (String loadedClass : ClassUsageLoggingClassLoader.getFoundClasses()) {
//			System.out.println(loadedClass);
//		}

//		System.out.println("-------------- Loaded classes -----------------");

//		for (String loadedClass : ClassUsageLoggingClassLoader.getLoadedClasses()) {
//			System.out.println(loadedClass);
//		}
//	}

    @Before
    public void cleanDatabase() {
//		Thread.currentThread().setContextClassLoader(classUsageLoggingClassLoader);
        DatabaseUnitils.cleanSchemas();
    }
    
    @Test
    public void testJdbc_Commit() throws Exception {
    	System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        Unitils.initSingletonInstance();
        runTest(JdbcTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(JdbcTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }
    
    @Test
    public void testJdbc_Rollback() throws Exception {
    	System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        Unitils.initSingletonInstance();
        runTest(JdbcTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(JdbcTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    public void testHibernateJpa_Commit() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        System.setProperty("jpa.persistenceProvider", "hibernate");
        Unitils.initSingletonInstance();
        runTest(HibernateJpaTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateJpaTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateJpaTest.class, "testMapping");
    }

    @Test
    public void testHibernateJpa_Rollback() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        System.setProperty("jpa.persistenceProvider", "hibernate");
        Unitils.initSingletonInstance();
        runTest(HibernateJpaTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateJpaTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateJpaTest.class, "testMapping");
    }

    @Test
    public void testToplinkJpa_Commit() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        System.setProperty("jpa.persistenceProvider", "toplink");
        Unitils.initSingletonInstance();
        runTest(ToplinkJpaTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(ToplinkJpaTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        JpaUnitils.getEntityManagerFactory().close();
    }

    @Test
    public void testToplinkJpa_Rollback() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        System.setProperty("jpa.persistenceProvider", "toplink");
        Unitils.initSingletonInstance();
        runTest(ToplinkJpaTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(ToplinkJpaTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        JpaUnitils.getEntityManagerFactory().close();
    }

    @Test
    public void testOpenJpa_Commit() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        System.setProperty("jpa.persistenceProvider", "openjpa");
        Unitils.initSingletonInstance();
        runTest(OpenJpaTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(OpenJpaTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    public void testOpenJpa_Rollback() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        System.setProperty("jpa.persistenceProvider", "openjpa");
        Unitils.initSingletonInstance();
        runTest(OpenJpaTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(OpenJpaTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    @Ignore
    public void testHibernateJpaSpring_Commit() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        Unitils.initSingletonInstance();
        runTest(HibernateJpaSpringTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateJpaSpringTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    @Ignore
    public void testHibernateJpaSpring_Rollback() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        Unitils.initSingletonInstance();
        runTest(HibernateJpaSpringTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateJpaSpringTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    public void testHibernate_Commit() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        Unitils.initSingletonInstance();
        runTest(HibernateTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    public void testHibernate_Rollback() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        Unitils.initSingletonInstance();
        runTest(HibernateTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    @Ignore
    public void testHibernateSpring_Commit() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "commit");
        Unitils.initSingletonInstance();
        runTest(HibernateSpringTest.class, "testFindById");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateSpringTest.class, "testPersist");
        assertEquals(1, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    @Test
    @Ignore
    public void testHibernateSpring_Rollback() throws Exception {
        System.setProperty("DatabaseModule.Transactional.value.default", "rollback");
        Unitils.initSingletonInstance();
        runTest(HibernateSpringTest.class, "testFindById");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
        runTest(HibernateSpringTest.class, "testPersist");
        assertEquals(0, SQLUnitils.getItemAsLong("select count(*) from person", DatabaseUnitils.getDefaultDataSource()));
    }

    protected void runTest(Class<?> testClass, final String testMethodName) throws InitializationError, IOException {
        Result result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());

        UnitilsJUnit4TestClassRunner testRunner = new UnitilsJUnit4TestClassRunner(testClass) {

            @Override
            protected List<Method> getTestMethods() {
                return Arrays.asList(ReflectionUtils.getMethod(getTestClass().getJavaClass(), testMethodName, false));
            }

        };
        testRunner.run(runNotifier);

        if (result.getFailureCount() > 0) {
            registerFailure();
        }

        for (Failure failure : result.getFailures()) {
            logger.error("Failure exception", failure.getException());
            StringWriter stringWriter = new StringWriter();
            failure.getException().printStackTrace(new PrintWriter(stringWriter));
//			logFileWriter.println(stringWriter.toString() + "\n");
        }
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }

    private static void registerFailure() throws IOException {
        registerResult(false);
    }

    private static void registerResult(boolean result) throws IOException {
        succeeded = result;
        File file = new File(RESULT_FILENAME);
        if (file.exists()) {
            file.delete();
        }
        org.apache.commons.io.FileUtils.writeStringToFile(file, "" + succeeded);
    }

    public static void main(String[] args) throws Exception {
        Result result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());

        JUnit4ClassRunner classRunner = new JUnit4ClassRunner(UnitilsIntegrationTest.class);
        classRunner.run(runNotifier);
        if (result.getFailureCount() > 0) {
            registerFailure();
        }
    }

}
