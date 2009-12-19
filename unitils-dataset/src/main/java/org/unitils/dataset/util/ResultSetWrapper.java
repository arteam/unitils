/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ResultSetWrapper {

    protected ResultSet resultSet;

    protected Set<String> primaryKeyColumnNames;

    protected int rowIndex;

    private boolean useRowIndexAsIdentifier = false;


    public ResultSetWrapper(ResultSet resultSet, Set<String> primaryKeyColumnNames) {
        this.resultSet = resultSet;
        this.primaryKeyColumnNames = primaryKeyColumnNames;
        this.rowIndex = 0;

        if (primaryKeyColumnNames.isEmpty()) {
            useRowIndexAsIdentifier = true;
        }
    }


    public boolean next() throws SQLException {
        rowIndex++;
        return resultSet.next();
    }

    public void close() throws SQLException {
        resultSet.close();
    }


    public String getRowIdentifier() throws SQLException {
        StringBuilder identifier = new StringBuilder();
        if (useRowIndexAsIdentifier) {
            identifier.append(rowIndex);
            return identifier.toString();
        }
        int index = 1;
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            String primaryKeyValue = resultSet.getString(primaryKeyColumnName);
            identifier.append(primaryKeyColumnName);
            identifier.append(primaryKeyValue);
        }
        return identifier.toString();
    }


    public String getValue(int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex + 1);
    }

    public String getExpectedValue(int columnIndex) throws SQLException {
        return resultSet.getString((columnIndex + 1) * 2);
    }

    public String getActualValue(int columnIndex) throws SQLException {
        return resultSet.getString((columnIndex * 2) + 1);
    }

    public int getNrOfColumns() throws SQLException {
        return resultSet.getMetaData().getColumnCount();
    }

    public List<String> getColumnNames() throws SQLException {
        int nrOfColumns = getNrOfColumns();
        List<String> columnNames = new ArrayList<String>(nrOfColumns);
        for (int i = 0; i < nrOfColumns; i++) {
            columnNames.add(resultSet.getMetaData().getColumnName(i + 1));
        }
        return columnNames;
    }
}