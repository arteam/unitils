/*
 * Copyright Unitils.org
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
package org.unitils.dataset.util;

import org.dbmaintain.database.Database;
import org.dbmaintain.database.DatabaseConnectionManager;
import org.dbmaintain.database.Databases;
import org.dbmaintain.database.DatabasesFactory;
import org.dbmaintain.database.impl.DefaultDatabaseConnectionManager;
import org.dbmaintain.database.impl.DefaultSQLHandler;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.datasource.DataSourceFactory;
import org.unitils.database.datasource.impl.DefaultDataSourceFactory;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCHEMANAMES;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseTestUtils {

    public static DatabaseMetaData createDatabaseMetaData() {
        Database defaultDatabase = getDefaultDatabase();
        return new DatabaseMetaData(defaultDatabase, new SqlTypeHandlerRepository());
    }

    public static Databases createDatabases(String schemaNames) {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_SCHEMANAMES, schemaNames);

        DataSourceFactory dataSourceFactory = new DefaultDataSourceFactory();
        DatabaseConnectionManager databaseConnectionManager = new DefaultDatabaseConnectionManager(configuration, new DefaultSQLHandler(), dataSourceFactory);
        DatabasesFactory databasesFactory = new DatabasesFactory(configuration, databaseConnectionManager);
        return databasesFactory.createDatabases();
    }
}
