/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dataset.core.preparedstatement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.loader.impl.Database;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabasePrimaryKeyTest extends UnitilsJUnit4 {

    /* Tested object */
    private Database database;

    @TestDataSource
    private DataSource dataSource;

    private Table tableWithPrimaryKeys;
    private Table tableWithoutPrimaryKeys;


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        DbSupport dbSupport = getDefaultDbSupport(configuration, sqlHandler);
        database = new Database(dbSupport);

        Schema schema = new Schema("public", false);
        tableWithPrimaryKeys = new Table("primary_keys", false);
        tableWithoutPrimaryKeys = new Table("no_primary_keys", false);
        schema.addTable(tableWithPrimaryKeys);
        schema.addTable(tableWithoutPrimaryKeys);

        dropTestTables();
        createTestTables();
    }

    @After
    public void cleanup() throws Exception {
        dropTestTables();
    }


    @Test
    public void primaryKeys() throws Exception {
        Set<String> result = database.getPrimaryKeyColumnNames(tableWithPrimaryKeys);
        assertReflectionEquals(asList("PK1", "Pk2"), result);
    }

    @Test
    public void noPrimaryKeys() throws Exception {
        Set<String> result = database.getPrimaryKeyColumnNames(tableWithoutPrimaryKeys);
        assertTrue(result.isEmpty());
    }


    protected void createTestTables() {
        executeUpdate("create table primary_keys (PK1 integer not null, \"Pk2\" integer not null, primary key (pk1, \"Pk2\"))", dataSource);
        executeUpdate("create table no_primary_keys (col1 integer)", dataSource);
    }

    protected void dropTestTables() {
        executeUpdateQuietly("drop table primary_keys", dataSource);
        executeUpdateQuietly("drop table no_primary_keys", dataSource);
    }

}