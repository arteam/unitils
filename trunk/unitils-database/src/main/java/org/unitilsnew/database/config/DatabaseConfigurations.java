/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.database.config;

import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;


/**
 * @author Tim Ducheyne
 */
public class DatabaseConfigurations {

    protected DatabaseConfiguration defaultDatabaseConfiguration;
    protected Map<String, DatabaseConfiguration> databaseConfigurations;


    public DatabaseConfigurations(DatabaseConfiguration defaultDatabaseConfiguration, Map<String, DatabaseConfiguration> databaseConfigurations) {
        this.defaultDatabaseConfiguration = defaultDatabaseConfiguration;
        this.databaseConfigurations = databaseConfigurations;
    }


    public DatabaseConfiguration getDatabaseConfiguration() {
        return defaultDatabaseConfiguration;
    }

    public DatabaseConfiguration getDatabaseConfiguration(String databaseName) {
        if (isBlank(databaseName)) {
            return defaultDatabaseConfiguration;
        }
        DatabaseConfiguration databaseConfiguration = getOptionalDatabaseConfiguration(databaseName);
        if (databaseConfiguration == null) {
            throw new UnitilsException("No configuration found for database with name '" + databaseName + "'");
        }
        return databaseConfiguration;
    }

    public DatabaseConfiguration getOptionalDatabaseConfiguration(String databaseName) {
        return databaseConfigurations.get(databaseName);
    }

    public List<String> getDatabaseNames() {
        return new ArrayList<String>(databaseConfigurations.keySet());
    }

    public List<DatabaseConfiguration> getDatabaseConfigurations() {
        return new ArrayList<DatabaseConfiguration>(databaseConfigurations.values());
    }
}
