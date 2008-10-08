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
package org.unitils.dbmaintainer.script.impl;

import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

/**
 * Tests the SQL script parser
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultScriptParserTest extends UnitilsJUnit4 {

    /* Tested instance  */
    private DefaultScriptParser defaultScriptParser;

    /* The unitils properties */
    private Properties configuration;

    /* Reader for the test script */
    private Reader testSQLScriptReader;

    /* Reader for the test script with a missing semi colon */
    private Reader testSQLMissingSemiColonScriptReader;

    /* Reader for the test script ending with a comment */
    private Reader testSQLEndingWithCommentScriptReader;

    /* Reader for the empty script */
    private Reader emptyScriptReader;


    /**
     * Initialize test fixture
     */
    @Before
    public void setUp() throws Exception {
        defaultScriptParser = new DefaultScriptParser();
        configuration = new ConfigurationLoader().loadConfiguration();
        testSQLScriptReader = new FileReader(new File(getClass().getResource("ScriptParserTest/sql-script.sql").toURI()));
        testSQLMissingSemiColonScriptReader = new FileReader(new File(getClass().getResource("ScriptParserTest/sql-script-missing-semicolon.sql").toURI()));
        testSQLEndingWithCommentScriptReader = new FileReader(new File(getClass().getResource("ScriptParserTest/sql-script-ending-with-comment.sql").toURI()));
        emptyScriptReader = new StringReader("");
    }


    /**
     * Cleans up the test by closing the streams.
     */
    @After
    public void tearDown() throws Exception {
        closeQuietly(testSQLScriptReader);
        closeQuietly(emptyScriptReader);
    }


    /**
     * Test parsing some statements out of a script.
     * 13 statements should have been found in the script.
     */
    @Test
    public void testParseStatements() throws Exception {
        defaultScriptParser.init(configuration, testSQLScriptReader);

        for (int i = 0; i < 13; i++) {
            assertNotNull(defaultScriptParser.getNextStatement());
        }
        assertNull(defaultScriptParser.getNextStatement());
    }


    /**
     * Test parsing a statements out of a script but statement does not end with a ;.
     * This should raise an exception
     */
    @Test(expected = UnitilsException.class)
    public void testParseStatements_missingEndingSemiColon() throws Exception {
        defaultScriptParser.init(configuration, testSQLMissingSemiColonScriptReader);
        defaultScriptParser.getNextStatement();
    }


    /**
     * Test parsing a statements out of a script ending with a comment.
     */
    @Test
    public void testParseStatements_endingWithComment() throws Exception {
        defaultScriptParser.init(configuration, testSQLEndingWithCommentScriptReader);
        defaultScriptParser.getNextStatement();
        defaultScriptParser.getNextStatement();
    }

    
    /**
     * Test parsing some statements out of an empty script.
     */
    @Test
    public void testParseStatements_emptyScript() throws Exception {
        defaultScriptParser.init(configuration, emptyScriptReader);
        assertNull(defaultScriptParser.getNextStatement());
    }
}
