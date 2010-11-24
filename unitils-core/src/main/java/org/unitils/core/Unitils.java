/*
 * Copyright Unitils.org
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

import org.springframework.test.context.TestContext;

import java.util.List;
import java.util.Properties;

/**
 * Core class of the Unitils library, and the main entry point that gives access to the {@link TestContext} and the
 * different {@link Module}s.
 * <p/>
 * An instance of Unitils is configured with a certain configuration using the {@link #init(Properties)} method. Normally,
 * only one instance of Unitils exists at any time. The default instance can be obtained using the {@link #getInstance()} method.
 * <p/>
 * Unitils itself is also implemented as a module. In fact, an instance of Unitils behaves like a module who's behaviour
 * is defined by the added behaviour of all modules.
 */
public class Unitils {


    /* The singleton instance */
    private static ThreadLocal<Unitils> unitilsThreadLocal = new ThreadLocal<Unitils>();


    /**
     * Returns the singleton instance
     *
     * @return the singleton instance, not null
     */
    public static synchronized Unitils getInstance() {
        Unitils unitils = unitilsThreadLocal.get();
        if (unitils == null) {
            unitils = new Unitils();
            unitilsThreadLocal.set(unitils);
            unitils.init(null);
        }
        return unitils;
    }


    /* Repository for all modules that are currently active in Unitils */
    protected ModulesRepository modulesRepository;
    /* Configuration of Unitils, made up of different properties files */
    protected Properties configuration;
    /* The current test class, null if not known */
    protected CurrentTestClass currentTestClass;
    /* The current test instance, null if not known  */
    protected CurrentTestInstance currentTestInstance;


    /**
     * Initializes Unitils with the given custom configuration. The custom config will override the default configuration.
     *
     * @param customConfiguration The custom config, null if there is no custom config
     */
    public void init(Properties customConfiguration) {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        this.configuration = configurationLoader.loadConfiguration();
        if (customConfiguration != null) {
            configuration.putAll(customConfiguration);
        }
        modulesRepository = createModulesRepository(configuration);
        afterInitModules();
    }


    /**
     * Gives all modules the opportunity to performs initialization that
     * can only work after all other modules have been initialized
     */
    protected void afterInitModules() {
        for (Module module : modulesRepository.getModules()) {
            module.afterInit();
        }
    }


    /**
     * Returns the {@link ModulesRepository} that provides access to the modules that are configured in unitils.
     *
     * @return the {@link ModulesRepository}
     */
    public ModulesRepository getModulesRepository() {
        return modulesRepository;
    }

    /**
     * Returns all properties that are used to configure unitils and the different modules.
     *
     * @return a <code>Properties</code> object
     */
    public Properties getConfiguration() {
        return configuration;
    }


    /**
     * @return The current test class, null if not known
     */
    public CurrentTestClass getCurrentTestClass() {
        if (currentTestClass == null) {
            throw new UnitilsException("Current test class is not known. Make sure your test class is Unitils enabled, i.e. extends a base class or uses the Spring test execution listener.");
        }
        return currentTestClass;
    }

    /**
     * @param currentTestClass The current test class, null if not known
     */
    public void setCurrentTestClass(CurrentTestClass currentTestClass) {
        this.currentTestClass = currentTestClass;
    }

    /**
     * @return The current test instance, null if not known
     */
    public CurrentTestInstance getCurrentTestInstance() {
        if (currentTestClass == null) {
            throw new UnitilsException("Current test instance is not known. Possible causes:\n" +
                    "- Your test class is not Unitils enabled, i.e. it does not extend one of the base classes or does not use the Spring test execution listener.\n" +
                    "- Your calling a method that needs the test instance from a test method where the test instance is not known, e.g. a before class or static method.");
        }
        return currentTestInstance;
    }

    /**
     * @param currentTestInstance The current test instance, null if not known
     */
    public void setCurrentTestInstance(CurrentTestInstance currentTestInstance) {
        this.currentTestInstance = currentTestInstance;
    }


    /**
     * Configures all unitils modules using the given <code>Properties</code> object, and stores them in a {@link
     * ModulesRepository}. The configuration of the modules is delegated to a {@link ModulesLoader} instance.
     *
     * @param configuration The config, not null
     * @return a new {@link ModulesRepository}
     */
    protected ModulesRepository createModulesRepository(Properties configuration) {
        ModulesLoader modulesLoader = new ModulesLoader();
        List<Module> modules = modulesLoader.loadModules(configuration);
        return new ModulesRepository(modules);
    }
}