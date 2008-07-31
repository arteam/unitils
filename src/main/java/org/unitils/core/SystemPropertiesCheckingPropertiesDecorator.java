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

import java.util.Properties;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class SystemPropertiesCheckingPropertiesDecorator extends Properties {

	private Properties decoratedProperties;
	
	
	/**
	 * @param decoratedProperties
	 */
	public SystemPropertiesCheckingPropertiesDecorator(Properties decoratedProperties) {
		this.decoratedProperties = decoratedProperties;
	}

	
	@Override
	public String getProperty(String key, String defaultValue) {
		String propertyValue = System.getProperty(key);
		if (propertyValue == null) {
			propertyValue = decoratedProperties.getProperty(key, defaultValue); 
		}
		return propertyValue;
	}

	
	@Override
	public String getProperty(String key) {
		String propertyValue = System.getProperty(key);
		if (propertyValue == null) {
			propertyValue = decoratedProperties.getProperty(key); 
		}
		return propertyValue;
	}


	@Override
	public synchronized boolean containsKey(Object key) {
		String propertyValue = System.getProperty((String) key);
		if (propertyValue != null) {
			return true;
		}
		return decoratedProperties.containsKey(key);
	}
	
	
	

	
}
