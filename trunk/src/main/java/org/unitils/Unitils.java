/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils;

import org.unitils.module.TestContext;
import org.unitils.module.TestListener;
import org.unitils.module.UnitilsModule;
import org.unitils.module.UnitilsModulesLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo javadoc
 */
public class Unitils {


    //todo javadoc
    private List<UnitilsModule> modules;

    private Map<UnitilsModule, TestListener> testListeners;


    public void beforeAll() {

        // Loading module will be done the first time only
        UnitilsModulesLoader unitilsModulesLoader = new UnitilsModulesLoader();
        modules = unitilsModulesLoader.loadModules();
        testListeners = createTestListeners(modules);

        setTestContextValue(null, null, null);

        // For each module, invoke the beforeAll method
        for (UnitilsModule module : modules) {
            getTestListener(module).beforeAll();
        }
    }


    public void beforeTestClass(Object test) {

        setTestContextValue(test.getClass(), test, null);

        for (UnitilsModule module : modules) {
            getTestListener(module).beforeTestClass();
        }
    }


    public void beforeTestMethod(Object test, String methodName) {

        setTestContextValue(test.getClass(), test, methodName);

        // For each module, invoke the beforeTestMethod method
        for (UnitilsModule module : modules) {
            getTestListener(module).beforeTestMethod();
        }
    }


    public void afterTestMethod(Object test, String methodName) {

        setTestContextValue(test.getClass(), test, methodName);

        // For each module, invoke the afterTestMethod method
        for (UnitilsModule module : modules) {
            getTestListener(module).afterTestMethod();
        }
    }

    public void afterTestClass(Object test) {

        setTestContextValue(test.getClass(), test, null);

        for (UnitilsModule module : modules) {
            getTestListener(module).afterTestClass();
        }
    }


    public void afterAll() {

        setTestContextValue(null, null, null);

        for (UnitilsModule module : modules) {
            getTestListener(module).afterAll();
        }
    }


    private TestListener getTestListener(UnitilsModule module) {

        return testListeners.get(module);
    }


    private Map<UnitilsModule, TestListener> createTestListeners(List<UnitilsModule> modules) {
        Map<UnitilsModule, TestListener> result = new HashMap<UnitilsModule, TestListener>(modules.size());

        for (UnitilsModule module : modules) {

            result.put(module, module.createTestListener());
        }
        return result;
    }


    private void setTestContextValue(Class testClass, Object testObject, String testMethodName) {

        TestContext.setTestClass(testClass);
        TestContext.setTestObject(testObject);
        TestContext.setTestMethodName(testMethodName);
    }

}