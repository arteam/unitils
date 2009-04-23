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

import static org.unitils.mock.ArgumentMatchers.same;

import java.util.Properties;

import org.apache.commons.logging.Log;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.core.util.PropertiesReader;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.InjectIntoStatic;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.reflectionassert.ReflectionAssert;

/**
 * @author Fabian Krueger
 * 
 * Test for {@link ConfigurationLoader}.
 * 
 */
//@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ConfigurationLoaderTest extends UnitilsTestNG{
	
	/** System under Test */
	@TestedObject
	private ConfigurationLoader sut;
	
	/** PropertiesReader used by sut */
	@InjectInto(property="propertiesReader")
	Mock<PropertiesReader> propertiesReader;

	/** Logger used by sut */
	@InjectIntoStatic(target=ConfigurationLoader.class, property="logger")
	Mock<Log> usedLogger;

	/** Faked default Properties (unitils.properties) */
	private Properties unitilsDefaultProperties;

	/** Faked custom Properties */
	private Properties customProperties;

	/** Faked local Properties from user.home */
	private Properties localProperties;
	
	/** Overwritten configuration file name */
	String customLocalConfigurationFileName;
	
	/** dummy filename */
	private String CUSTOM_PROPERTIES_FILE_NAME;

	/** dummy filename */
	private String LOCAL_PROPERTIES_FILE_NAME;

	/** Expected log message if no custom file was found */
	private String EXPECTED_MESSAGE_NO_CUSTOM_FILE;

	/** Expected log message if no user file was found */
	private String EXPECTED_MESSAGE_NO_LOCAL_FILE;

	//-----------------------------------------------------------------------------------
	// SetUp
	//-----------------------------------------------------------------------------------
	@BeforeMethod
	public void setUp(){
		sut = new ConfigurationLoader();
		
		CUSTOM_PROPERTIES_FILE_NAME = "unitils-custom.properties";
		LOCAL_PROPERTIES_FILE_NAME = "unitils-local.properties";

		localProperties = new Properties();
		localProperties.put("key", "value");
		
		customLocalConfigurationFileName = "my-unitils.properties";
		customProperties = new Properties();
		customProperties.put(ConfigurationLoader.PROPKEY_LOCAL_CONFIGURATION, customLocalConfigurationFileName);
		
		unitilsDefaultProperties = new Properties();
		unitilsDefaultProperties.put(ConfigurationLoader.PROPKEY_CUSTOM_CONFIGURATION, CUSTOM_PROPERTIES_FILE_NAME);
		unitilsDefaultProperties.put(ConfigurationLoader.PROPKEY_LOCAL_CONFIGURATION, LOCAL_PROPERTIES_FILE_NAME);
		
		
		
		EXPECTED_MESSAGE_NO_CUSTOM_FILE = "No custom configuration file " + CUSTOM_PROPERTIES_FILE_NAME + " found.";
		EXPECTED_MESSAGE_NO_LOCAL_FILE = "No custom configuration file " + LOCAL_PROPERTIES_FILE_NAME + " found.";
	}

	//-----------------------------------------------------------------------------------
	// Test
	//-----------------------------------------------------------------------------------
	/**
	 * Test scenario:
	 *  <ul>
	 *  <li>no filename given</li>
	 *  <li>unitils.properties file not found</li>
	 *  <li>Exception thrown</li>
	 *  </ul>
	 * 
	 */
	@Test
	public void testLoadConfiguration_noDefaultConfigurationFound(){
		String expectedMessage = "Configuration file: " + ConfigurationLoader.DEFAULT_PROPERTIES_FILE_NAME + " not found in classpath.";
		String fileName = null;
    
		try{
			propertiesReader.returns(null).loadPropertiesFileFromClasspath(same(fileName));
	        sut.loadConfiguration(fileName);
	    	fail("Exception expected.");
	    } catch(UnitilsException ue){
	    	assertEquals(expectedMessage, ue.getMessage());
	    } catch(Exception e){
	    	fail("UnitilsException expected");
	    }
	}
	
	/**
	 * Test scenario:
	 *  <ul>
	 *  <li>no filename given</li>
	 *  <li>unitils.properties file found in classpath</li>
	 *  <li>custom configuration file not found</li>
	 *  <li>local configuration file not found</li>
	 *  <li>properties from unitils.properties returned</li>
	 *  </ul>
	 * 
	 */
	@Test
	public void testLoadConfiguration_onlyDefaultConfigurationFound(){		
		String fileName = null;
		
		propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(ConfigurationLoader.DEFAULT_PROPERTIES_FILE_NAME);
		propertiesReader.returns(null).loadPropertiesFileFromClasspath(same(CUSTOM_PROPERTIES_FILE_NAME));
		propertiesReader.returns(null).loadPropertiesFileFromUserHome(same(LOCAL_PROPERTIES_FILE_NAME));
		propertiesReader.returns(null).loadPropertiesFileFromClasspath(same(CUSTOM_PROPERTIES_FILE_NAME));
		
		Properties returnedProperties = sut.loadConfiguration(fileName);
		usedLogger.assertInvoked().warn(EXPECTED_MESSAGE_NO_CUSTOM_FILE);
		usedLogger.assertInvoked().info(EXPECTED_MESSAGE_NO_LOCAL_FILE);

		ReflectionAssert.assertReflectionEquals(unitilsDefaultProperties, returnedProperties);
		
	}
	
	/**
	 * Test scenario:
	 *  <ul>
	 *  <li>no filename given</li>
	 *  <li>unitils.properties file found in classpath</li>
	 *  <li>custom configuration file found</li>
	 *  <li>local configuration file not found</li>
	 *  <li>returns properties from unitils.properties overwritten with properties from custom configuration</li>
	 *  </ul>
	 * 
	 */
	@Test
	public void testLoadConfiguration_defaultAndCustomConfigurationFound(){
		customLocalConfigurationFileName = "my-unitils.properties";
		Properties expectedProperties = new Properties();
		expectedProperties = unitilsDefaultProperties;
		expectedProperties.putAll(customProperties);
	
		EXPECTED_MESSAGE_NO_LOCAL_FILE = "No custom configuration file " + customLocalConfigurationFileName + " found.";
		
		String fileName = null;
		
		propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(ConfigurationLoader.DEFAULT_PROPERTIES_FILE_NAME);
		propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
		propertiesReader.returns(null).loadPropertiesFileFromUserHome(same(LOCAL_PROPERTIES_FILE_NAME));
		propertiesReader.returns(null).loadPropertiesFileFromClasspath(same(CUSTOM_PROPERTIES_FILE_NAME));
		
		Properties returnedProperties = sut.loadConfiguration(fileName);
		usedLogger.assertInvoked().info(EXPECTED_MESSAGE_NO_LOCAL_FILE);

		ReflectionAssert.assertReflectionEquals(expectedProperties, returnedProperties);		
	}
	
	/**
	 * Test scenario:
	 *  <ul>
	 *  <li>no filename given</li>
	 *  <li>unitils.properties file found in classpath</li>
	 *  <li>custom configuration file found</li>
	 *  <li>local configuration file found in user home directory</li>
	 *  <li>returns properties from unitils.properties first overwritten with custom properties then with user properties</li>
	 *  </ul>
	 */
	@Test
	public void testLoadConfiguration_allConfigurationssFoundWithUserConfigurationFromHomeDir(){
		customLocalConfigurationFileName = "my-unitils.properties";
		Properties expectedProperties = new Properties();
		expectedProperties = unitilsDefaultProperties;
		expectedProperties.putAll(customProperties);
		expectedProperties.putAll(localProperties);
		
		String fileName = null;
		
		propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(ConfigurationLoader.DEFAULT_PROPERTIES_FILE_NAME);
		propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
		propertiesReader.returns(localProperties).loadPropertiesFileFromUserHome(same(LOCAL_PROPERTIES_FILE_NAME));
		propertiesReader.returns(null).loadPropertiesFileFromClasspath(same(CUSTOM_PROPERTIES_FILE_NAME));
		
		Properties returnedProperties = sut.loadConfiguration(fileName);

		ReflectionAssert.assertReflectionEquals(expectedProperties, returnedProperties);	
	}
	
	/**
	 * Test scenario:
	 *  <ul>
	 *  <li>no filename given</li>
	 *  <li>unitils.properties file found in classpath</li>
	 *  <li>custom configuration file found</li>
	 *  <li>local configuration file not found in user home directory</li>
	 *  <li>local configuration file found in classpath</li>
	 *  <li>returns properties from unitils.properties first overwritten with custom properties then with user properties</li>
	 *  </ul>
	 */
	@Test
	public void testLoadConfiguration_allConfigurationsFoundWithUserConfigurationFromClasspath(){
		customLocalConfigurationFileName = "my-unitils.properties";
		Properties expectedProperties = new Properties();
		expectedProperties = unitilsDefaultProperties;
		expectedProperties.putAll(customProperties);
		expectedProperties.putAll(localProperties);
		
		String fileName = null;
		
		propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(ConfigurationLoader.DEFAULT_PROPERTIES_FILE_NAME);
		propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
		propertiesReader.returns(null).loadPropertiesFileFromUserHome(same(LOCAL_PROPERTIES_FILE_NAME));
		propertiesReader.returns(localProperties).loadPropertiesFileFromClasspath(same(CUSTOM_PROPERTIES_FILE_NAME));
		
		Properties returnedProperties = sut.loadConfiguration(fileName);

		ReflectionAssert.assertReflectionEquals(expectedProperties, returnedProperties);	
	}
}
