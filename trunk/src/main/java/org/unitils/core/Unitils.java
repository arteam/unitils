/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.core;

import java.util.List;
import java.lang.reflect.Method;

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

    private ThreadLocal<TestContext> testContextHolder = new ThreadLocal<TestContext>();


    // Loading core will be done the first time only
    //todo
    public Unitils() {

        modulesRepository = createModulesRepository();
    }


    public TestContext getTestContextImpl() {

        TestContext testContext = testContextHolder.get();
        if (testContext == null) {
            testContext = new TestContext();
            testContextHolder.set(testContext);
        }
        return testContext;
    }

    public static TestContext getTestContext() {
        return getInstance().getTestContextImpl();
    }


    public ModulesRepository getModulesRepositoryImpl() {
        return modulesRepository;
    }

    public static ModulesRepository getModulesRepository() {
        return getInstance().getModulesRepositoryImpl();
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


    public void beforeTestMethod(Object test, Method method) {

        setTestContextValue(test.getClass(), test, method);

        // For each core, invoke the beforeTestMethod method
        List<UnitilsModule> modules = modulesRepository.getModules();
        for (UnitilsModule module : modules) {
            modulesRepository.getTestListener(module).beforeTestMethod();
        }
    }


    public void afterTestMethod(Object test, Method method) {

        setTestContextValue(test.getClass(), test, method);

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


    private void setTestContextValue(Class testClass, Object testObject, Method testMethod) {

        TestContext testContext = getTestContext();
        testContext.setTestClass(testClass);
        testContext.setTestObject(testObject);
        testContext.setTestMethod(testMethod);
    }


}