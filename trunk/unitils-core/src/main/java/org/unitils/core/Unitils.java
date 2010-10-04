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

import org.springframework.test.context.TestContext;

import java.util.List;
import java.util.Properties;

/**
 * Core class of the Unitils library, and the main entry point that gives access to the {@link TestContext} and the
 * different {@link Module}s.
 * <p/>
 * An instance of Unitils is configured with a certain configuration using the {@link #init(Properties)} method. Normally,
 * only one instance of Unitils exists at any time. The default instance can be obtained using the {@link #getInstance()} method.
 * This default instance can be set to a custom initialized instance or instance of a custom subclass using
 * {@link #setInstance(Unitils)}.
 * <p/>
 * If not set, the singleton instance is initialized by default using {@link #initSingletonInstance()}. This method uses
 * the {@link ConfigurationLoader} to load the configuration. An instance of {@link ModulesRepository} is used to
 * initialize and maintain the modules.
 * <p/>
 * Unitils itself is also implemented as a module. In fact, an instance of Unitils behaves like a module who's behaviour
 * is defined by the added behaviour of all modules.
 */
public class Unitils {


    /* The singleton instance */
    private static Unitils unitils;


    /**
     * Returns the singleton instance
     *
     * @return the singleton instance, not null
     */
    public static synchronized Unitils getInstance() {
        if (unitils == null) {
            initSingletonInstance();
        }
        return unitils;
    }


    /**
     * Sets the singleton instance to the given object
     *
     * @param unitils the singleton instance
     */
    public static void setInstance(Unitils unitils) {
        Unitils.unitils = unitils;
    }


    /**
     * Initializes the singleton instance to the default value, loading the configuration using the {@link
     * ConfigurationLoader}
     */
    public static void initSingletonInstance() {
        unitils = new Unitils();
        unitils.init();
    }


    /* Repository for all modules that are currently active in Unitils */
    private ModulesRepository modulesRepository;

    /* Configuration of Unitils, made up of different properties files */
    private Properties configuration;


    /**
     * Initializes unitils with the configuration files.
     */
    public void init() {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Properties properties = configurationLoader.loadConfiguration();
        init(properties);
    }

    /**
     * Initializes Unitils with the given configuration. All the modules that are configured in the given configuration
     * are also created and initialized with this configuration.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        //verifyPackaging(configuration);
        this.configuration = configuration;
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