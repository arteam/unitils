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
package org.unitils.database.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.util.BaseConfigurable;

/**
 * A {@link DataSourceFactory} that loads the necessary information from a properties file.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PropertiesDataSourceFactory extends BaseConfigurable implements DataSourceFactory {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PropertiesDataSourceFactory.class);

    public static final String PROPERTY_DATABASE_START = "database";
    
    public static final String PROPERTY_DRIVERCLASSNAME_END = "driverClassName";
    
    public static final String PROPERTY_URL_END = "url";
    
    public static final String PROPERTY_USERNAME_END = "userName";
    
    public static final String PROPERTY_PASSWORD_END = "password";
    
    
    public DataSource createDataSource(String name) {
    	String driverClassName = configuration.getProperty(PROPERTY_DATABASE_START + '.' + name + '.' + PROPERTY_DRIVERCLASSNAME_END);
    	String url = configuration.getProperty(PROPERTY_DATABASE_START + '.' + name + '.' + PROPERTY_URL_END);
    	String userName = configuration.getProperty(PROPERTY_DATABASE_START + '.' + name + '.' + PROPERTY_USERNAME_END);
    	String password = configuration.getProperty(PROPERTY_DATABASE_START + '.' + name + '.' + PROPERTY_PASSWORD_END);
    	
        BasicDataSource dataSource = createDataSource(name, driverClassName, url, userName, password);
        return dataSource;
    }


	public DataSource createDefaultDataSource() {
		String driverClassName = configuration.getProperty(PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END);
    	String url = configuration.getProperty(PROPERTY_DATABASE_START + '.' + PROPERTY_URL_END);
    	String userName = configuration.getProperty(PROPERTY_DATABASE_START + '.' + PROPERTY_USERNAME_END);
    	String password = configuration.getProperty(PROPERTY_DATABASE_START + '.' + PROPERTY_PASSWORD_END);
    	
        BasicDataSource dataSource = createDataSource(null, driverClassName, url, userName, password);
        return dataSource;
	}


	protected BasicDataSource createDataSource(String name,
			String driverClassName, String url, String userName, String password) {
		
		logger.info("Creating data source" + (name != null ? " with name '" + name + "'" : "") + ". Driver: " + driverClassName + 
        		", url: " + url + ", user: " + userName + ", password: <not shown>");
        
		BasicDataSource dataSource = getNewDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
		return dataSource;
	}


	/**
     * Returns a concrete instance of <code>BasicDataSource</code>. This method may be overridden e.g. to return a mock
     * instance for testing
     *
     * @return An instance of <code>BasicDataSource</code>
     */
    protected BasicDataSource getNewDataSource() {
        return new BasicDataSource();
    }
}
