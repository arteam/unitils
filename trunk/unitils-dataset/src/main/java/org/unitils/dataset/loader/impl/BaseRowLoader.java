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
package org.unitils.dataset.loader.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.ColumnProcessor;
import org.unitils.dataset.core.ProcessedColumn;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.util.PreparedStatementUtils;
import org.unitils.dataset.loader.RowLoader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseRowLoader implements RowLoader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseRowLoader.class);

    protected ColumnProcessor columnProcessor;
    protected NameProcessor nameProcessor;
    protected Database database;

    protected PreparedStatementUtils preparedStatementFactory = new PreparedStatementUtils();


    public void init(ColumnProcessor columnProcessor, NameProcessor nameProcessor, Database database) {
        this.columnProcessor = columnProcessor;
        this.nameProcessor = nameProcessor;
        this.database = database;
    }


    public int loadRow(Row row, List<String> variables) {
        try {
            database.addExtraParentColumnsForChild(row);

            Set<String> unusedPrimaryKeyColumnNames = database.getPrimaryKeyColumnNames(row.getTable());
            List<ProcessedColumn> processedColumns = processColumns(row, variables, unusedPrimaryKeyColumnNames);

            Connection connection = database.createConnection();
            try {
                String tableName = nameProcessor.getTableName(row.getTable());
                return load(tableName, processedColumns, connection);
            } finally {
                connection.close();
            }

        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set row for table: " + row.getTable() + ", row: [" + row + "], variables: " + variables, e);
        }
    }

    protected abstract int load(String tableName, List<ProcessedColumn> processedColumns, Connection connection) throws SQLException;


    protected List<ProcessedColumn> processColumns(Row row, List<String> variables, Set<String> unusedPrimaryKeyColumnNames) {
        Set<String> allPrimaryKeyColumnNames = new HashSet<String>(unusedPrimaryKeyColumnNames);

        List<ProcessedColumn> processedColumns = new ArrayList<ProcessedColumn>();
        for (Column column : row.getColumns()) {
            boolean primaryKey = isPrimaryKeyColumn(column, allPrimaryKeyColumnNames, unusedPrimaryKeyColumnNames);
            ProcessedColumn processedColumn = columnProcessor.processColumn(column, variables, primaryKey);
            processedColumns.add(processedColumn);
        }
        return processedColumns;
    }

    protected boolean isPrimaryKeyColumn(Column column, Set<String> primaryKeyColumnNames, Set<String> remainingPrimaryKeyColumnNames) {
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            if (column.hasName(primaryKeyColumnName)) {
                remainingPrimaryKeyColumnNames.remove(primaryKeyColumnName);
                return true;
            }
        }
        return false;
    }
}