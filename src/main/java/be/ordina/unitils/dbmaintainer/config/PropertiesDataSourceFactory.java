/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * {@link DataSourceFactory} that loads the necessary information from a properties file.
 */
public class PropertiesDataSourceFactory implements DataSourceFactory {

    /**
     * The name of the <code>java.sql.Driver</code> class
     */
    private String driverClassName;

    /**
     * The url of the database
     */
    private String databaseUrl;

    /**
     * The database username
     */
    private String userName;

    /**
     * The database password
     */
    private String password;

    /**
     * Initializes itself using the properties in the given <code>Properties</code> object.
     *
     * @param properties The properties
     * @throws IllegalArgumentException When the given <code>Properties</code> misses one or more required properties.
     */
    public void init(Properties properties) throws IllegalArgumentException {
        driverClassName = properties.getProperty("dataSource.driverClassName");
        databaseUrl = properties.getProperty("dataSource.url");
        userName = properties.getProperty("dataSource.userName");
        password = properties.getProperty("dataSource.password");

        if (StringUtils.isEmpty(driverClassName)) {
            throw new IllegalArgumentException("Could not determine driver class name. Missing property dataSource.driverClassName");
        }
        if (StringUtils.isEmpty(databaseUrl)) {
            throw new IllegalArgumentException("Could not determine database url. Missing property dataSource.databaseUrl");
        }
    }

    /**
     * @see be.ordina.unitils.dbmaintainer.config.DataSourceFactory#createDataSource()
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
