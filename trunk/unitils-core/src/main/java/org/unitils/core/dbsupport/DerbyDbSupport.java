/*
 * Copyright 2010,  Unitils.org
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;


/**
 * Implementation of {@link DbSupport} for a Derby database.
 * <p/>
 * Special thanks to Scott Prater who donated the initial version of the Derby support code.
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
        return getSQLHandler().getItemsAsStringSet("select t.TABLENAME from SYS.SYSTABLES t, SYS.SYSSCHEMAS  s where t.TABLETYPE = 'T' AND t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getSQLHandler().getItemsAsStringSet("select c.COLUMNNAME from SYS.SYSCOLUMNS c, SYS.SYSTABLES t, SYS.SYSSCHEMAS s where c.REFERENCEID = t.TABLEID and t.TABLENAME = '" + tableName + "' AND t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getSQLHandler().getItemsAsStringSet("select t.TABLENAME from SYS.SYSTABLES t, SYS.SYSSCHEMAS s where t.TABLETYPE = 'V' AND t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the synonyms in the database schema.
     *
     * @return The names of all synonyms in the database
     */
    public Set<String> getSynonymNames() {
        return getSQLHandler().getItemsAsStringSet("select t.TABLENAME from SYS.SYSTABLES t, SYS.SYSSCHEMAS s where t.TABLETYPE = 'A' AND t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getSQLHandler().getItemsAsStringSet("select t.TRIGGERNAME from SYS.SYSTRIGGERS t, SYS.SYSSCHEMAS s where t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
    }


    /**
     * Gets the names of all identity columns of the given table.
     * <p/>
     * todo check, at this moment the PK columns are returned
     *
     * @param tableName The table, not null
     * @return The names of the identity columns of the table with the given name
     */
    @Override
    public Set<String> getIdentityColumnNames(String tableName) {
        return getPrimaryKeyColumnNames(tableName);
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
     * Enables or disables the setting of identity value in insert and update statements.
     * By default some databases do not allow to set values of identity columns directly from insert/update
     * statements. If supported, this method will enable/disable this behavior.
     *
     * @param tableName The table with the identity column, not null
     * @param enabled   True to enable, false to disable
     */
    @Override
    public void setSettingIdentityColumnValueEnabled(String tableName, boolean enabled) {
        // nothing to do
        // 'generated always' columns do not allow values to be set directly
        // 'generated by default' allows values to be set directly
    }


    /**
     * Disables all referential constraints (e.g. foreign keys) on all table in the schema
     */
    @Override
    public void disableReferentialConstraints() {
        Set<String> tableNames = getTableNames();
        for (String tableName : tableNames) {
            disableReferentialConstraints(tableName);
        }
    }

    // todo refactor (see oracle)

    protected void disableReferentialConstraints(String tableName) {
        SQLHandler sqlHandler = getSQLHandler();
        Set<String> constraintNames = sqlHandler.getItemsAsStringSet("select c.CONSTRAINTNAME from SYS.SYSCONSTRAINTS c, SYS.SYSTABLES t, SYS.SYSSCHEMAS s where c.TYPE = 'F' AND c.TABLEID = t.TABLEID  AND t.TABLENAME = '" + tableName + "' AND t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
        for (String constraintName : constraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(tableName) + " drop constraint " + quoted(constraintName));
        }
    }

    /**
     * Disables all value constraints (e.g. not null) on all tables in the schema
     */
    @Override
    public void disableValueConstraints() {
        Set<String> tableNames = getTableNames();
        for (String tableName : tableNames) {
            disableValueConstraints(tableName);
        }
    }

    // todo refactor (see oracle)

    protected void disableValueConstraints(String tableName) {
        SQLHandler sqlHandler = getSQLHandler();

        // disable all check and unique constraints
        Set<String> constraintNames = sqlHandler.getItemsAsStringSet("select c.CONSTRAINTNAME from SYS.SYSCONSTRAINTS c, SYS.SYSTABLES t, SYS.SYSSCHEMAS s where c.TYPE in ('U', 'C') AND c.TABLEID = t.TABLEID  AND t.TABLENAME = '" + tableName + "' AND t.SCHEMAID = s.SCHEMAID AND s.SCHEMANAME = '" + getSchemaName() + "'");
        for (String constraintName : constraintNames) {
            sqlHandler.executeUpdate("alter table " + qualified(tableName) + " drop constraint " + quoted(constraintName));
        }

        // retrieve the name of the primary key, since we cannot remove the not-null constraint on this column
        Set<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(tableName);

        // disable all not null constraints
        Set<String> notNullColumnNames = getNotNullColummnNames(tableName);
        for (String notNullColumnName : notNullColumnNames) {
            if (primaryKeyColumnNames.contains(notNullColumnName)) {
                // Do not remove PK constraints
                continue;
            }
            sqlHandler.executeUpdate("alter table " + qualified(tableName) + " alter column " + quoted(notNullColumnName) + " NULL");
        }
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


    /**
     * Gets the names of all primary columns of the given table.
     * <p/>
     * This info is not available in the Derby sys tables. The database meta data is used instead to retrieve it.
     *
     * @param tableName The table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    protected Set<String> getPrimaryKeyColumnNames(String tableName) {
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
    protected Set<String> getNotNullColummnNames(String tableName) {
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


}