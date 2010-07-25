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

import org.dbmaintain.database.*;
import org.dbmaintain.database.impl.DefaultSQLHandler;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.util.List;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_SCHEMANAMES;
import static org.unitils.database.DatabaseUnitils.getDatabases;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestUtils {

    public static DatabaseMetaData createDatabaseMetaData() {
        Database defaultDatabase = getDatabases().getDefaultDatabase();
        return new DatabaseMetaData(defaultDatabase, new SqlTypeHandlerRepository());
    }

    public static Databases createDatabases(String schemaNames) {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(PROPERTY_SCHEMANAMES, schemaNames);

        DatabaseInfoFactory databaseInfoFactory = new DatabaseInfoFactory(configuration);
        List<DatabaseInfo> databaseInfos = databaseInfoFactory.getDatabaseInfos();
        DatabasesFactory databasesFactory = new DatabasesFactory(configuration, new DefaultSQLHandler());
        return databasesFactory.createDatabases(databaseInfos);
    }
}
