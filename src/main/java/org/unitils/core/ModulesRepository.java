/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for holding and retrieving modules.
 */
public class ModulesRepository {


    /* All modules */
    private List<Module> modules;

    /* A map containing the test listeners of each module */
    private Map<Module, TestListener> testListeners;


    /**
     * Creates a repository containing the given modules.
     *
     * @param modules the modules, not null
     */
    public ModulesRepository(List<Module> modules) {
        this.modules = modules;
        this.testListeners = createTestListeners(modules);
    }


    /**
     * Gets all modules.
     *
     * @return the modules, not null
     */
    public List<Module> getModules() {
        return modules;
    }


    /**
     * Gets all listeners.
     *
     * @return the listeners per module, not null
     */
    public Map<Module, TestListener> getTestListeners() {
        return testListeners;
    }


    /**
     * Gets the modules that is of the given type or a sub-type.
     *
     * @param type the type, not null
     * @return the module, null if not found
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Module> T getFirstModule(Class<T> type) {

        for (Module module : modules) {

            if (type.isAssignableFrom(module.getClass())) {
                return (T) module;
            }
        }
        return null;
    }


    /**
     * Gets all modules that are of the given type or a sub-type.
     *
     * @param type the type, not null
     * @return the modules, an empty list if none found
     */
    @SuppressWarnings({"unchecked"})
    public <T extends Module> List<T> getModules(Class<T> type) {

        List<T> result = new ArrayList<T>();

        for (Module module : modules) {

            if (type.isAssignableFrom(module.getClass())) {
                result.add((T) module);
            }
        }
        return result;
    }


    /**
     * Gets the listener corresponding to the given module.
     *
     * @param module the module, not null
     * @return the listener, null if the module could not be found
     */
    public TestListener getTestListener(Module module) {

        return testListeners.get(module);
    }


    /**
     * Creates test listeners for each of the given modules.
     *
     * @param modules the modules, not null
     * @return the listeners for each module, not null
     */
    private Map<Module, TestListener> createTestListeners(List<Module> modules) {

        Map<Module, TestListener> result = new HashMap<Module, TestListener>(modules.size());

        for (Module module : modules) {
            result.put(module, module.createTestListener());
        }
        return result;
    }

}
