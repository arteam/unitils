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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

/**
 * Tests the Oracle SQL and PL-SQL script parser
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class OracleScriptParserTest extends UnitilsJUnit4 {

    /* Tested instance  */
    private OracleScriptParser oracleScriptParser;

    /* The unitils properties */
    private Properties configuration;

    /* Reader for the standard SQL test script */
    private Reader testSQLScriptReader;

    /* Reader for the Oracle PL-SQL test script */
    private Reader testPLSQLScriptReader;

    /* Reader for the empty script */
    private Reader emptyScriptReader;


    /**
     * Initialize test fixture
     */
    @Before
    public void setUp() throws Exception {
        oracleScriptParser = new OracleScriptParser();
        configuration = new ConfigurationLoader().loadConfiguration();
        testSQLScriptReader = new FileReader(new File(getClass().getResource("ScriptParserTest/sql-script.sql").toURI()));
        testPLSQLScriptReader = new FileReader(new File(getClass().getResource("ScriptParserTest/plsql-script.sql").toURI()));
        emptyScriptReader = new StringReader("");
    }


    /**
     * Cleans up the test by closing the streams.
     */
    @After
    public void tearDown() throws Exception {
        closeQuietly(testSQLScriptReader);
        closeQuietly(testPLSQLScriptReader);
        closeQuietly(emptyScriptReader);
    }


    /**
     * Test parsing some statements out of a script.
     * 13 statements should have been found in the script.
     */
    @Test
    public void testParseStatements_SQL() throws Exception {
        oracleScriptParser.init(configuration, testSQLScriptReader);

        for (int i = 0; i < 13; i++) {
            assertNotNull(oracleScriptParser.getNextStatement());
        }
        assertNull(oracleScriptParser.getNextStatement());
    }


    /**
     * Test parsing some statements out of a PL-SQL script.
     * 4 statements should have been found in the script.
     */
    @Test
    public void testParseStatements_PLSQL() throws Exception {
        oracleScriptParser.init(configuration, testPLSQLScriptReader);

        for (int i = 0; i < 5; i++) {
            assertNotNull(oracleScriptParser.getNextStatement());
        }
        assertNull(oracleScriptParser.getNextStatement());
    }


    /**
     * Test parsing some statements out of an empty script.
     */
    @Test
    public void testParseStatements_emptyScript() throws Exception {
        oracleScriptParser.init(configuration, emptyScriptReader);
        assertNull(oracleScriptParser.getNextStatement());
    }
}