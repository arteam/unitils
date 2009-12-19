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

import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.preparedstatement.BasePreparedStatement;
import org.unitils.dataset.core.preparedstatement.InsertPreparedStatement;
import org.unitils.dataset.core.preparedstatement.UpdatePreparedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetLoader extends BaseDataSetLoader {


    protected BasePreparedStatement createPreparedStatementWrapper(String schemaName, String tableName, Connection connection) throws SQLException {
        return new UpdatePreparedStatement(schemaName, tableName, connection);
    }


    @Override
    protected int loadRow(String schemaName, String tableName, Row row, List<String> variables, Connection connection) throws Exception {
        int nrUpdates = super.loadRow(schemaName, tableName, row, variables, connection);
        if (nrUpdates > 0) {
            return nrUpdates;
        }
        return insertRow(schemaName, tableName, row, variables, connection);
    }


    protected int insertRow(String schemaName, String tableName, Row row, List<String> variables, Connection connection) throws SQLException {
        BasePreparedStatement preparedStatementWrapper = createInsertPreparedStatementWrapper(schemaName, tableName, connection);
        return loadRow(row, variables, preparedStatementWrapper);
    }

    protected BasePreparedStatement createInsertPreparedStatementWrapper(String schemaName, String tableName, Connection connection) throws SQLException {
        return new InsertPreparedStatement(schemaName, tableName, connection);
    }

}