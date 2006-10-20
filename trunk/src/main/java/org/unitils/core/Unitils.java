/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.core;

import org.apache.commons.configuration.Configuration;

import java.lang.reflect.Method;
import java.util.List;

/**
 * todo javadoc
 */
public class Unitils {

    private static Unitils unitils = new Unitils();

    public static Unitils getInstance() {

        return unitils;
    }

    //todo javadoc
    private ModulesRepository modulesRepository;

    //todo javadoc
    private Configuration configuration;

    private ThreadLocal<TestContext> testContextHolder = new ThreadLocal<TestContext>();


    // Loading core will be done the first time only
    //todo
    public Unitils() {

        configuration = createConfiguration();
        modulesRepository = createModulesRepository(configuration);
    }


    public static ModulesRepository getModulesRepository() {
        return getInstance().getModulesRepositoryImpl();
    }


    public ModulesRepository getModulesRepositoryImpl() {
        return modulesRepository;
    }


    public TestContext getTestContext() {

        TestContext testContext = testContextHolder.get();
        if (testContext == null) {
            testContext = new TestContext();
            testContextHolder.set(testContext);
        }
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

    protected Configuration createConfiguration() {

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        return configurationLoader.loadConfiguration();
    }


    public void beforeAll() {

        TestContext testContext = getTestContext();
        testContext.setTestClass(null);
        testContext.setTestObject(null);
        testContext.setTestMethod(null);

        // For each core, invoke the beforeAll method
        List<Module> modules = modulesRepository.getModules();
        for (Module module : modules) {
            modulesRepository.getTestListener(module).beforeAll();
        }
    }


    public void beforeTestClass(Object test) {

        TestContext testContext = getTestContext();
        testContext.setTestClass(test.getClass());
        testContext.setTestObject(test);
        testContext.setTestMethod(null);

        List<Module> modules = modulesRepository.getModules();
        for (Module module : modules) {
            modulesRepository.getTestListener(module).beforeTestClass(test);
        }
    }


    public void beforeTestMethod(Object testObject, Method testMethod) {

        TestContext testContext = getTestContext();
        testContext.setTestClass(testObject.getClass());
        testContext.setTestObject(testObject);
        testContext.setTestMethod(testMethod);

        // For each core, invoke the beforeTestMethod method
        List<Module> modules = modulesRepository.getModules();
        for (Module module : modules) {
            modulesRepository.getTestListener(module).beforeTestMethod(testObject, testMethod);
        }
    }


    public void afterTestMethod(Object testObject, Method testMethod) {

        TestContext testContext = getTestContext();
        testContext.setTestClass(testObject.getClass());
        testContext.setTestObject(testObject);
        testContext.setTestMethod(testMethod);

        // For each core, invoke the afterTestMethod method
        List<Module> modules = modulesRepository.getModules();
        for (Module module : modules) {
            modulesRepository.getTestListener(module).afterTestMethod(testObject, testMethod);
        }
    }

    public void afterTestClass(Object test) {

        TestContext testContext = getTestContext();
        testContext.setTestClass(test.getClass());
        testContext.setTestObject(test);
        testContext.setTestMethod(null);

        List<Module> modules = modulesRepository.getModules();
        for (Module module : modules) {
            modulesRepository.getTestListener(module).afterTestClass(test.getClass());
        }
    }


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