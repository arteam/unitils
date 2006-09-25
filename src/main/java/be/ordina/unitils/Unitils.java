/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils;

import be.ordina.unitils.module.UnitilsModule;
import be.ordina.unitils.module.UnitilsModulesLoader;

import java.util.List;

/**
 * todo javadoc
 */
public class Unitils {


    private List<UnitilsModule> modules;

    private static ThreadLocal<Object> currentTestHolder = new ThreadLocal<Object>();

    private static ThreadLocal<String> currentMethodNameHolder = new ThreadLocal<String>();

    public void beforeAll() {
        try {
            // Loading module will be done the first time only
            UnitilsModulesLoader unitilsModulesLoader = new UnitilsModulesLoader();
            modules = unitilsModulesLoader.loadModules();

            // For each module, invoke the init method
            for (UnitilsModule module : modules) {

                module.beforeAll();
            }

        } catch (Exception e) {
            //todo implement
            e.printStackTrace();
        }
    }


    public void beforeTestClass(Object test) {
        try {
            for (UnitilsModule module : modules) {
                module.beforeTestClass(test);
            }
        } catch (Exception e) {
            //todo implement
            e.printStackTrace();
        }
    }

    public void beforeTestMethod(Object test, String methodName) {
        try {
            currentTestHolder.set(test);
            currentMethodNameHolder.set(methodName);

            // For each module, invoke the beforeTestMethod method
            for (UnitilsModule module : modules) {
                module.beforeTestMethod(test, methodName);
            }

        } catch (Exception e) {
            //todo implement exception handling
            e.printStackTrace();
        }
    }


    public void afterTestMethod(Object test, String methodName) {
        try {
            // For each module, invoke the afterTestMethod method
            for (UnitilsModule module : modules) {
                module.afterTestMethod(test, methodName);
            }

            currentTestHolder.set(null);
            currentMethodNameHolder.set(null);

        } catch (Exception e) {
            //todo implement exception handling
            e.printStackTrace();
        }
    }

    public void afterTestClass(Object test) {
        try {
            for (UnitilsModule module : modules) {
                module.afterTestClass(test);
            }

        } catch (Exception e) {
            //todo implement
            e.printStackTrace();
        }
    }


    public void afterAll() {
        try {
            for (UnitilsModule module : modules) {
                module.afterAll();
            }
        } catch (Exception e) {
            //todo implement
            e.printStackTrace();
        }
    }


    public static Object getCurrentTest() {
        return currentTestHolder.get();
    }

    public static String getCurrentMethodName() {
        return currentMethodNameHolder.get();
    }

}