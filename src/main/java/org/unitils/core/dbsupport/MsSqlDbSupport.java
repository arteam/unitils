/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.core.dbsupport;

import org.unitils.core.dbsupport.DbSupport;

import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a MsSQL database.
 *
 * @author Niki Driessen
 * @created 13-nov-2007
 */
public class MsSqlDbSupport extends DbSupport {

    /**
     * Creates a new DB support instance for MS SQL.
     */
    public MsSqlDbSupport() {
        super("MsSql");
    }

    
    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_schema = '" + getSchemaName() + "' and table_type = 'BASE TABLE'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where table_name = '" + tableName + "' and table_schema = '" + getSchemaName() + "'");
    }

    
    /**
     * Gets the names of all primary columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    @Override
    public Set<String> getPrimaryKeyColumnNames(String tableName) {
        // TODO Also take schema name into account
    	return getSQLHandler().getItemsAsStringSet("select c.name from sysindexes i " +
                "join sysobjects o ON i.id = o.id " +
                "join sysobjects pk ON i.name = pk.name " +
                "AND pk.parent_obj = i.id " +
                "AND pk.xtype = 'PK' " +
                "join sysindexkeys ik on i.id = ik.id " +
                "and i.indid = ik.indid " +
                "join syscolumns c ON ik.id = c.id " +
                "AND ik.colid = c.colid " +
                "where o.name = '" + tableName + "' ");
    }

    
    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName The table, not null
     * @return The set of column names, not null
     */
    @Override
    public Set<String> getNotNullColummnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select column_name from information_schema.columns where is_nullable = 'NO' and table_name = '" + tableName + "' and table_schema = '" + getSchemaName() + "'");
    }

    
    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select table_name from information_schema.tables where table_schema = '" + getSchemaName() + "' and table_type = 'VIEW'");
    }

    
    /**
     * Retrieves the names of all the foreign key constraints.
     *
     * @param tableName the table, not null
     * @return The set of foreign key constraints, not null
     */
    @Override
    public Set<String> getForeignKeyConstraintNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select constraint_name from information_schema.table_constraints where constraint_type = 'FOREIGN KEY' AND table_name = '" + tableName + "' and constraint_schema = '" + getSchemaName() + "'");
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    @Override
    public void removeNotNullConstraint(String tableName, String columnName) {
        String dataType = getColumnDataType(tableName, columnName);
        //MS SQL doesn't support "altering" column of type "text"
        if (!dataType.equalsIgnoreCase("text")) {
            getSQLHandler().executeUpdate("ALTER TABLE " + qualified(tableName) + " alter column " + quoted(columnName) + " " + dataType + " NULL");
        }
    }

    
    /**
     * @param tableName  The table with the column, not null
     * @param columnName The name of the column, not null
     * @return The data type of the given column from the given table, not null
     */
	protected String getColumnDataType(String tableName, String columnName) {
		// TODO provide cleaner implementation for data type handling
		String dataType = getSQLHandler().getItemAsString(
                "select data_type from information_schema.columns where table_name = '" + tableName + 
                "' and table_schema = '" + getSchemaName() + "' and column_name = '" + columnName + "'");
        if (dataType.contains("char")) {
            String length = getSQLHandler().getItemAsString(
                    "select character_maximum_length from information_schema.columns where table_name = '" +
                            tableName + "' and column_name = '" + columnName + "'");
            dataType = dataType + "(" + length + ")";
        }
		return dataType;
	}


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    @Override
    public void dropTable(String tableName) {
        getSQLHandler().executeUpdate("drop table " + qualified(tableName));
    }

    
    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    @Override
    public void removeForeignKeyConstraint(String tableName, String constraintName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " drop constraint " + quoted(constraintName));
    }
}
