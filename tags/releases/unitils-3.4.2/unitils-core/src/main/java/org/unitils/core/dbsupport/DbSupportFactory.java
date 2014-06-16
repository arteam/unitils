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
package org.unitils.core.dbsupport;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.util.PropertyUtils.getStringList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * todo javadoc
 * <p/>
 * todo cache instances
 */
public class DbSupportFactory {


    /** Property key of the SQL dialect of the underlying DBMS implementation */
    //public static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /** Property key for the database schema names */
    public static final String PROPKEY_DATABASE_SCHEMA_NAMES = "database.schemaNames";

    /* Cache of created db support instance, per schema name */
    private static Map<String, DbSupport> dbSupportCache = new HashMap<String, DbSupport>();


    /**
     * Returns the dbms specific {@link DbSupport} as configured in the given <code>Configuration</code> for the
     * default schema. The default schema is the first schema in the configured list of schemas.
     *
     * @param configuration The config, not null
     * @param sqlHandler    The sql handler, not null
     * @param dialect 
     * @return The dbms specific instance of {@link DbSupport}, not null
     */
    public static DbSupport getDefaultDbSupport(Properties configuration, SQLHandler sqlHandler, String dialect, String defaultSchemaName) {
        //String defaultSchemaName = getStringList(PROPKEY_DATABASE_SCHEMA_NAMES, configuration, true).get(0);
        return getDbSupport(configuration, sqlHandler, defaultSchemaName, dialect);
    }


    /**
     * Returns the dbms specific {@link DbSupport} as configured in the given <code>Configuration</code>.
     *
     * @param configuration The config, not null
     * @param sqlHandler    The sql handler, not null
     * @param schemaName    The schema name, not null
     * @return The dbms specific instance of {@link DbSupport}, not null
     */
    public static DbSupport getDbSupport(Properties configuration, SQLHandler sqlHandler, String schemaName, String dialect) {
        // try to retrieve from cache
        DbSupport dbSupport = dbSupportCache.get(schemaName);
        if (dbSupport != null) {
            return dbSupport;
        }
        // create new instance
        //String databaseDialect = getString(PROPKEY_DATABASE_DIALECT, configuration);
        dbSupport = getInstanceOf(DbSupport.class, configuration, dialect);
        dbSupport.init(configuration, sqlHandler, schemaName);
        // add to cache
        dbSupportCache.put(schemaName, dbSupport);
        return dbSupport;
    }


    /**
     * Returns the dbms specific {@link DbSupport} instances for all configured schemas.
     *
     * @param configuration The config, not null
     * @param sqlHandler    The sql handler, not null
     * @param dialect 
     * @return The dbms specific {@link DbSupport}, not null
     */
    public static List<DbSupport> getDbSupports(Properties configuration, SQLHandler sqlHandler, String dialect, List<String> schemaNames) {
        List<DbSupport> result = new ArrayList<DbSupport>();
        //List<String> schemaNames = getStringList(PROPKEY_DATABASE_SCHEMA_NAMES, configuration, true);
        for (String schemaName : schemaNames) {
            DbSupport dbSupport = getDbSupport(configuration, sqlHandler, schemaName, dialect);
            result.add(dbSupport);
        }
        return result;
    }
}
