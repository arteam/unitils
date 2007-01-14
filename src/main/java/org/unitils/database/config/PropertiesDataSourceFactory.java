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
package org.unitils.database.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;

/**
 * A {@link DataSourceFactory} that loads the necessary information from a properties file.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PropertiesDataSourceFactory implements DataSourceFactory {

    /* The name of the <code>java.sql.Driver</code> class. */
    private String driverClassName;

    /* The url of the database. */
    private String databaseUrl;

    /* The database username. */
    private String userName;

    /* The database password. */
    private String password;


    /**
     * Initializes itself using the properties in the given <code>Properties</code> object.
     *
     * @param configuration The config, not null
     */
    public void init(Configuration configuration) {
        driverClassName = configuration.getString("dataSource.driverClassName");
        databaseUrl = configuration.getString("dataSource.url");
        userName = configuration.getString("dataSource.userName");
        password = configuration.getString("dataSource.password");

        if (StringUtils.isEmpty(driverClassName)) {
            throw new UnitilsException("Could not determine driver class name. Missing property dataSource.driverClassName");
        }
        if (StringUtils.isEmpty(databaseUrl)) {
            throw new UnitilsException("Could not determine database url. Missing property dataSource.databaseUrl");
        }
    }


    /**
     * @see DataSourceFactory#createDataSource()
     */
    public DataSource createDataSource() {
        BasicDataSource dataSource = getNewDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setUrl(databaseUrl);
        return dataSource;

    }


    /**
     * Returns a concrete instance of <code>BasicDataSource</code>. This method can org overridden to return a mock
     * instance, for testing
     *
     * @return An instance of <code>BasicDataSource</code>
     */
    protected BasicDataSource getNewDataSource() {
        return new BasicDataSource();
    }
}
