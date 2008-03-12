/*
 * Copyright 2006-2007,  Unitils.org
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

import java.lang.reflect.Method;
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


    private TestListener testListener;
    
    /* Repository for all modules that are currently active in Unitils */
    private ModulesRepository modulesRepository;

    /* Configuration of Unitils, made up of different properties files */
    private Properties configuration;

    /* Object keeping track of the unit test that is currently running */
    private TestContext testContext;
    

    /**
     * Creates a new instance.
     */
    public Unitils() {
        testContext = new TestContext();
    }

    
	/**
     * Initializes unitils with the configuration files.
     */
    public void init() {
        init((String) null);
    }


    /**
     * Initializes unitils with the given custom configuration file
     *
     * @param customConfigurationFileName The name of the custom configuration file
     */
    public void init(String customConfigurationFileName) {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Properties properties = configurationLoader.loadConfiguration(customConfigurationFileName);
        init(properties);
    }

    /**
     * Initializes Unitils with the given configuration. All the modules that are configured in the given configuration
     * are also created and initialized with this configuration.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
    	verifyPackaging(configuration);
        this.configuration = configuration;
        modulesRepository = createModulesRepository(configuration);
        testListener = new UnitilsTestListener();
        afterInitModules();
    }
    
    
    protected void afterInitModules() {
    	for (Module module : modulesRepository.getModules()) {
    		module.afterInit();
    	}
    }
    
    
    private void verifyPackaging(Properties configuration) {
    	String springCoreClassName = configuration.getProperty("spring.core.someClass.name");
    	String unitilsPackagedWithSpring = "org.unitils.includeddeps." + springCoreClassName;
    	
		if (isClassAvailable(springCoreClassName) && isClassAvailable(unitilsPackagedWithSpring)) {
			throw new IllegalStateException("It appears that you're using the unitils distribution that is packaged with " +
					"its dependency to spring, while spring is also in your classpath. This is not supported. The spring-packaged " +
					"distribution can only be used when you're not using spring at all. Please replace unitils-spring-included-version.jar " +
					"with unitils-version.jar");
		}
	}

    
	private boolean isClassAvailable(String className) {
		try {
			Thread.currentThread().getContextClassLoader().loadClass(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}


    /**
     * Returns the single instance of {@link TestListener}. This instance provides hook callback methods that enable intervening
     * during the execution of unit tests.
     *
     * @return The single {@link TestListener}
     */
    public TestListener getTestListener() {
        return testListener;
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
     * Returns the {@link TestContext} that, during the execution of the test suite, keeps track of the current test
     * object, class and test method that are executing.
     *
     * @return the {@link TestContext}
     */
    public TestContext getTestContext() {
        return testContext;
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


    /**
     * Implementation of {@link TestListener} that ensures that at every point during the run of a test, every {@link
     * Module} gets the chance of performing some behavior, by calling the {@link TestListener} of each module in turn.
     * Also makes sure that the state of the instance of {@link TestContext} returned by {@link Unitils#getTestContext()}
     * is correctly set to the current test class, test object and test method.
     */
    private class UnitilsTestListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).beforeTestSetUp(testObject, testMethod);
            }
        }


        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(testMethod);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).beforeTestMethod(testObject, testMethod);
            }
        }


        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable throwable) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(testMethod);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).afterTestMethod(testObject, testMethod, throwable);
            }
        }


        @Override
        public void afterTestTearDown(Object testObject, Method testMethod) {
            TestContext testContext = getTestContext();
            testContext.setTestClass(testObject.getClass());
            testContext.setTestObject(testObject);
            testContext.setTestMethod(null);

            List<Module> modules = modulesRepository.getModules();
            for (Module module : modules) {
                modulesRepository.getTestListener(module).afterTestTearDown(testObject, testMethod);
            }
        }
        
    }

}