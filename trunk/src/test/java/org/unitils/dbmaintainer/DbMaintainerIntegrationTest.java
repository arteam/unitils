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
package org.unitils.dbmaintainer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.util.SQLTestUtils;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.SQLUnitils;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.thirdparty.org.apache.commons.io.FileUtils;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import javax.sql.DataSource;
import java.io.*;
import java.util.Properties;
import java.util.Set;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbMaintainerIntegrationTest {

    private static final String INITIAL_INCREMENTAL_1 = "initial_incremental_1";
    private static final String INITIAL_INCREMENTAL_2 = "initial_incremental_2";
    private static final String INITIAL_REPEATABLE = "initial_repeatable";
    private static final String NEW_INCREMENTAL = "new_incremental";
    private static final String NEW_REPEATABLE = "new_repeatable";
    private static final String UPDATED_REPEATABLE = "updated_repeatable";
    private static final String UPDATED_INCREMENTAL_1 = "updated_incremental_1";
    private static final String NEW_INCREMENTAL_LOWER_INDEX = "new_incremental_lower_index";
    private static final String SECOND_LOCATION_INCREMENTAL = "second_location_incremental";
    private static final String SECOND_LOCATION_REPEATABLE = "second_location_repeatable";
    private static final String BEFORE_INITIAL_TABLE = "before_initial";

    @TestDataSource
    private DataSource dataSource = null;

    private File scriptsLocation1;
    private File scriptsLocation2;
    private DbSupport dbSupport;
    private Properties configuration;

    @Before
    public void init() {
        scriptsLocation2 = new File(System.getProperty("java.io.tmpdir") + "/dbmaintain-integrationtest/scripts2");
        scriptsLocation1 = new File(System.getProperty("java.io.tmpdir") + "/dbmaintain-integrationtest/scripts1");
        initConfiguration();
        clearScriptsDirectory();
        clearTestDatabase();
    }

    @After
    public void cleanup() {
        clearScriptsDirectory();
        clearTestDatabase();
    }

    @Test
    public void initial() {
        addInitialScripts();
        updateDatabase();
        assertTablesExist(INITIAL_INCREMENTAL_1, INITIAL_REPEATABLE, INITIAL_INCREMENTAL_2);
    }

    @Test
    public void addIncremental() {
        addInitialScripts();
        updateDatabase();
        assertTablesDontExist(NEW_INCREMENTAL);
        newIncrementalScript();
        updateDatabase();
        assertTablesExist(NEW_INCREMENTAL);
    }

    @Test
    public void addRepeatable() {
        addInitialScripts();
        updateDatabase();
        assertTablesDontExist(NEW_REPEATABLE);
        newRepeatableScript();
        updateDatabase();
        assertTablesExist(NEW_REPEATABLE);
    }

    @Test
    public void updateRepeatable() {
        addInitialScripts();
        updateDatabase();
        updateRepeatableScript();
        updateDatabase();
        assertTablesExist(UPDATED_REPEATABLE);
    }

    @Test
    public void updateIncremental_fromScratchEnabled() {
        enableFromScratch();
        addInitialScripts();
        updateDatabase();
        updateIncrementalScript();
        updateDatabase();
        assertTablesDontExist(INITIAL_INCREMENTAL_1);
        assertTablesExist(UPDATED_INCREMENTAL_1);
    }

    @Test
    public void updateIncremental_fromScratchDisabled() {
        addInitialScripts();
        updateDatabase();
        updateIncrementalScript();
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            // TODO
            //assertMessageContains(e.getMessage(), "existing", "modified", INITIAL_INCREMENTAL_1 + ".sql");
        }
    }

    @Test
    public void addIncrementalWithLowerIndex_fromScratchEnabled() {
        enableFromScratch();
        addInitialScripts();
        updateDatabase();
        addIncrementalScriptWithLowerIndex();
        updateDatabase();
        assertTablesExist(NEW_INCREMENTAL_LOWER_INDEX);
    }

    @Test
    public void addIncrementalWithLowerIndex_fromScratchDisabled() {
        addInitialScripts();
        updateDatabase();
        addIncrementalScriptWithLowerIndex();
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            // TODO
            //assertMessageContains(e.getMessage(), "added", "lower index", NEW_INCREMENTAL_LOWER_INDEX + ".sql");
        }
    }

    @Test
    public void removeExistingIncremental_fromScratchEnabled() {
        enableFromScratch();
        addInitialScripts();
        updateDatabase();
        removeIncrementalScript();
        updateDatabase();
        assertTablesDontExist(INITIAL_INCREMENTAL_1);
    }

    @Test
    public void removeExistingIncremental_fromScratchDisabled() {
        addInitialScripts();
        updateDatabase();
        removeIncrementalScript();
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            // TODO
            //assertMessageContains(e.getMessage(), "removed", INITIAL_INCREMENTAL_1 + ".sql");
        }
    }

    @Test
    public void errorInIncrementalScript() {
        addInitialScripts();
        errorInInitialScript();
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            // TODO
            //assertMessageContains(e.getMessage(), "error", INITIAL_INCREMENTAL_2 + ".sql");
        }
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "previous run", "error", INITIAL_INCREMENTAL_2 + ".sql");
        }
        fixErrorInInitialScript();
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "existing", "modified"/*, INITIAL_INCREMENTAL_2 + ".sql"*/);
        }
        enableFromScratch();
        updateDatabase();
        assertTablesExist(INITIAL_INCREMENTAL_1, INITIAL_REPEATABLE, INITIAL_INCREMENTAL_2);
    }

    @Test
    public void errorInRepeatableScript() {
        addInitialScripts();
        //createErrorInRepeatableScript();
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            // TODO
            //assertMessageContains(e.getMessage(), "error", INITIAL_INCREMENTAL_2 + ".sql");
        }
        try {
            updateDatabase();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "previous run", "error", INITIAL_REPEATABLE + ".sql");
        }
        //fixErrorInRepeatableScript();
        updateDatabase();
        assertTablesExist(INITIAL_INCREMENTAL_1, INITIAL_REPEATABLE, INITIAL_INCREMENTAL_2);
    }

    @Test
    public void moreThanOneScriptLocation() {
        configureSecondScriptLocation();
        addInitialScripts();
        addSecondLocationScripts();
        updateDatabase();
        assertTablesExist(INITIAL_INCREMENTAL_1, INITIAL_REPEATABLE, INITIAL_INCREMENTAL_2, SECOND_LOCATION_INCREMENTAL, SECOND_LOCATION_REPEATABLE);
    }


    /**
     * Verifies that, if the dbmaintain_scripts table doesn't exist yet, and the autoCreateExecutedScriptsInfoTable property is set to true,
     * we start with a from scratch update
     */
    @Test
    public void initialFromScratchUpdate() {
        createTable(BEFORE_INITIAL_TABLE);
        addInitialScripts();
        updateDatabase();
        assertTablesDontExist(BEFORE_INITIAL_TABLE);
    }
    
    /**
     * Verifies that, if the dbmaintain_scripts table doesn't exist yet, and the autoCreateExecutedScriptsInfoTable property is set to true,
     * we start with a from scratch update
     */
    @Test
    public void noInitialFromScratchUpdateIfFromScratchDisabled() {
        disableFromScratch();
        createTable(BEFORE_INITIAL_TABLE);
        addInitialScripts();
        updateDatabase();
        assertTablesExist(BEFORE_INITIAL_TABLE);
    }


    private void createTable(String tableName) {
        SQLUnitils.executeUpdate("create table " + tableName + " (test varchar(10))", dbSupport.getSQLHandler().getDataSource());
    }
    
    private void errorInInitialScript() {
        createScript("02_latest/01_" + INITIAL_INCREMENTAL_2 + ".sql", "this is an error;");
    }

    private void fixErrorInInitialScript() {
        createScript("02_latest/01_" + INITIAL_INCREMENTAL_2 + ".sql", "create table " + INITIAL_INCREMENTAL_2 + " (test varchar(10));");
    }

    private void removeIncrementalScript() {
        removeScript("01_base/01_" + INITIAL_INCREMENTAL_1 + ".sql");
    }

    private void addIncrementalScriptWithLowerIndex() {
        createScript("01_base/03_" + NEW_INCREMENTAL_LOWER_INDEX + ".sql", "create table " + NEW_INCREMENTAL_LOWER_INDEX + " (test varchar(10));");
    }

    private void assertMessageContains(String message, String... subStrings) {
        for (String subString : subStrings) {
            assertTrue("Expected message to contain substring " + subString + ", but it doesn't.\nMessage was: " + message,
                    message.contains(subString));
        }
    }

    private void enableFromScratch() {
        configuration.put(DBMaintainer.PROPKEY_FROM_SCRATCH_ENABLED, "true");
    }
    
    private void disableFromScratch() {
        configuration.put(DBMaintainer.PROPKEY_FROM_SCRATCH_ENABLED, "false");
    }

    private void updateIncrementalScript() {
        createScript("01_base/01_" + INITIAL_INCREMENTAL_1 + ".sql", "create table " + UPDATED_INCREMENTAL_1 + "(test varchar(10));");
    }

    private void updateRepeatableScript() {
        createScript("01_base/" + INITIAL_REPEATABLE + ".sql", "drop table " + INITIAL_REPEATABLE + " if exists;\n" +
                "drop table " + UPDATED_REPEATABLE + " if exists;\n" +
                "create table " + UPDATED_REPEATABLE + "(test varchar(10));");
    }

    private void newIncrementalScript() {
        createScript("02_latest/02_" + NEW_INCREMENTAL + ".sql", "create table " + NEW_INCREMENTAL + " (test varchar(10));");
    }

    private void newRepeatableScript() {
        createScript("02_latest/" + NEW_REPEATABLE + ".sql", "drop table " + NEW_REPEATABLE + " if exists;\n" +
                "create table " + NEW_REPEATABLE + " (test varchar(10));");
    }

    private void addInitialScripts() {
        createScript("01_base/01_" + INITIAL_INCREMENTAL_1 + ".sql", "create table " + INITIAL_INCREMENTAL_1 + "(test varchar(10));");
        createScript("01_base/" + INITIAL_REPEATABLE + ".sql", "drop table " + INITIAL_REPEATABLE + " if exists;\n" +
                "create table " + INITIAL_REPEATABLE + "(test varchar(10));");
        createScript("02_latest/01_" + INITIAL_INCREMENTAL_2 + ".sql", "create table " + INITIAL_INCREMENTAL_2 + "(test varchar(10));");
    }

    private void addSecondLocationScripts() {
        createScript(scriptsLocation2, "01_base/02_" + SECOND_LOCATION_INCREMENTAL + ".sql", "create table " + SECOND_LOCATION_INCREMENTAL + "(test varchar(10));");
        createScript(scriptsLocation2, "01_base/" + SECOND_LOCATION_REPEATABLE + ".sql", "create table " + SECOND_LOCATION_REPEATABLE + "(test varchar(10));");
    }


    private void assertTablesExist(String... tables) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String table : tables) {
            assertTrue(table + " does not exist", tableNames.contains(dbSupport.toCorrectCaseIdentifier(table)));
        }
    }

    private void assertTablesDontExist(String... tables) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String table : tables) {
            assertFalse(table + " exists, while it shouldn't", tableNames.contains(dbSupport.toCorrectCaseIdentifier(table)));
        }
    }

    private void updateDatabase() {
        DBMaintainer dbMaintainer = new DBMaintainer(configuration, new DefaultSQLHandler(dataSource));
        dbMaintainer.updateDatabase();
    }

    private void clearTestDatabase() {
        SQLTestUtils.dropTestTables(dbSupport, "dbmaintain_scripts", INITIAL_INCREMENTAL_1, INITIAL_INCREMENTAL_2,
                INITIAL_REPEATABLE, NEW_INCREMENTAL, NEW_REPEATABLE, UPDATED_INCREMENTAL_1, UPDATED_REPEATABLE,
                NEW_INCREMENTAL_LOWER_INDEX, SECOND_LOCATION_INCREMENTAL, SECOND_LOCATION_REPEATABLE);
    }

    private void createScript(String relativePath, String scriptContent) {
        createScript(scriptsLocation1, relativePath, scriptContent);
    }

    private void createScript(File scriptsLocation, String relativePath, String scriptContent) {
        Writer fileWriter = null;
        try {
            File scriptFile = new File(scriptsLocation.getAbsolutePath() + "/" + relativePath);
            scriptFile.getParentFile().mkdirs();
            fileWriter = new FileWriter(scriptFile);
            IOUtils.copy(new StringReader(scriptContent), fileWriter);
        } catch (IOException e) {
            throw new UnitilsException(e);
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

    private void removeScript(String relativePath) {
        File scriptFile = new File(scriptsLocation1.getAbsolutePath() + "/" + relativePath);
        scriptFile.delete();
    }

    private void clearScriptsDirectory() {
        try {
            FileUtils.cleanDirectory(scriptsLocation1);
            FileUtils.cleanDirectory(scriptsLocation2);
        } catch (IOException e) {
            throw new UnitilsException(e);
        } catch (IllegalArgumentException e) {
            // Ignored
        }
    }

    private void configureSecondScriptLocation() {
        configuration.put("dbMaintainer.script.locations", scriptsLocation1.getAbsolutePath() + "," +
                scriptsLocation2.getAbsolutePath());
    }

    private void initConfiguration() {
        configuration = new ConfigurationLoader().getDefaultConfiguration();
        configuration.put("database.dialect", "hsqldb");
        configuration.put("database.driverClassName", "org.hsqldb.jdbcDriver");
        configuration.put("database.url", "jdbc:hsqldb:mem:unitils");
        configuration.put("database.userName", "sa");
        configuration.put("database.password", "");
        configuration.put("database.schemaNames", "PUBLIC");
        configuration.put("dbMaintainer.autoCreateExecutedScriptsTable", "true");
        configuration.put("dbMaintainer.script.locations", scriptsLocation1.getAbsolutePath());
        configuration.put("dbMaintainer.generateDataSetStructure.enabled", "false");

        Unitils unitils = new Unitils();
        Unitils.setInstance(unitils);
        unitils.init(configuration);

        dataSource = DatabaseUnitils.getDataSource();
        dbSupport = DbSupportFactory.getDefaultDbSupport(configuration, new DefaultSQLHandler(dataSource));
        System.out.println("dbsupport: " + dbSupport);
    }
}
