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

import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

/**
 * Implementation of {@link ConstraintsDisabler} for a DBMS with following properties:
 * <ul>
 * <li>Constraints can be disabled permanently and individually</li>
 * <li>Not null constraints are treated differently from foreign key constraints</li>
 * <li>Foreign key constraints checking cannot be disabled on a JDBC connection<li>
 * </ul>
 * Examples of such a DBMS are PostgreSql.
 * <p/>
 * todo merge MySql, Oracle and PostfreSql styles into 1 disabler
 * Contributed by Sunteya
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PostgreSqlStyleConstraintsDisabler extends BaseDatabaseTask implements ConstraintsDisabler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PostgreSqlStyleConstraintsDisabler.class);


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
     * Not supported for postgresql style.
     *
     * @param connection The db connection to use, not null
     */
    public void disableConstraintsOnConnection(Connection connection) {
    }


    /**
     * Removes all foreign key constraints.
     *
     * @param dbSupport The database support, not null
     */
    protected void removeForeignKeyConstraints(DbSupport dbSupport) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            Set<String> constraintNames = dbSupport.getTableConstraintNames(tableName);
            for (String constraintName : constraintNames) {
                dbSupport.disableConstraint(tableName, constraintName);
            }
        }
    }


    /**
     * Removes all not-null constraints are disabled.
     *
     * @param dbSupport The database support, not null
     */
    protected void removeNotNullConstraints(DbSupport dbSupport) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            removeNotNullConstraints(tableName, dbSupport);
        }
    }


    /**
     * Removes all not-null constraints for the table with the given name.
     *
     * @param tableName The name of the table to remove constraints from, not null
     * @param dbSupport The database support, not null
     */
    protected void removeNotNullConstraints(String tableName, DbSupport dbSupport) {
        // Retrieve the name of the primary key, since we cannot remove the not-null constraint on this column
        Set<String> primaryKeyColumnNames = dbSupport.getPrimaryKeyColumnNames(tableName);
        // Iterate over all column names
        Set<String> notNullColumnNames = dbSupport.getNotNullColummnNames(tableName);
        for (String notNullColumnName : notNullColumnNames) {
            if (!primaryKeyColumnNames.contains(notNullColumnName)) {
                // Remove the not-null constraint. Disabling is not possible in Hsqldb
                dbSupport.removeNotNullConstraint(tableName, notNullColumnName);
            }
        }
    }

}
