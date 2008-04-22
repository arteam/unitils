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

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.unitils.util.PropertyUtils.getString;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * A {@link DataSourceFactory} that loads the necessary information from a properties file.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PropertiesDataSourceFactory implements DataSourceFactory {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PropertiesDataSourceFactory.class);

    /* Propery key of the database driver class name */
    private static final String PROPKEY_DATASOURCE_DRIVERCLASSNAME = "database.driverClassName";

    /* Property key of the datasource url */
    private static final String PROPKEY_DATASOURCE_URL = "database.url";

    /* Property key of the datasource connect username */
    private static final String PROPKEY_DATASOURCE_USERNAME = "database.userName";

    /* Property key of the datasource connect password */
    private static final String PROPKEY_DATASOURCE_PASSWORD = "database.password";

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
    public void init(Properties configuration) {
        driverClassName = getString(PROPKEY_DATASOURCE_DRIVERCLASSNAME, configuration);
        databaseUrl = getString(PROPKEY_DATASOURCE_URL, configuration);
        userName = getString(PROPKEY_DATASOURCE_USERNAME, null, configuration);
        password = getString(PROPKEY_DATASOURCE_PASSWORD, null, configuration);
    }


    public DataSource createDataSource() {
        logger.info("Creating data source. Driver: " + driverClassName + ", url: " + databaseUrl + ", user: " + userName + ", password: <not shown>");
        BasicDataSource dataSource = getNewDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setUrl(databaseUrl);
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
