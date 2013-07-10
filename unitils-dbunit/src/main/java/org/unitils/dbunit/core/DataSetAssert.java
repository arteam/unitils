/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit.core;

import org.unitils.core.util.ObjectFormatter;
import org.unitils.dbunit.dataset.Column;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Schema;
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;

/**
 * Assert class that offers assert methods for testing things that are specific to DbUnit.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetAssert {

    /* Utility for creating string representations */
    protected ObjectFormatter objectFormatter;


    public DataSetAssert(ObjectFormatter objectFormatter) {
        this.objectFormatter = objectFormatter;
    }


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
     * Formats the assertion failed message for the given difference.
     *
     * @param schemaDifference The difference, not null
     * @return The message, not null
     */
    protected String generateErrorMessage(SchemaDifference schemaDifference) {
        StringBuilder result = new StringBuilder("Assertion failed. Differences found between the expected data set and actual database content.\n");

        String schemaName = schemaDifference.getSchema().getName();
        appendMissingTableDifferences(schemaDifference, result);
        appendTableDifferences(schemaDifference, result);
        result.append("Actual database content:\n");
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
        Schema schema = schemaDifference.getSchema();
        for (Table missingTable : schemaDifference.getMissingTables()) {
            result.append("Found missing table ");
            appendTableName(schema, missingTable, result);
            result.append("\n");
        }
    }

    /**
     * Appends the table differences of the given schema difference to the result
     *
     * @param schemaDifference The difference, not null
     * @param result           The result to append to, not null
     */
    protected void appendTableDifferences(SchemaDifference schemaDifference, StringBuilder result) {
        Schema schema = schemaDifference.getSchema();
        for (TableDifference tableDifference : schemaDifference.getTableDifferences()) {
            Table table = tableDifference.getTable();
            if (table.isEmpty()) {
                result.append("Expected table to be empty but found rows for table ");
                appendTableName(schema, table, result);
                result.append("\n");
                continue;
            }
            result.append("Found differences for table ");
            appendTableName(schema, table, result);
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
            result.append("  Missing row:\n    ");
            appendColumnNames(missingRow, result);
            result.append("\n    ");
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
            result.append("  Different row:\n    ");
            appendColumnNames(rowDifference.getRow(), result);
            result.append("\n    ");
            appendRow(rowDifference.getRow(), result);

            result.append("\n  Best matching differences:");
            for (Column column : rowDifference.getMissingColumns()) {
                result.append("\n    ");
                result.append(column.getName());
                result.append(": missing");
            }
            for (ColumnDifference columnDifference : rowDifference.getColumnDifferences()) {
                result.append("\n    ");
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
            result.append("  ");
            appendTableName(schema, actualTable, result);
            result.append("\n");

            if (actualTable.getRows().isEmpty()) {
                result.append("    <empty table>\n");
            } else {
                result.append("    ");
                appendColumnNames(actualTable.getRows().get(0), result);
                result.append("\n");
                for (Row row : actualTable.getRows()) {
                    result.append("    ");
                    appendRow(row, result);
                    result.append("\n");
                }
            }
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

    /**
     * Appends the column names of the given row to the result
     *
     * @param row    The row, not null
     * @param result The result to append to, not null
     */
    protected void appendColumnNames(Row row, StringBuilder result) {
        for (Column column : row.getPrimaryKeyColumns()) {
            result.append(column.getName());
            result.append(", ");
        }
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
        for (Column column : row.getPrimaryKeyColumns()) {
            result.append(objectFormatter.format(column.getValue()));
            result.append(", ");
        }
        for (Column column : row.getColumns()) {
            result.append(objectFormatter.format(column.getValue()));
            result.append(", ");
        }
        result.setLength(result.length() - 2);
    }
}
