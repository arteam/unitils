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
package org.unitils.dbmaintainer.script;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.easymock.EasyMockTestCase;
import org.unitils.easymock.annotation.Mock;
import org.unitils.core.ConfigurationLoader;
import org.apache.commons.configuration.Configuration;

/**
 * Tests the SQL script runner
 */
public class SQLScriptRunnerTest extends EasyMockTestCase {

    @Mock
    private StatementHandler mockStatementHandler = null;

    /* Tested instance  */
    private ScriptRunner sqlScriptRunner;

    /**
     * Normal script, containing 2 statements and a blank line
     */
    private static final String DEFAULT_SCRIPT = "CREATE TABLE PERSON (\n" +
            "ID INTEGER PRIMARY KEY, NAME VARCHAR2(50)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE ROLE (\n" +
            "ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20));\n";

    /**
     * Script containing 2 line comments
     */
    private static final String SCRIPT_WITH_LINE_COMMENTS = "CREATE TABLE PERSON (\n" +
            "-- comment1\n" +
            "ID INTEGER PRIMARY KEY, \n" +
            "-- comment2\n" +
            "NAME VARCHAR2(50)\n" +
            ");\n";

    /**
     * Script containing a block comment on one line
     */
    private static final String SCRIPT_WITH_BLOCK_COMMENT_SAMELINE = "CREATE TABLE PERSON (\n" +
            "/* comment */\n" +
            "ID INTEGER PRIMARY KEY, \n" +
            "NAME VARCHAR2(50)\n" +
            ");\n";

    /**
     * Script containing a block comment that spans multiple lines
     */
    private static final String SCRIPT_WITH_BLOCK_COMMENT_MULTIPLE_LINES = "CREATE TABLE PERSON (\n" +
            "/* this is a \n" +
            " comment */\n" +
            "ID INTEGER PRIMARY KEY, \n" +
            "NAME VARCHAR2(50)\n" +
            ");\n";

    /**
     * The first statement of the script
     */
    private static final String STATEMENT_1 = "CREATE TABLE PERSON ( " +
            "ID INTEGER PRIMARY KEY, NAME VARCHAR2(50) )";

    /**
     * The second statement of the script
     */
    private static final String STATEMENT_2 = "CREATE TABLE ROLE ( " +
            "ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20))";


    /**
     * Initialize test fixture
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        sqlScriptRunner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(ScriptRunner.class, configuration,
                null, mockStatementHandler);
    }

    /**
     * Test a normal script, containing 2 statements and a blank line
     *
     * @throws Exception
     */
    public void testExecute() throws Exception {
        mockStatementHandler.handle(STATEMENT_1);
        mockStatementHandler.handle(STATEMENT_2);
        replay();

        sqlScriptRunner.execute(DEFAULT_SCRIPT);

        verify();
    }

    /**
     * Test with line comments
     */
    public void testExecute_lineComments() throws Exception {
        mockStatementHandler.handle(STATEMENT_1);
        replay();

        sqlScriptRunner.execute(SCRIPT_WITH_LINE_COMMENTS);

        verify();
    }

    /**
     * This test doesn't function yet
     */
    public void IGNOREtestExecute_blockCommentsSameLine() throws Exception {
        mockStatementHandler.handle(STATEMENT_1);
        replay();

        sqlScriptRunner.execute(SCRIPT_WITH_BLOCK_COMMENT_SAMELINE);

        verify();
    }

    /**
     * Test with a block comment that spans multiple lines
     */
    public void testExecute_blockCommentsMultipleLines() throws Exception {
        mockStatementHandler.handle(STATEMENT_1);
        replay();

        sqlScriptRunner.execute(SCRIPT_WITH_BLOCK_COMMENT_MULTIPLE_LINES);

        verify();
    }

}
