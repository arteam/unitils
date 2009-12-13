/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dbunit.util;

import org.dbunit.dataset.IDataSet;
import org.unitils.core.util.ObjectFormatter;
import org.unitils.dbunit.dataset.*;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;

import java.util.List;

/**
 * Assert class that offers assert methods for testing things that are specific to DbUnit.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetAssert {

    /* Utility for creating string representations */
    private ObjectFormatter objectFormatter = new ObjectFormatter();


    /**
     * Asserts that the given expected schema is equal to the actual schema.
     * Tables, rows or columns that are not specified in the expected schema will be ignored.
     * If an empty table is specified in the expected schema, it will check that the actual table is also be empty.
     *
     * @param expectedSchema The expected schema, not null
     * @param actualSchema   The actual schema, not null
     * @throws AssertionError When the assertion fails.
     */
    public void assertEqualSchemas(Schema expectedSchema, Schema actualSchema) throws AssertionError {
        SchemaDifference schemaDifference = expectedSchema.compare(actualSchema);
        if (schemaDifference != null) {
            String message = generateErrorMessage(schemaDifference);
            throw new AssertionError(message);
        }
    }


    /**
     * Asserts that the given expected DbUnit data set is equal to the actual DbUnit data set.
     * Tables, rows or columns that are not specified in the expected data set will be ignored.
     * If an empty table is specified in the expected data set, it will check that the actual table is also be empty.
     *
     * @param schemaName      The name of the schema that these data sets belong to, not null
     * @param expectedDataSet The expected data set, not null
     * @param actualDataSet   The actual data set, not null
     * @throws AssertionError When the assertion fails.
     */
    public void assertEqualDbUnitDataSets(String schemaName, IDataSet expectedDataSet, IDataSet actualDataSet) {
        SchemaFactory dbUnitDataSetBuilder = new SchemaFactory();
        Schema expectedSchema = dbUnitDataSetBuilder.createSchemaForDbUnitDataSet(schemaName, expectedDataSet);

        List<String> expectedTableNames = expectedSchema.getTableNames();
        Schema actualSchema = dbUnitDataSetBuilder.createSchemaForDbUnitDataSet(schemaName, actualDataSet, expectedTableNames);

        assertEqualSchemas(expectedSchema, actualSchema);
    }


    /**
     * Formats the assertion failed message for the given difference.
     *
     * @param schemaDifference The difference, not null
     * @return The message, not null
     */
    protected String generateErrorMessage(SchemaDifference schemaDifference) {
        StringBuilder result = new StringBuilder("Assertion failed. Differences found between the expected data set and actual database content.");

        String schemaName = schemaDifference.getSchema().getName();
        appendMissingTableDifferences(schemaDifference, result);
        appendTableDifferences(schemaDifference, result);
        result.append("\n\nActual database content:\n\n");
        appendSchemaContent(schemaDifference.getSchema(), schemaDifference.getActualSchema(), result);
        return result.toString();
    }


    /**
     * Appends the missing tables of the given schema difference to the result
     *
     * @param schemaDifference The difference, not null
     * @param result           The result to append to, not null
     */
    protected void appendMissingTableDifferences(SchemaDifference schemaDifference, StringBuilder result) {
        for (Table missingTable : schemaDifference.getMissingTables()) {
            result.append("\nFound missing table ");
            result.append(schemaDifference.getSchema().getName());
            result.append(".");
            result.append(missingTable.getName());
        }
    }


    /**
     * Appends the table differences of the given schema difference to the result
     *
     * @param schemaDifference The difference, not null
     * @param result           The result to append to, not null
     */
    protected void appendTableDifferences(SchemaDifference schemaDifference, StringBuilder result) {
        for (TableDifference tableDifference : schemaDifference.getTableDifferences()) {
            Table table = tableDifference.getTable();
            if (table.isEmpty()) {
                result.append("\nExpected table to be empty but found rows for table ");
                appendTableName(schemaDifference.getSchema(), table, result);
                result.append("\n");
                continue;
            }
            result.append("\nFound differences for table ");
            appendTableName(schemaDifference.getSchema(), table, result);
            result.append(":\n");
            appendMissingRowDifferences(tableDifference, result);
            appendBestRowDifferences(tableDifference, result);
        }
    }


    /**
     * Appends the missing rows of the given table difference to the result
     *
     * @param tableDifference The difference, not null
     * @param result          The result to append to, not null
     */
    protected void appendMissingRowDifferences(TableDifference tableDifference, StringBuilder result) {
        for (Row missingRow : tableDifference.getMissingRows()) {
            result.append("\n  Missing row:\n  ");
            appendColumnNames(missingRow, result);
            result.append("\n  ");
            appendRow(missingRow, result);
            result.append("\n");
        }
    }


    /**
     * Appends the best matching row differences of the given table difference to the result
     *
     * @param tableDifference The difference, not null
     * @param result          The result to append to, not null
     */
    protected void appendBestRowDifferences(TableDifference tableDifference, StringBuilder result) {
        for (RowDifference rowDifference : tableDifference.getBestRowDifferences()) {
            result.append("\n  Different row: \n  ");
            appendColumnNames(rowDifference.getRow(), result);
            result.append("\n  ");
            appendRow(rowDifference.getRow(), result);

            result.append("\n\n  Best matching differences:  ");
            for (Column column : rowDifference.getMissingColumns()) {
                result.append("\n  Missing column ");
                result.append(column.getName());
            }
            for (ColumnDifference columnDifference : rowDifference.getColumnDifferences()) {
                result.append("\n  ");
                result.append(columnDifference.getColumn().getName());
                result.append(": ");
                result.append(objectFormatter.format(columnDifference.getColumn().getValue()));
                result.append(" <-> ");
                Column actualColumn = columnDifference.getActualColumn();
                result.append(objectFormatter.format(actualColumn == null ? null : actualColumn.getValue()));
            }
            result.append("\n");
        }
    }


    /**
     * Appends the column names of the given row to the result
     *
     * @param row    The row, not null
     * @param result The result to append to, not null
     */
    protected void appendColumnNames(Row row, StringBuilder result) {
        for (Column column : row.getColumns()) {
            result.append(column.getName());
            result.append(", ");
        }
        result.setLength(result.length() - 2);
    }


    /**
     * Appends the values of the given row to the result
     *
     * @param row    The row, not null
     * @param result The result to append to, not null
     */
    protected void appendRow(Row row, StringBuilder result) {
        for (Column column : row.getColumns()) {
            result.append(objectFormatter.format(column.getValue()));
            result.append(", ");
        }
        result.setLength(result.length() - 2);
    }


    /**
     * Appends all rows and tables of the actual schema to the result. Only tables that are in the
     * expected schema will be appended.
     *
     * @param schema       The expected schema, not null
     * @param actualSchema The actual schema, not null
     * @param result       The result to append to, not null
     */
    protected void appendSchemaContent(Schema schema, Schema actualSchema, StringBuilder result) {
        for (Table table : schema.getTables()) {
            Table actualTable = actualSchema.getTable(table.getName());
            if (actualTable == null) {
                continue;
            }
            appendTableName(schema, actualTable, result);
            result.append("\n");

            if (actualTable.getRows().isEmpty()) {
                result.append("  <empty table>\n");
            } else {
                result.append("  ");
                appendColumnNames(actualTable.getRows().get(0), result);
                result.append("\n");
                for (Row row : actualTable.getRows()) {
                    result.append("  ");
                    appendRow(row, result);
                    result.append("\n");
                }
            }
            result.append("\n");
        }
    }


    /**
     * Appends the schema and table name to the result
     *
     * @param schema The schema name, not null
     * @param table  The table name, not null
     * @param result The result to append to, not null
     */
    protected void appendTableName(Schema schema, Table table, StringBuilder result) {
        result.append(schema.getName());
        result.append(".");
        result.append(table.getName());
    }



}
