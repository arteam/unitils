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
import org.unitils.dbmaintainer.util.BaseDatabaseTask;

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
public class DefaultConstraintsDisabler extends BaseDatabaseTask implements ConstraintsDisabler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultConstraintsDisabler.class);


    /**
     * Permanently disable every foreign key or not-null constraint
     */
    public void removeConstraints() {
        for (DbSupport dbSupport : dbSupports) {
            logger.info("Disabling contraints in database schema " + dbSupport.getSchemaName());

            // first remove referential constraints to avoid conflicts
            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                removeReferentialConstraints(tableName, dbSupport);
            }
            // remove not-null and check constraints
            for (String tableName : tableNames) {
                removeValueConstraints(tableName, dbSupport);
            }
        }
    }


    /**
     * Removes all referential constraints (e.g. foreign keys) on the specified table
     *
     * @param tableName The table, not null
     * @param dbSupport The dbSupport for the database, not null
     */
    protected void removeReferentialConstraints(String tableName, DbSupport dbSupport) {
        try {
            dbSupport.removeReferentialConstraints(tableName);
        } catch (Throwable t) {
            logger.error("Unable to remove referential constraints for table " + tableName, t);
        }
    }


    /**
     * Disables all value constraints (e.g. not null) on the specified table
     *
     * @param tableName The table, not null
     * @param dbSupport The dbSupport for the database, not null
     */
    protected void removeValueConstraints(String tableName, DbSupport dbSupport) {
        try {
            dbSupport.removeValueConstraints(tableName);
        } catch (Throwable t) {
            logger.error("Unable to remove value constraints for table " + tableName, t);
        }
    }
}