/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit.connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseUnitils;
import org.unitilsnew.core.config.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.dbunit.database.DatabaseConfig.*;
import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupport;
import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * @author Tim Ducheyne
 */
public class DbUnitDatabaseConnectionManager {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DbUnitDatabaseConnectionManager.class);

    protected Properties properties;
    protected Map<String, DbUnitDatabaseConnection> dbUnitDatabaseConnectionsPerSchema = new HashMap<String, DbUnitDatabaseConnection>();


    public DbUnitDatabaseConnectionManager(Configuration configuration) {
        this.properties = configuration.getAllProperties();
    }


    /**
     * Gets the DbUnit connection or creates one if it does not exist yet.
     *
     * @param schemaName The schema name, not null
     * @return The DbUnit connection, not null
     */
    public DbUnitDatabaseConnection getDbUnitDatabaseConnection(String schemaName) {
        DbUnitDatabaseConnection dbUnitDatabaseConnection = dbUnitDatabaseConnectionsPerSchema.get(schemaName);
        if (dbUnitDatabaseConnection == null) {
            dbUnitDatabaseConnection = createDbUnitDatabaseConnection(schemaName);
            dbUnitDatabaseConnectionsPerSchema.put(schemaName, dbUnitDatabaseConnection);
        }
        return dbUnitDatabaseConnection;
    }


    /**
     * Creates a new instance of dbUnit's <code>IDatabaseConnection</code>
     *
     * @param schemaName The schema name, not null
     * @return A new instance of dbUnit's <code>IDatabaseConnection</code>
     */
    protected DbUnitDatabaseConnection createDbUnitDatabaseConnection(String schemaName) {
        // A DbSupport instance is fetched in order to get the schema name in correct case
        DataSource dataSource = DatabaseUnitils.getDataSource();
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        DbSupport dbSupport = getDbSupport(properties, sqlHandler, schemaName);

        // Create connection
        DbUnitDatabaseConnection connection = new DbUnitDatabaseConnection(dataSource, dbSupport.getSchemaName());
        configureDbUnitDatabaseConnection(connection, dbSupport);
        return connection;
    }

    protected void configureDbUnitDatabaseConnection(DbUnitDatabaseConnection dbUnitDatabaseConnection, DbSupport dbSupport) {
        DatabaseConfig config = dbUnitDatabaseConnection.getConfig();
        // Make sure that DbUnit's correct IDataTypeFactory, that handles dbms specific data type issues, is used
        IDataTypeFactory dataTypeFactory = getInstanceOf(IDataTypeFactory.class, properties, dbSupport.getDatabaseDialect());
        config.setProperty(PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        // Make sure that table and column names are escaped using the dbms-specific identifier quote string
        String identifierQuoteString = dbSupport.getIdentifierQuoteString();
        if (identifierQuoteString != null) {
            config.setProperty(PROPERTY_ESCAPE_PATTERN, identifierQuoteString + '?' + identifierQuoteString);
        }
        // Make sure that batched statements are used to insert the data into the database
        config.setProperty(FEATURE_BATCHED_STATEMENTS, "true");
        // Make sure that Oracle's recycled tables (BIN$) are ignored (value is used to ensure DbUnit 2.2 compliance)
        config.setProperty(FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, "true");
    }
}
