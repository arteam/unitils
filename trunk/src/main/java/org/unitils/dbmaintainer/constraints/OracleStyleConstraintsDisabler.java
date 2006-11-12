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
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.HashSet;

/**
 * TODO test me
 * TODO javadoc me
 * <p/>
 * Implementation of {@link ConstraintsDisabler} for an DB2 database.
 *
 * @author BaVe
 */
public class OracleStyleConstraintsDisabler extends DatabaseTask implements ConstraintsDisabler {

    protected void doInit(Configuration configuration) {
    }

    /**
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#disableConstraints()
     */
    public void disableConstraints() throws StatementHandlerException {
        try {
            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                Set<String> constraintNames = dbSupport.getTableConstraintNames(tableName);
                for (String constraintName : constraintNames) {
                    dbSupport.disableConstraint(tableName, constraintName);
                }
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        }
    }

    /**
     * @see ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)
     */
    public void disableConstraintsOnConnection(Connection conn) {

    }

}
