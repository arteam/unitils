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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.unitils.util.PropertyUtils.*;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.*;

/**
 * A class for loading unitils modules.
 * <p/>
 * The core names set by the {@link #PROPKEY_MODULES} property which modules will be loaded. These names can then
 * be used to construct properties that define the classnames and optionally the dependencies of these modules. E.g.
 * <pre><code>
 * unitils.modules= a, b, c, d
 * unitils.module.a.className= be.ordina.A
 * unitils.module.a.runAfter= b, c
 * unitils.module.b.className= be.ordina.B
 * unitils.module.b.runAfter= c
 * unitils.module.c.className= be.ordina.C
 * unitils.module.d.enabled= false
 * </code></pre>
 * The above configuration will load 3 core classes A, B and C and will always perform processing in
 * order C, B, A.
 * <p/>
 * If a circular dependency is found in the runAfter configuration, a runtime exception will be thrown.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ModulesLoader {

    /**
     * Property that contains the names of the modules that are to be loaded
     */
    public static final String PROPKEY_MODULES = "unitils.modules";

    /**
     * First part of all core specific properties
     */
    public static final String PROPKEY_MODULE_PREFIX = "unitils.module.";

    /**
     * Last part of the core specific property that specifies whehter the core should be loaded
     */
    public static final String PROPKEY_MODULE_SUFFIX_ENABLED = ".enabled";

    /**
     * Last part of the core specific property that specifies the classname of the core
     */
    public static final String PROPKEY_MODULE_SUFFIX_CLASS_NAME = ".className";

    /**
     * Last part of the core specific property that specifies the names of the modules that should be run before this core
     */
    public static final String PROPKEY_MODULE_SUFFIX_RUN_AFTER = ".runAfter";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ModulesLoader.class);


    /**
     * Loads all unitils modules as described in the class javadoc.
     *
     * @param configuration the configuration, not null
     * @return the modules, not null
     */
    public List<Module> loadModules(Properties configuration) {
        // get all declared modules (filter doubles)
        Set<String> moduleNames = new TreeSet<String>(getStringList(PROPKEY_MODULES, configuration));

        // remove all disable modules
        removeDisabledModules(moduleNames, configuration);

        // get all core dependencies
        Map<String, List<String>> runAfters = new HashMap<String, List<String>>();
        for (String moduleName : moduleNames) {

            // get dependencies for core
            List<String> runAfterModuleNames = getStringList(PROPKEY_MODULE_PREFIX + moduleName + PROPKEY_MODULE_SUFFIX_RUN_AFTER, configuration);
            runAfters.put(moduleName, runAfterModuleNames);
        }

        // Count each time a core is (indirectly) used in runAfter and order by count
        Map<Integer, List<String>> runAfterCounts = new TreeMap<Integer, List<String>>();
        for (String moduleName : moduleNames) {

            // calculate the nr of times a core is (indirectly) referenced
            int count = countRunAfters(moduleName, runAfters, new HashMap<String, String>());

            // store in map with count as key and a list corresponding modules as values
            List<String> countModuleNames = runAfterCounts.get(count);
            if (countModuleNames == null) {
                countModuleNames = new ArrayList<String>();
                runAfterCounts.put(count, countModuleNames);
            }
            countModuleNames.add(moduleName);
        }

        // Create core instances in the correct sequence
        List<Module> result = new ArrayList<Module>();
        for (List<String> moduleNameList : runAfterCounts.values()) {
            List<Module> modules = createAndInitializeModules(moduleNameList, configuration);
            result.addAll(modules);
        }
        return result;
    }


    /**
     * Creates the modules with the given class names and calls initializes them with the given configuration.
     *
     * @param moduleNameList the module class names, not null
     * @param configuration  the configuration, not null
     * @return the modules, not null
     */
    protected List<Module> createAndInitializeModules(List<String> moduleNameList, Properties configuration) {
        List<Module> result = new ArrayList<Module>();
        for (String moduleName : moduleNameList) {
            // get core class name
            String className = getString(PROPKEY_MODULE_PREFIX + moduleName + PROPKEY_MODULE_SUFFIX_CLASS_NAME, configuration);
            try {
                // create core instance
                Object module = createInstanceOfType(className);
                if (!(module instanceof Module)) {
                    throw new UnitilsException("Unable to load core. Module class is not of type UnitilsModule: " + className);
                }
                // run initializer
                ((Module) module).init(configuration);
                result.add((Module) module);

            } catch (UnitilsException e) {

                if (e.getCause() instanceof ClassNotFoundException || e.getCause() instanceof NoClassDefFoundError) {
                    // Class not found, maybe this is caused by a library that is not in the classpath
                    // Log warning and ingore exception
                    logger.warn("Unable to create module instance for module class: " + className + ". The module will " +
                            "not be loaded. If this is caused by a library that is not used by your project and thus not " +
                            "in the classpath, this warning can be avoided by explicitly disabling the module.");
                    logger.debug("Ignored exception during module initialisation.re", e);
                    continue;
                }
                throw e;
            }
        }
        return result;
    }


    /**
     * Count each time a core is (indirectly) used in runAfter and order by count.
     * <p/>
     * This way all modules can be ordered in such a way that all core dependencies (runAfterz) are met.
     * If no such order can be found (circular dependency) a runtime exception is thrown
     *
     * @param moduleName           the core to count, not null
     * @param allRunAfters         all dependencies as (moduleName, run-after moduleNames) entries, not null
     * @param traversedModuleNames all moduleNames that were already counted as (moduleName, moduleName) entries, not null
     * @return the count
     * @throws RuntimeException if an infinite loop (circular dependency) is found
     */
    private int countRunAfters(String moduleName, Map<String, List<String>> allRunAfters, Map<String, String> traversedModuleNames) {
        // Check for infinite loops
        if (traversedModuleNames.containsKey(moduleName)) {
            throw new UnitilsException("Unable to load modules. Circular dependency found for modules: " + traversedModuleNames.keySet());
        }
        traversedModuleNames.put(moduleName, moduleName);

        int count = 1;
        List<String> runAfters = allRunAfters.get(moduleName);
        if (runAfters != null) {
            for (String currentModuleName : runAfters) {
                // recursively count all dependencies
                count += countRunAfters(currentModuleName, allRunAfters, traversedModuleNames);
            }
        }

        traversedModuleNames.remove(moduleName);
        return count;
    }


    /**
     * Removes all modules that have a value false for the enabled property.
     *
     * @param moduleNames   the module names, not null
     * @param configuration the configuration, not null
     */
    protected void removeDisabledModules(Set<String> moduleNames, Properties configuration) {
        Iterator<String> moduleNameIterator = moduleNames.iterator();
        while (moduleNameIterator.hasNext()) {

            String moduleName = moduleNameIterator.next();
            boolean enabled = getBoolean(PROPKEY_MODULE_PREFIX + moduleName + PROPKEY_MODULE_SUFFIX_ENABLED, true, configuration);
            if (!enabled) {
                moduleNameIterator.remove();
            }
        }
    }
}
