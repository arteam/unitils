/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils;

import be.ordina.unitils.module.UnitilsModule;
import be.ordina.unitils.module.UnitilsModulesLoader;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * todo javadoc
 */
public class Unitils {


    /* The logger instance for this class */
    private static final Logger logger = Logger.getLogger(Unitils.class);


    private static final String PROPKEY_MODULE_START = "module.";


    private List<UnitilsModule> modules;

    private static ThreadLocal<Object> currentTestHolder = new ThreadLocal<Object>();

    private static ThreadLocal<String> currentMethodNameHolder = new ThreadLocal<String>();

    public void beforeAll() throws Exception {
        // Loading module will be done the first time only
        UnitilsModulesLoader unitilsModulesLoader = new UnitilsModulesLoader();
        modules = unitilsModulesLoader.loadModules();

        // For each module, invoke the init method
        for (UnitilsModule module : modules) {

            module.beforeAll();
        }
    }


    public void beforeTestClass(Object test) throws Exception {
        for (UnitilsModule module : modules) {
            module.beforeTestClass(test);
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

    public void afterTestClass(Object test) throws Exception {
        for (UnitilsModule module : modules) {
            module.afterTestClass(test);
        }
    }


    public void afterAll() throws Exception {

        for (UnitilsModule module : modules) {
            module.afterAll();
        }
    }


    public static Object getCurrentTest() {
        return currentTestHolder.get();
    }

    public static String getCurrentMethodName() {
        return currentMethodNameHolder.get();
    }

}