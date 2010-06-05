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
package org.unitils.dataset.loadstrategy.loader.impl;

import org.unitils.dataset.core.dataset.DataSetRow;
import org.unitils.dataset.database.DatabaseMetaData;

/**
 * todo merge with row processor ??
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class IdentifierNameProcessor {

    private DatabaseMetaData database;


    public void init(DatabaseMetaData database) {
        this.database = database;
    }


    /**
     * Gets the table name prefixed with the schema name and quoted if it is a case-sensitive name.
     *
     * @param dataSetRow The data set row, not null
     * @return The quoted name or the original name if quoting is not supported or not case sensitive
     */
    public String getQualifiedTableName(DataSetRow dataSetRow) {
        String schemaName = dataSetRow.getSchemaName();
        String tableName = dataSetRow.getTableName();

        boolean caseSensitive = dataSetRow.getDataSetSettings().isCaseSensitive();
        if (caseSensitive) {
            schemaName = database.quoteIdentifier(schemaName);
            tableName = database.quoteIdentifier(tableName);
        } else {
            schemaName = database.toCorrectCaseIdentifier(schemaName);
            tableName = database.toCorrectCaseIdentifier(tableName);
        }
        return schemaName + "." + tableName;
    }


}