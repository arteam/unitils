/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.core;

import org.apache.commons.configuration.Configuration;

import java.lang.reflect.Method;
import java.util.List;

/**
 * todo javadoc
 * <p/>
 * todo implement module
 * todo remove test context
 */
public class Unitils implements Module {

    private static Unitils unitils;

    public static Unitils getInstance() {
        if (unitils == null) {
            synchronized(Unitils.class) {
                if (unitils == null) {
                    initSingletonInstance();
                }
            }
        }
        return unitils;
    }

    public static void setInstance(Unitils unitils) {
        Unitils.unitils = unitils;
    }

    public static void initSingletonInstance() {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadConfiguration();

        Unitils unitils = new Unitils();
        unitils.init(configuration);
        setInstance(unitils);
    }


    //todo javadoc
    private ModulesRepository modulesRepository;

    //todo javadoc
    private Configuration configuration;

    private TestContext testContext;


    // Loading core will be done the first time only
    //todo
    public Unitils() {
        testContext = new TestContext();
    }


    public void init(Configuration configuration) {
        this.configuration = configuration;
        modulesRepository = createModulesRepository(configuration);
    }

    public TestListener createTestListener() {
        return new UnitilsTestListener();
    }


    public ModulesRepository getModulesRepository() {
        return modulesRepository;
    }


    public TestContext getTestContext() {
        return testContext;
    }


    public Configuration getConfiguration() {
        return configuration;
    }


    protected ModulesRepository createModulesRepository(Configuration configuration) {
        ModulesLoader modulesLoader = new ModulesLoader();
        List<Module> modules = modulesLoader.loadModules(configuration);
        return new ModulesRepository(modules);
    }


    private class UnitilsTestListener extends TestListener {

        @Override
        public void beforeAll() {
            TestContext testContext = getTestContext();
            testContext.setTestClass(null);
            testContext.setTestObject(null);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).beforeAll();
            }
        }


        @Override
        public void beforeTestClass(Class testClass) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testClass);
            testContext.setTestObject(null);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).beforeTestClass(testClass);
            }
        }


        @Override
        public void beforeTestSetUp(Object testObject) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).beforeTestSetUp(testObject);
            }
        }


        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(testMethod);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).beforeTestMethod(testObject, testMethod);
            }
        }


        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(testMethod);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).afterTestMethod(testObject, testMethod);
            }
        }


        @Override
        public void afterTestTearDown(Object testObject) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).afterTestTearDown(testObject);
            }
        }


        @Override
        public void afterTestClass(Class testClass) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testClass);
            testContext.setTestObject(null);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).afterTestClass(testClass);
            }
        }


        @Override
        public void afterAll() {
            TestContext testContext = getTestContext();
            testContext.setTestClass(null);
            testContext.setTestObject(null);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).afterAll();
            }
        }
    }

}