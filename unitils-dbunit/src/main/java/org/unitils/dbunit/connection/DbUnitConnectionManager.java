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
import org.dbmaintain.database.Database;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.unitils.database.dbmaintain.DbMaintainWrapper;
import org.unitilsnew.core.context.Context;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.dbunit.database.DatabaseConfig.*;

/**
 * @author Tim Ducheyne
 */
public class DbUnitConnectionManager {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DbUnitConnectionManager.class);

    protected Context context;
    protected DbMaintainWrapper dbMaintainWrapper;

    protected Map<String, DbUnitConnection> dbUnitConnectionsPerSchema = new HashMap<String, DbUnitConnection>();


    public DbUnitConnectionManager(Context context, DbMaintainWrapper dbMaintainWrapper) {
        this.context = context;
        this.dbMaintainWrapper = dbMaintainWrapper;
    }


    /**
     * Gets the DbUnit connection or creates one if it does not exist yet.
     *
     * @param schemaName The schema name, not null
     * @return The DbUnit connection, not null
     */
    public DbUnitConnection getDbUnitConnection(String schemaName) {
        DbUnitConnection dbUnitConnection = dbUnitConnectionsPerSchema.get(schemaName);
        if (dbUnitConnection == null) {
            dbUnitConnection = createDbUnitConnection(schemaName);
            dbUnitConnectionsPerSchema.put(schemaName, dbUnitConnection);
        }
        return dbUnitConnection;
    }

    public void resetDbUnitConnections() {
        dbUnitConnectionsPerSchema.clear();
    }


    /**
     * Creates a new instance of dbUnit's <code>IDatabaseConnection</code>
     *
     * @param schemaName The schema name, not null
     * @return A new instance of dbUnit's <code>IDatabaseConnection</code>
     */
    protected DbUnitConnection createDbUnitConnection(String schemaName) {
        Database database = dbMaintainWrapper.getDatabase(null);
        DataSource dataSource = database.getDataSource();
        String correctCaseSchemaName = database.toCorrectCaseIdentifier(schemaName);

        DbUnitConnection connection = new DbUnitConnection(dataSource, correctCaseSchemaName);
        configureDbUnitConnection(connection, database);
        return connection;
    }

    protected void configureDbUnitConnection(DbUnitConnection dbUnitConnection, Database database) {
        DatabaseConfig config = dbUnitConnection.getConfig();
        String dialect = database.getDatabaseInfo().getDialect();

        // Configure the IDataTypeFactory for the correct database
        IDataTypeFactory dataTypeFactory = context.getInstanceOfType(IDataTypeFactory.class, dialect);
        config.setProperty(PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        // Configure the identifier quote string
        String identifierQuoteString = database.getIdentifierQuoteString();
        if (identifierQuoteString != null) {
            config.setProperty(PROPERTY_ESCAPE_PATTERN, identifierQuoteString + '?' + identifierQuoteString);
        }
        // Make sure that batched statements are used to insert the data into the database
        config.setProperty(FEATURE_BATCHED_STATEMENTS, "true");
        // Make sure that Oracle's recycled tables (BIN$) are ignored (value is used to ensure DbUnit 2.2 compliance)
        config.setProperty(FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES, "true");
    }
}
