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
package org.unitils.dbmaintainer.script.impl;

import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.SQLHandler;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.database.SQLUnitils.isEmpty;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.script.Script;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Test class for the DefaultScriptRunner.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultScriptRunnerTest extends UnitilsJUnit4 {

    /* The tested object */
    private DefaultScriptRunner defaultScriptRunner;

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected DataSource dataSource = null;

    /* A test script that will create 2 tables: table1, table2 */
    private Script script1;

    /* A test script that will create 1 table: table3 */
    private Script script2;


    /**
     * Test fixture. Configures the ConstraintsDisabler with the implementation that matches the configured database
     * dialect
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        defaultScriptRunner = new DefaultScriptRunner();
        defaultScriptRunner.init(configuration, new SQLHandler(dataSource));

        script1 = new Script("test-script1.sql", getClass().getResource("DefaultScriptRunnerTest/test-script1.sql"), null);
        script2 = new Script("test-script2.sql", getClass().getResource("DefaultScriptRunnerTest/test-script2.sql"), null);

        cleanupTestDatabase();
    }


    /**
     * Drops the test tables, to avoid influencing other tests
     */
    @After
    public void tearDown() throws Exception {
        cleanupTestDatabase();
    }


    /**
     * Tests running some scripts.
     */
    @Test
    public void testExecute() throws Exception {
        defaultScriptRunner.execute(script1);
        defaultScriptRunner.execute(script2);

        // all tables should exist (otherwhise an exception will be thrown)
        assertTrue(isEmpty("table1", dataSource));
        assertTrue(isEmpty("table2", dataSource));
        assertTrue(isEmpty("table3", dataSource));
    }


    /**
     * Drops the test tables
     */
    private void cleanupTestDatabase() {
        executeUpdateQuietly("drop table table1", dataSource);
        executeUpdateQuietly("drop table table2", dataSource);
        executeUpdateQuietly("drop table table3", dataSource);
    }

}