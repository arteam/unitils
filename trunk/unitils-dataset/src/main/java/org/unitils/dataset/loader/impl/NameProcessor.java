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

import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class NameProcessor {

    /* The quote string to use for making names case sensitive, null if quoting is not supported */
    private String identifierQuoteString;


    public NameProcessor(String identifierQuoteString) {
        this.identifierQuoteString = identifierQuoteString;
    }


    /**
     * Gets the schema.table name, quoted if it is a case-sensitive name.
     *
     * @param table The table, not null
     * @return The quoted name or the original name if quoting is not supported or not case sensitive
     */
    public String getTableName(Table table) {
        Schema schema = table.getSchema();
        String schemaName = schema.getName();
        if (schema.isCaseSensitive()) {
            schemaName = getQuotedName(schemaName);
        }
        String tableName = table.getName();
        if (table.isCaseSensitive()) {
            tableName = getQuotedName(tableName);
        }
        return schemaName + "." + tableName;
    }

    /**
     * Gets the column name, quoted if it is a case-sensitive name.
     *
     * @param column The column, not null
     * @return The quoted name or the original name if quoting is not supported or not case sensitive
     */
    public String getColumnName(Column column) {
        String columnName = column.getName();
        if (column.isCaseSensitive()) {
            return getQuotedName(columnName);
        }
        return columnName;
    }

    /**
     * Gets the quoted name making it a case-sensitive name.
     *
     * @param name The name, not null
     * @return The quoted name or the original name if quoting is not supported
     */
    public String getQuotedName(String name) {
        if (identifierQuoteString == null) {
            return name;
        }
        return identifierQuoteString + name + identifierQuoteString;
    }

}