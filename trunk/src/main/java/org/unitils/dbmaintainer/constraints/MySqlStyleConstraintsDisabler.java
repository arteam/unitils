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
package org.unitils.dbmaintainer.constraints;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * Implementation of {@link ConstraintsDisabler} for a DBMS that has following properties:
 * <ul>
 * <li>Constraints cannot be disabled individually</li>
 * <li>Foreign key checking can be switched off on a JDBC <code>Connection</code></li>
 * </ul>
 * Examples of such a DBMS are MySql and hsqldb
 *
 * @author Filip Neven
 * @author Bart Vermeiren
 * @author Tim Ducheyne
 */
public class MySqlStyleConstraintsDisabler extends DatabaseTask implements ConstraintsDisabler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(MySqlStyleConstraintsDisabler.class);


    /**
     * Initializes the disabler.
     *
     * @param configuration The config, not null
     */
    protected void doInit(Configuration configuration) {
    }


    /**
     * Remove all not-null constraints. Foreign key constraints are disabled directly on the connection (see method
     * disableConstraintsOnConnection)
     */
    public void disableConstraints() throws StatementHandlerException {
        try {
            logger.info("Disabling contraints");
            removeNotNullConstraints();
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        }
    }


    /**
     * Sends statements to the StatementHandler that make sure all not-null constraints are disabled.
     */
    private void removeNotNullConstraints() throws SQLException, StatementHandlerException {
        // Iterate of all table names
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            removeNotNullConstraints(tableName);
        }
    }


    /**
     * Sends statements to the StatementHandler that make sure all not-null constraints for the table with the given
     * name are disabled.
     *
     * @param tableName The name of the table to remove constraints from, not null
     */
    private void removeNotNullConstraints(String tableName) throws SQLException, StatementHandlerException {
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


    /**
     * Makes sure foreign key checking is disabled
     *
     * @param connection The db connection to use, not null
     */
    public void disableConstraintsOnConnection(Connection connection) {
        dbSupport.disableForeignKeyConstraintsCheckingOnConnection(connection);
    }


}
