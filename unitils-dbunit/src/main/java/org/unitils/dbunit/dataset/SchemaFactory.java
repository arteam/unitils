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
package org.unitils.dbunit.dataset;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder for creating data set schemas.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SchemaFactory {


    /**
     * Creates a data set schema for the given DbUnit dataset.
     *
     * @param schemaName    The schema name that this data set is for, not null
     * @param dbUnitDataSet The DbUnit data set, not null
     * @return The data set schema, not null
     */
    public Schema createSchemaForDbUnitDataSet(String schemaName, IDataSet dbUnitDataSet) {
        Schema result = new Schema(schemaName);
        try {
            addTables(dbUnitDataSet, result);
            return result;

        } catch (DataSetException e) {
            throw new UnitilsException("Unable to create data set for db unit data set. Schema name: " + schemaName, e);
        }

    }


    /**
     * Creates a data set schema for the given DbUnit dataset.
     *
     * @param schemaName      The schema name that this data set is for, not null
     * @param dbUnitDataSet   The DbUnit data set, not null
     * @param tablesToInclude Only tables with these names will be returned the rest will be ignored, null for all tables
     * @return The data set schema, not null
     */
    public Schema createSchemaForDbUnitDataSet(String schemaName, IDataSet dbUnitDataSet, List<String> tablesToInclude) {
        IDataSet filteredDataSet = new FilteredDataSet(new IncludeTableFilter(tablesToInclude.toArray(new String[tablesToInclude.size()])), dbUnitDataSet);
        return createSchemaForDbUnitDataSet(schemaName, filteredDataSet);
    }


    /**
     * Adds the tables of the DbUnit dataset to the given schema.
     *
     * @param dbUnitDataSet The DbUnit dataset containing the tables, not null
     * @param schema        The schema to add the tables to, not null
     */
    protected void addTables(IDataSet dbUnitDataSet, Schema schema) throws DataSetException {
        ITableIterator dbUnitTableIterator = dbUnitDataSet.iterator();
        while (dbUnitTableIterator.next()) {
            ITable dbUnitTable = dbUnitTableIterator.getTable();
            String tableName = dbUnitTable.getTableMetaData().getTableName();

            List<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(dbUnitTable);

            Table table = schema.getTable(tableName);
            if (table == null) {
                table = new Table(tableName);
                schema.addTable(table);
            }
            addRows(dbUnitTable, table, primaryKeyColumnNames);
        }
    }


    /**
     * @param tableName       The table name to check, not null
     * @param tablesToInclude Names of tables to include, null for all tables
     * @return True if the table name should be included
     */
    protected boolean shouldIgnoreTable(String tableName, List<String> tablesToInclude) {
        if (tablesToInclude == null) {
            return false;
        }
        for (String tableToInclude : tablesToInclude) {
            if (tableToInclude.equalsIgnoreCase(tableName)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Adds the rows of the DbUnit table to the given table.
     *
     * @param dbUnitTable           The DbUnit table containing the rows, not null
     * @param table                 The table to add the rows to, not null
     * @param primaryKeyColumnNames The names of the pk columns, empty if there are none
     */
    protected void addRows(ITable dbUnitTable, Table table, List<String> primaryKeyColumnNames) throws DataSetException {
        org.dbunit.dataset.Column[] columns = dbUnitTable.getTableMetaData().getColumns();
        int rowCount = dbUnitTable.getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = new Row();
            table.addRow(row);

            for (org.dbunit.dataset.Column dbUnitColumn : columns) {
                String columnName = dbUnitColumn.getColumnName();
                DataType columnType = dbUnitColumn.getDataType();
                Object value = dbUnitTable.getValue(rowIndex, columnName);

                Column column = new Column(columnName, columnType, value);
                if (primaryKeyColumnNames.contains(columnName)) {
                    row.addPrimaryKeyColumn(column);
                } else {
                    row.addColumn(column);
                }
            }
        }
    }


    /**
     * Gets the primary key column names for the given DbUnit table.
     *
     * @param dbUnitTable The DbUnit table, not null
     * @return The pk column names, empty if none found
     */
    protected List<String> getPrimaryKeyColumnNames(ITable dbUnitTable) throws DataSetException {
        List<String> result = new ArrayList<String>();
        for (org.dbunit.dataset.Column column : dbUnitTable.getTableMetaData().getPrimaryKeys()) {
            result.add(column.getColumnName());
        }
        return result;
    }

}
