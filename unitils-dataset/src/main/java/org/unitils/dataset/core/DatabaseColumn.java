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
package org.unitils.dataset.core;

import org.unitils.dataset.sqltypehandler.SqlTypeHandler;

/**
 * A value of a data set column for which all variables and literal tokens were processed.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseColumn {

    /* The column name */
    private String columnName;
    /* The sql type of the column in the database */
    private int sqlType;

    private SqlTypeHandler sqlTypeHandler;
    /* True if the column is a primary key column */
    private boolean primaryKey;


    /**
     * @param columnName The column name, not null
     * @param sqlType    The sql type of the column in the database
     * @param primaryKey True if the column is a primary key column
     */
    public DatabaseColumn(String columnName, int sqlType, SqlTypeHandler sqlTypeHandler, boolean primaryKey) {
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.sqlTypeHandler = sqlTypeHandler;
        this.primaryKey = primaryKey;
    }


    /**
     * @return The column name, not null
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @return The sql type of the column in the database
     */
    public int getSqlType() {
        return sqlType;
    }


    // todo remove??
    public SqlTypeHandler getSqlTypeHandler() {
        return sqlTypeHandler;
    }

    /**
     * @return True if the column is a primary key column
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

}