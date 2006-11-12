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

import java.util.Set;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ConstraintsDisabler} for a hsqldb database.
 */
public class MySqlStyleConstraintsDisabler extends DatabaseTask implements ConstraintsDisabler {

    protected void doInit(Configuration configuration) {
    }

    /**
     * Remove all not-null constraints. Foreign key constraints are disabled directly on the connection (see method
     * disableConstraintsOnConnection)
     */
    public void disableConstraints() throws StatementHandlerException {
        try {
            removeNotNullConstraints();
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        }
    }

    /**
     * Sends statements to the StatementHandler that make sure all not-null constraints are disabled.
     *
     * @throws SQLException
     * @throws StatementHandlerException
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
     * @param tableName
     * @throws SQLException
     * @throws StatementHandlerException
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
     * @param conn
     */
    public void disableConstraintsOnConnection(Connection conn) {
        dbSupport.disableForeignKeyConstraintsCheckingOnConnection(conn);
    }


}
