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
import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;
import org.unitils.dbunit.dataset.comparison.ValueDifference;

/**
 * Assert class that offers assert methods for testing things that are specific to DbUnit.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetAssert {


    private ObjectFormatter objectFormatter = new ObjectFormatter();


    // todo javadoc
    public void assertEqualSchemas(Schema expectedSchema, Schema actualSchema) {
        SchemaDifference schemaDifference = expectedSchema.compare(actualSchema);
        if (schemaDifference != null) {
            String message = generateErrorMessage(schemaDifference);
            throw new AssertionError(message);
        }
    }


    // todo javadoc
    public void assertEqualDbUnitDataSets(String schemaName, IDataSet expectedDataSet, IDataSet actualDataSet) {
        DbUnitDataSetBuilder dbUnitDataSetBuilder = new DbUnitDataSetBuilder();
        Schema actualSchema = dbUnitDataSetBuilder.createDataSetSchema(schemaName, actualDataSet);
        Schema expectedSchema = dbUnitDataSetBuilder.createDataSetSchema(schemaName, expectedDataSet);

        assertEqualSchemas(expectedSchema, actualSchema);
    }


    //todo javadoc
    protected String generateErrorMessage(SchemaDifference schemaComparison) {
        StringBuilder result = new StringBuilder("Assertion failed. Differences found between the expected data set and actual database content.");

        String schemaName = schemaComparison.getSchema().getName();
        appendMissingTableDifferences(schemaComparison, result);
        result.append("\n");
        appendTableDifferences(schemaComparison, result);
        result.append("\n\nActual database content:\n\n");
        appendSchemaContent(schemaComparison.getActualSchema(), result);
        return result.toString();
    }


    protected void appendMissingTableDifferences(SchemaDifference schemaComparison, StringBuilder result) {
        for (Table missingTable : schemaComparison.getMissingTables()) {
            result.append("Found missing table ");
            result.append(schemaComparison.getSchema().getName());
            result.append(".");
            result.append(missingTable.getTableName());
        }
    }


    protected void appendTableDifferences(SchemaDifference schemaComparison, StringBuilder result) {
        for (TableDifference tableDifference : schemaComparison.getTableDifference()) {
            result.append("Found differences for table ");
            appendTableName(schemaComparison.getSchema(), tableDifference.getTable(), result);
            result.append(":\n ");
            appendMissingRowDifferences(tableDifference, result);
            appendBestRowDifferences(tableDifference, result);
            result.append("\n");
        }
    }


    protected void appendMissingRowDifferences(TableDifference tableDifference, StringBuilder result) {
        for (Row missingRow : tableDifference.getMissingRows()) {
            result.append("Missing row:\n\n  ");
            appendColumnNames(missingRow, result);
            result.append("\n  ");
            appendRow(missingRow, result);
        }
    }


    protected void appendBestRowDifferences(TableDifference tableDifference, StringBuilder result) {
        for (RowDifference rowDifference : tableDifference.getBestRowDifferences()) {
            result.append("\nDifferent row: \n\n  ");
            appendColumnNames(rowDifference.getRow(), result);
            result.append("\n  ");
            appendRow(rowDifference.getRow(), result);

            result.append("\n\n  Best matching differences:  ");
            for (ValueDifference valueDifference : rowDifference.getValueDifferences()) {
                result.append("\n  ");
                result.append(valueDifference.getValue().getColumnName());
                result.append(": ");
                result.append(objectFormatter.format(valueDifference.getValue().getValue()));
                result.append(" <-> ");
                Value actualValue = valueDifference.getActualValue();
                result.append(objectFormatter.format(actualValue == null ? null : actualValue.getValue()));
            }
            result.append("\n");
        }
    }


    protected void appendColumnNames(Row row, StringBuilder result) {
        for (Value value : row.getValues()) {
            result.append(value.getColumnName());
            result.append(", ");
        }
        result.setLength(result.length() - 2);
    }


    protected void appendRow(Row row, StringBuilder result) {
        for (Value value : row.getValues()) {
            result.append(objectFormatter.format(value.getValue()));
            result.append(", ");
        }
        result.setLength(result.length() - 2);
    }


    protected void appendSchemaContent(Schema schema, StringBuilder result) {
        for (Table table : schema.getTables()) {
            appendTableName(schema, table, result);
            result.append("\n");

            if (table.getRows().isEmpty()) {
                result.append("  <empty table>\n");
            } else {
                result.append("  ");
                appendColumnNames(table.getRows().get(0), result);
                result.append("\n");
                for (Row row : table.getRows()) {
                    result.append("  ");
                    appendRow(row, result);
                    result.append("\n");
                }
            }
            result.append("\n");
        }
    }


    protected void appendTableName(Schema schema, Table table, StringBuilder result) {
        result.append(schema.getName());
        result.append(".");
        result.append(table.getTableName());
    }

}
