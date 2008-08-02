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
package org.unitils.dbmaintainer.structure.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;

import java.util.Set;

/**
 * Default implementation of {@link ConstraintsDisabler}.
 * This will disable all foreign key, check and not-null constraints on the configured database schemas.
 * Primary key constraints will not be disabled.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Bart Vermeiren
 */
public class DefaultConstraintsDisabler extends BaseDatabaseAccessor implements ConstraintsDisabler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultConstraintsDisabler.class);


    /**
     * Permanently disable every foreign key or not-null constraint
     */
    public void removeConstraints() {
        for (DbSupport dbSupport : getDbSupports()) {
            for (String schemaName : dbSupport.getSchemaNames()) {
	        	logger.info("Disabling contraints in database " + (dbSupport.getDatabaseName() != null ? dbSupport.getDatabaseName() + 
	        			", and schema " : "schema ") + schemaName);
	
	            // first remove referential constraints to avoid conflicts
	            Set<String> tableNames = dbSupport.getTableNames(schemaName);
	            for (String tableName : tableNames) {
	                removeReferentialConstraints(dbSupport, schemaName, tableName);
	            }
	            // remove not-null and check constraints
	            for (String tableName : tableNames) {
	                removeValueConstraints(dbSupport, schemaName, tableName);
	            }
            }
        }
    }


    /**
     * Removes all referential constraints (e.g. foreign keys) on the specified table
     * @param dbSupport The dbSupport for the database, not null
     * @param schemaName 
     * @param tableName The table, not null
     */
    protected void removeReferentialConstraints(DbSupport dbSupport, String schemaName, String tableName) {
        try {
            dbSupport.removeReferentialConstraints(schemaName, tableName);
        } catch (Throwable t) {
            logger.error("Unable to remove referential constraints for table " + tableName, t);
        }
    }


    /**
     * Disables all value constraints (e.g. not null) on the specified table
     * @param dbSupport The dbSupport for the database, not null
     * @param schemaName 
     * @param tableName The table, not null
     */
    protected void removeValueConstraints(DbSupport dbSupport, String schemaName, String tableName) {
        try {
            dbSupport.removeValueConstraints(schemaName, tableName);
        } catch (Throwable t) {
            logger.error("Unable to remove value constraints for table " + tableName, t);
        }
    }
}