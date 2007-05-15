/*
 * Copyright 2006 the original author or authors.
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

import java.util.Properties;
import java.util.Set;

/**
 * Default implementation of {@link ConstraintsDisabler}
 *
 * @author Filip Neven
 * @author Bart Vermeiren
 * @author Tim Ducheyne
 */
public class DefaultConstraintsDisabler extends BaseDatabaseTask implements ConstraintsDisabler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultConstraintsDisabler.class);


    /**
     * Initializes the disabler.
     *
     * @param configuration the config, not null
     */
    protected void doInit(Properties configuration) {
    }


    /**
     * Permanently disable every foreign key or not-null constraint
     */
    public void disableConstraints() {
        for (DbSupport dbSupport : dbSupports) {
            logger.info("Disabling contraints in database schema " + dbSupport.getSchemaName());
            removeForeignKeyConstraints(dbSupport);
            removeNotNullConstraints(dbSupport);
        }
    }


    /**
     * Disables all foreign key constraints
     *
     * @param dbSupport The database support, not null
     */
    protected void removeForeignKeyConstraints(DbSupport dbSupport) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            Set<String> constraintNames = dbSupport.getForeignKeyConstraintNames(tableName);
            for (String constraintName : constraintNames) {
                dbSupport.removeForeignKeyConstraint(tableName, constraintName);
            }
        }
    }


    /**
     * Disables all not-null constraints that are not of primary keys.
     *
     * @param dbSupport The database support, not null
     */
    protected void removeNotNullConstraints(DbSupport dbSupport) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            // Retrieve the name of the primary key, since we cannot remove the not-null constraint on this column
            Set<String> primaryKeyColumnNames = dbSupport.getPrimaryKeyColumnNames(tableName);

            Set<String> notNullColumnNames = dbSupport.getNotNullColummnNames(tableName);
            for (String notNullColumnName : notNullColumnNames) {
                if (primaryKeyColumnNames.contains(notNullColumnName)) {
                    // Do not remove PK constraints
                    continue;
                }
                dbSupport.removeNotNullConstraint(tableName, notNullColumnName);
            }
        }
    }


}