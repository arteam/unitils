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

import org.unitils.core.UnitilsException;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


/**
 * Implementation of {@link DbSupport} for a Derby database.
 * <p/>
 * Special thanks to Scott Prater how donated the Derby support code.
 *
 * @author Scott Prater
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DerbyDbSupport extends DbSupport {


    /**
     * Creates support for Derby databases.
     */
    public DerbyDbSupport() {
        super("derby");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getSQLHandler().getItemsAsStringSet("select SYS.SYSTABLES.TABLENAME from SYS.SYSTABLES, SYS.SYSSCHEMAS " +
                "where SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID AND SYS.SYSSCHEMAS.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select SYS.SYSCOLUMNS.COLUMNNAME from SYS.SYSCOLUMNS, SYS.SYSTABLES, SYS.SYSSCHEMAS " +
                "where SYS.SYSCOLUMNS.REFERENCEID = SYS.SYSTABLES.TABLEID AND SYS.SYSTABLES.TABLETYPE = 'T' AND SYS.SYSTABLES.TABLENAME = '" + tableName + "' AND " +
                "SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID AND SYS.SYSSCHEMAS.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all primary columns of the given table.
     * <p/>
     * This info is not available in the Derby sys tables. The database meta data is used instead to retrieve it.
     *
     * @param tableName The table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    @Override
    public Set<String> getPrimaryKeyColumnNames(String tableName) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = getSQLHandler().getDataSource().getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            resultSet = databaseMetaData.getPrimaryKeys(null, getSchemaName(), tableName);
            Set<String> result = new HashSet<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(4)); // COLUMN_NAME
            }
            return result;
        } catch (SQLException e) {
            throw new UnitilsException("Error while querying for Derby primary keys for table name: " + tableName, e);
        } finally {
            closeQuietly(connection, null, resultSet);
        }
    }


    /**
     * Returns the names of all columns that have a 'not-null' constraint on them.
     * <p/>
     * This info is not available in the Derby sys tables. The database meta data is used instead to retrieve it.
     *
     * @param tableName The table, not null
     * @return The set of column names, not null
     */
    @Override
    public Set<String> getNotNullColummnNames(String tableName) {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = getSQLHandler().getDataSource().getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            resultSet = databaseMetaData.getColumns(null, getSchemaName(), tableName, "%");
            Set<String> result = new HashSet<String>();
            while (resultSet.next()) {
                if (resultSet.getInt(11) == DatabaseMetaData.columnNoNulls) { // NULLABLE
                    result.add(resultSet.getString(4)); //COLUMN_NAME
                }
            }
            return result;
        } catch (SQLException e) {
            throw new UnitilsException("Error while querying for Derby primary keys for table name: " + tableName, e);
        } finally {
            closeQuietly(connection, null, resultSet);
        }
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select SYS.SYSTABLES.TABLENAME from SYS.SYSTABLES, SYS.SYSSCHEMAS " +
                "where SYS.SYSTABLES.TABLETYPE = 'V' AND SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID AND SYS.SYSSCHEMAS.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the synonyms in the database schema.
     *
     * @return The names of all synonyms in the database
     */
    public Set<String> getSynonymNames() {
        return getSQLHandler().getItemsAsStringSet("select SYS.SYSTABLES.TABLENAME from SYS.SYSTABLES, SYS.SYSSCHEMAS " +
                "where SYS.SYSTABLES.TABLETYPE = 'A' AND SYS.SYSTABLES.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID AND SYS.SYSSCHEMAS.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select SYS.SYSTRIGGERS.TRIGGERNAME from SYS.SYSTRIGGERS, SYS.SYSSCHEMAS " +
                "where SYS.SYSTRIGGERS.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID AND SYS.SYSSCHEMAS.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Increments the identity value for the specified identity column on the specified table to the given value.
     *
     * @param tableName          The table with the identity column, not null
     * @param identityColumnName The column, not null
     * @param identityValue      The new value
     */
    @Override
    public void incrementIdentityColumnToValue(String tableName, String identityColumnName, long identityValue) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " alter column " + quoted(identityColumnName) + " RESTART WITH " + identityValue);
    }


    /**
     * Returns the foreign key constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    @Override
    public Set<String> getForeignKeyConstraintNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select SYS.SYSCONSTRAINTS.CONSTRAINTNAME from SYS.SYSCONSTRAINTS, SYS.SYSTABLES, SYS.SYSSCHEMAS " +
                "where SYS.SYSCONSTRAINTS.TYPE = 'F' AND SYS.SYSCONSTRAINTS.TABLEID = SYS.SYSTABLES.TABLEID  AND SYS.SYSTABLES.TABLENAME = '" + tableName + "' AND " +
                "SYS.SYSCONSTRAINTS.SCHEMAID = SYS.SYSSCHEMAS.SCHEMAID AND SYS.SYSSCHEMAS.SCHEMANAME = '" + getSchemaName() + "'");
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


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    @Override
    public void removeNotNullConstraint(String tableName, String columnName) {
        getSQLHandler().executeUpdate("alter table " + qualified(tableName) + " alter column " + quoted(columnName) + " NULL");
    }


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     * Derby (as of version 10.3.x) does not support the "cascade" syntax in drop statements
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    @Override
    public void dropTable(String tableName) {
        getSQLHandler().executeUpdate("drop table " + qualified(tableName));
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     * Derby (as of version 10.3.x) does not support the "cascade" syntax in drop statements
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    public void dropView(String viewName) {
        getSQLHandler().executeUpdate("drop view " + qualified(viewName));
    }


    /**
     * Synonyms are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsSynonyms() {
        return true;
    }


    /**
     * Triggers are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsTriggers() {
        return true;
    }


    /**
     * Identity columns are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }


}