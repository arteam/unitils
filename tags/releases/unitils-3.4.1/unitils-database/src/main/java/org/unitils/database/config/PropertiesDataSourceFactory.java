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
package org.unitils.database.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.config.Configuration;

/**
 * A {@link DataSourceFactory} that loads the necessary information from a properties file.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PropertiesDataSourceFactory implements DataSourceFactory {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PropertiesDataSourceFactory.class);
    
    //Configuration
    private DatabaseConfiguration config;
    


    /**
     * Initialises itself using the properties in the given <code>Properties</code> object.
     *
     * @param configuration The config, not null
     * @param databaseName 
     */
    public void init(Properties configuration, String databaseName) {
        DatabaseConfigurations factory = new DatabaseConfigurationsFactory(new Configuration(configuration)).create();
		config = factory.getDatabaseConfiguration(databaseName);
    }
    
    /**
     * Initialises itself using the properties in the given <code>Properties</code> object.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
    	DatabaseConfigurations factory = new DatabaseConfigurationsFactory(new Configuration(configuration)).create();
		config = factory.getDatabaseConfiguration();
		
	}
    public void init(DatabaseConfiguration tempConfig) {
        this.config = tempConfig;
    }


    public DataSource createDataSource() {
        logger.info("Creating data source. Driver: " + config.getDriverClassName() + ", url: " + config.getUrl() + ", user: " + config.getUserName() + ", password: <not shown>");
        BasicDataSource dataSource = getNewDataSource();
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUsername(config.getUserName());
        dataSource.setPassword(config.getPassword());
        dataSource.setUrl(config.getUrl());
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
