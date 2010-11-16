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
package org.unitils.dataset.model.database;

import static thirdparty.org.apache.commons.lang.StringUtils.isBlank;

/**
 * The name of a table.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableName {

    /* The schema name in the correct case, not null */
    private String schemaName;
    /* The table name in the correct case, not null */
    private String tableName;
    /* The table name prefixed with the schema name and quoted if it is a case-sensitive name. */
    private String qualifiedTableName;


    /**
     * Creates a database row.
     *
     * @param schemaName         The schema name in the correct case, not null
     * @param tableName          The table name in the correct case, not null
     * @param qualifiedTableName The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public TableName(String schemaName, String tableName, String qualifiedTableName) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.qualifiedTableName = qualifiedTableName;
    }


    /**
     * @return The schema name in the correct case, not null
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @return The table name in the correct case, not null
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return The table name prefixed with the schema name and quoted if it is a case-sensitive name, not null
     */
    public String getQualifiedTableName() {
        return qualifiedTableName;
    }

    /**
     * @return The string representation of this row, not null
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!isBlank(schemaName)) {
            stringBuilder.append(schemaName);
            stringBuilder.append('.');
        }
        stringBuilder.append(tableName);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        TableName tableName = (TableName) object;
        return qualifiedTableName.equals(tableName.qualifiedTableName);
    }

    @Override
    public int hashCode() {
        return qualifiedTableName.hashCode();
    }
}