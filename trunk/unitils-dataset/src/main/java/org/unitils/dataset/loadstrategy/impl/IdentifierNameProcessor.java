/*
 * Copyright Unitils.org
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
package org.unitils.dataset.loadstrategy.impl;

import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;

/**
 * Handles correct casing of database identifier, e.g. table names.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class IdentifierNameProcessor {

    private DatabaseMetaData databaseMetaData;


    public IdentifierNameProcessor(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }


    /**
     * Gets the table name prefixed with the schema name and quoted if it is a case-sensitive name.
     *
     * @param dataSetRow The data set row, not null
     * @return The qualified table name, not null
     */
    public String getQualifiedTableName(DataSetRow dataSetRow) {
        String schemaName = dataSetRow.getSchemaName();
        String tableName = dataSetRow.getTableName();

        boolean caseSensitive = dataSetRow.getDataSetSettings().isCaseSensitive();
        if (caseSensitive) {
            schemaName = databaseMetaData.quoteIdentifier(schemaName);
            tableName = databaseMetaData.quoteIdentifier(tableName);
        } else {
            schemaName = databaseMetaData.toCorrectCaseIdentifier(schemaName);
            tableName = databaseMetaData.toCorrectCaseIdentifier(tableName);
        }
        return schemaName + "." + tableName;
    }

    /**
     * Gets the column name in the correct case and quoted if it's a case-sensitive name.
     *
     * @param columnName      The column name, not null
     * @param dataSetSettings The data set settings, not null
     * @return The column name in the correct case, not null
     */
    public String getCorrectCaseColumnName(String columnName, DataSetSettings dataSetSettings) {
        boolean caseSensitive = dataSetSettings.isCaseSensitive();
        if (caseSensitive) {
            return databaseMetaData.quoteIdentifier(columnName);
        } else {
            return databaseMetaData.toCorrectCaseIdentifier(columnName);
        }
    }
}