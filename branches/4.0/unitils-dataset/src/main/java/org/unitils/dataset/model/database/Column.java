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

/**
 * A value of a data set column for which all variables and literal tokens were processed.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Column {

    /* The column name */
    private String columnName;
    /* The sql type of the column in the database */
    private int sqlType;
    /* True if the column is a primary key column */
    private boolean primaryKey;


    /**
     * @param columnName The column name, not null
     * @param sqlType    The sql type of the column in the database
     * @param primaryKey True if the column is a primary key column
     */
    public Column(String columnName, int sqlType, boolean primaryKey) {
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.primaryKey = primaryKey;
    }


    /**
     * @return The column name, not null
     */
    public String getName() {
        return columnName;
    }

    /**
     * @return The sql type of the column in the database
     */
    public int getSqlType() {
        return sqlType;
    }

    /**
     * @return True if the column is a primary key column
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

}