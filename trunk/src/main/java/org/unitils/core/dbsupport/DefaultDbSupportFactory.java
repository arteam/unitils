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
package org.unitils.core.dbsupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.util.BaseConfigurable;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.config.PropertiesDataSourceFactory;
import org.unitils.util.PropertyUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class DefaultDbSupportFactory extends BaseConfigurable implements DbSupportFactory {

	/* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PropertiesDataSourceFactory.class);

    public static final String PROPERTY_DATABASE_START = "database";
    
    public static final String PROPERTY_DRIVERCLASSNAME_END = "driverClassName";
    
    public static final String PROPERTY_URL_END = "url";
    
    public static final String PROPERTY_USERNAME_END = "userName";
    
    public static final String PROPERTY_PASSWORD_END = "password";
    
    /** Property key of the SQL dialect of the underlying DBMS implementation */
    public static final String PROPERTY_DIALECT_END = "dialect";

    /** Property key for the database schema names */
    public static final String PROPERTY_SCHEMA_NAMES_END = "schemaNames";
	
	public DbSupport createDefaultDbSupport(SQLHandler sqlHandler) {
		String driverClassName = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END, configuration);
    	String url = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + PROPERTY_URL_END, configuration);
    	String userName = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + PROPERTY_USERNAME_END, configuration);
    	String password = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + PROPERTY_PASSWORD_END, "", configuration);
    	
    	BasicDataSource dataSource = createDataSource(null, driverClassName, url, userName, password);
    	
    	String databaseDialect = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + PROPERTY_DIALECT_END, configuration);
    	List<String> schemaNamesList = PropertyUtils.getStringList(PROPERTY_DATABASE_START + '.' + PROPERTY_SCHEMA_NAMES_END, configuration);
    	String defaultSchemaName = schemaNamesList.get(0);
    	Set<String> schemaNames = new HashSet<String>(schemaNamesList);
    	DbSupport dbSupport = ConfigUtils.getInstanceOf(DbSupport.class, configuration, databaseDialect);
    	dbSupport.init(configuration, sqlHandler, dataSource, null, defaultSchemaName, schemaNames);
    	return dbSupport;
	}
	
	/**
     * Returns the dbms specific {@link DbSupport} as configured in the given <code>Configuration</code>.
	 * @param sqlHandler    The sql handler, not null
	 * @param dataSource 
	 * @param databaseDialect 
	 * @param schemaName    The schema name, not null
     *
     * @return The dbms specific instance of {@link DbSupport}, not null
     */
    public DbSupport createDbSupport(String databaseName, SQLHandler sqlHandler) {
    	String driverClassName = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_DRIVERCLASSNAME_END, configuration);
    	String url = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_URL_END, configuration);
    	String userName = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_USERNAME_END, configuration);
    	String password = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_PASSWORD_END, configuration);
    	
        BasicDataSource dataSource = createDataSource(null, driverClassName, url, userName, password);
    	
        String databaseDialect = PropertyUtils.getString(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_DIALECT_END, configuration);
    	List<String> schemaNamesList = PropertyUtils.getStringList(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_SCHEMA_NAMES_END, configuration);
    	String defaultSchemaName = schemaNamesList.get(0);
    	Set<String> schemaNames = new HashSet<String>(schemaNamesList);
    	DbSupport dbSupport = ConfigUtils.getInstanceOf(DbSupport.class, configuration, databaseDialect);
    	dbSupport.init(configuration, sqlHandler, dataSource, databaseName, defaultSchemaName, schemaNames);
    	return dbSupport;
    }
    
    
	protected BasicDataSource createDataSource(String name,	String driverClassName, String url, String userName, String password) {
		
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
