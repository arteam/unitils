/*
 * Copyright 2006 the original author or authors.
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

import static org.unitils.core.util.SQLUtils.*;

import java.util.Set;

/**
 * todo include schemanames in all statements where applicable
 * <p/>
 * Implementation of {@link DbSupport} for an IBM DB2 database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Frederick Beernaert
 */
public class Db2DbSupport extends DbSupport {


    /**
     * Creates support for Db2 databases.
     */
    public Db2DbSupport() {
        super("db2");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        // todo implement
        return null;
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        // todo implement
        return null;
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getItemsAsStringSet("select SEQNAME from SYSIBM.SYSSEQUENCES where SEQSCHEMA = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getItemsAsStringSet("select NAME from SYSIBM.SYSTRIGGERS where SCHEMA = '" + getSchemaName() + "'", getDataSource());
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getCurrentValueOfSequence(String sequenceName) {
        return getItemAsLong("VALUES PREVVAL FOR " + qualified(sequenceName), getDataSource());
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        executeUpdate("ALTER SEQUENCE " + qualified(sequenceName) + " RESTART WITH " + newSequenceValue, getDataSource());
        executeUpdate("VALUES NEXTVAL FOR " + qualified(sequenceName), getDataSource());
    }


    /**
     * todo add schemaname to query
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    @Override
    public Set<String> getTableConstraintNames(String tableName) {
        return getItemsAsStringSet("select CONSTNAME from SYSCAT.TABCONST where TABNAME = '" + tableName + "' and ENFORCED = 'Y'", getDataSource());
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    @Override
    public void disableConstraint(String tableName, String constraintName) {
        executeUpdate("alter table " + qualified(tableName) + " drop constraint " + constraintName, getDataSource());
    }


    /**
     * Sequences are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsSequences() {
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