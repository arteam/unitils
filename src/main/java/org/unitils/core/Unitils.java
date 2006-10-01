/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.core;

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

    private static ThreadLocal<TestContext> testContextHolder = new ThreadLocal<TestContext>();


    // Loading core will be done the first time only
    //todo
    public Unitils() {

        modulesRepository = createModulesRepository();
    }


    public static TestContext getTestContext() {

        TestContext testContext = testContextHolder.get();
        if (testContext == null) {
            testContextHolder.set(new TestContext());
        }
        return testContext;
    }


    public ModulesRepository getModulesRepository() {

        return modulesRepository;
    }


    public void beforeAll() {

        // For each core, invoke the beforeAll method
        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).beforeAll();
        }
    }

    protected ModulesRepository createModulesRepository() {

        UnitilsModulesLoader unitilsModulesLoader = new UnitilsModulesLoader();
        List<UnitilsModule> modules = unitilsModulesLoader.loadModules();
        return new ModulesRepository(modules);
    }


    public void beforeTestClass(Object test) {

        setTestContextValue(test.getClass(), test, null);

        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).beforeTestClass();
        }
    }


    public void beforeTestMethod(Object test, String methodName) {

        setTestContextValue(test.getClass(), test, methodName);

        // For each core, invoke the beforeTestMethod method
        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).beforeTestMethod();
        }
    }


    public void afterTestMethod(Object test, String methodName) {

        setTestContextValue(test.getClass(), test, methodName);

        // For each core, invoke the afterTestMethod method
        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).afterTestMethod();
        }
    }

    public void afterTestClass(Object test) {

        setTestContextValue(test.getClass(), test, null);

        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).afterTestClass();
        }
    }


    public void afterAll() {

        setTestContextValue(null, null, null);

        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).afterAll();
        }
    }


    private void setTestContextValue(Class testClass, Object testObject, String testMethodName) {

        TestContext testContext = getTestContext();
        testContext.setTestClass(testClass);
        testContext.setTestObject(testObject);
        testContext.setTestMethodName(testMethodName);
    }


}