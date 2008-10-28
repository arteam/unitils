/*
 * Copyright 2008,  Unitils.org
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

import static org.unitils.util.ReflectionUtils.getClassWithName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A class for holding and retrieving modules.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
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
     * A UnitilsException is thrown when there is not exactly 1 possible match.
     *
     * @param <T>  The module type
     * @param type the module type, not null
     * @return the module, not null
     */
    public <T extends Module> T getModuleOfType(Class<T> type) {
        List<T> modulesOfType = getModulesOfType(type);
        if (modulesOfType.size() > 1) {
            throw new UnitilsException("More than one module found of type " + type.getName());
        }
        if (modulesOfType.size() < 1) {
            throw new UnitilsException("No module found of type " + type.getName());
        }
        return modulesOfType.get(0);
    }


    /**
     * Gets all modules that are of the given type or a sub-type.
     *
     * @param <T>  The module type
     * @param type the type, not null
     * @return the modules, an empty list if none found
     */
    @SuppressWarnings({"unchecked"})
    public <T> List<T> getModulesOfType(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Module module : modules) {
            if (type.isAssignableFrom(module.getClass())) {
                result.add((T) module);
            }
        }
        return result;
    }


    /**
     * Checks whether a module of a type with the given class name exists. The class name can also be
     * the super-type of an existing module.
     *
     * @param fullyQualifiedClassName The class name, not null
     * @return True if the module exists and is enabled
     */
    @SuppressWarnings("unchecked")
    public boolean isModuleEnabled(String fullyQualifiedClassName) {
        Class<? extends Module> moduleClass;
        try {
            moduleClass = getClassWithName(fullyQualifiedClassName);

        } catch (UnitilsException e) {
            // class could not be loaded
            return false;
        }
        return isModuleEnabled(moduleClass);
    }


    /**
     * Checks whether a module of a type exists. The class an also be the super-type of an existing module.
     *
     * @param moduleClass The class, not null
     * @return True if the module exists and is enabled
     */
    public boolean isModuleEnabled(Class<? extends Module> moduleClass) {
        List<? extends Module> modulesOfType = getModulesOfType(moduleClass);
        if (modulesOfType.size() > 1) {
            throw new UnitilsException("More than one module found of type " + moduleClass.getName());
        }
        return modulesOfType.size() == 1;
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
     * @param moduleList the modules, not null
     * @return the listeners for each module, not null
     */
    private Map<Module, TestListener> createTestListeners(List<Module> moduleList) {
        Map<Module, TestListener> result = new HashMap<Module, TestListener>(moduleList.size());
        for (Module module : moduleList) {
            result.put(module, module.getTestListener());
        }
        return result;
    }


	/**
	 * Gives all modules the chance to initialize itself with the given configuration
	 * @param configuration The configuration
	 */
    public void initModules(Properties configuration) {
        for (Module module : modules) {
            ((Module) module).init(configuration);
        }
	}

}
