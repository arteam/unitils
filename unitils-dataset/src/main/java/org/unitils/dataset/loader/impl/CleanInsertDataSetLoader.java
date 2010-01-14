/*
 * Copyright 2009,  Unitils.org
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
package org.unitils.dataset.loader.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dataset.core.Schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * First deletes all data from the tables in the data set and then
 * loads the data using insert statements.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertDataSetLoader extends InsertDataSetLoader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(CleanInsertDataSetLoader.class);


    @Override
    public void loadSchema(Schema schema, List<String> variables, Connection connection) throws SQLException {
        deleteDataFromTablesInReverseOrder(schema, connection);
        super.loadSchema(schema, variables, connection);
    }


    // delete tables in reverse order    

    protected void deleteDataFromTablesInReverseOrder(Schema schema, Connection connection) throws SQLException {
        String schemaName = schema.getName();

        List<String> tableNames = new ArrayList<String>(schema.getTableNames());
        Collections.reverse(tableNames);
        for (String tableName : tableNames) {
            deleteDataFromTable(schemaName, tableName, connection);
        }
    }

    protected void deleteDataFromTable(String schemaName, String tableName, Connection connection) throws SQLException {
        String deleteStatement = createDeleteStatement(schemaName, tableName);
        PreparedStatement preparedStatement = connection.prepareStatement(deleteStatement);
        preparedStatement.execute();
        preparedStatement.close();
    }

    protected String createDeleteStatement(String schemaName, String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ");
        stringBuilder.append(schemaName);
        stringBuilder.append(".");
        stringBuilder.append(tableName);
        return stringBuilder.toString();
    }
}