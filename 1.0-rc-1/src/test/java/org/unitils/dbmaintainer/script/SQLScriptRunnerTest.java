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

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.RegularMock;

import java.util.Properties;

/**
 * Tests the SQL script runner
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLScriptRunnerTest extends UnitilsJUnit3 {

    @RegularMock
    private StatementHandler mockStatementHandler = null;

    /* Tested instance  */
    private ScriptRunner sqlScriptRunner;

    /* Normal script, containing 2 statements and a blank line */
    private static final String NORMAL_SCRIPT =
            "CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50));\n" +
                    "CREATE TABLE ROLE (ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20));";

    /* Same as previous script except on multiple lines (containing new lines and cariage returns) */
    private static final String SCRIPT_MULTILINE =
            "CREATE TABLE PERSON\n (ID INTEGER PRIMARY KEY,\r NAME VARCHAR2(50));\n" +
                    "CREATE\n TABLE\r ROLE (ID\n INTEGER PRIMARY KEY,\r ROLENAME VARCHAR2(20));\r\n" +
                    "INSERT INTO USERS(NAME) VALUES ('This is\na multiline\rvalue');";

    /* Script containing 2 line comments */
    private static final String SCRIPT_LINE_COMMENTS =
            "-- comment1\n" +
                    "CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, -- inline comment\n" +
                    "-- comment2 /* ignored block comment*/\n" +
                    "NAME VARCHAR2(50)); -- another comment";

    /* Script containing a block comment on one line */
    private static final String SCRIPT_BLOCK_COMMENTS =
            "/* comment1 */\n" +
                    "CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, /* inline comment */\n" +
                    "/* comment2 -- ignored line comment */\n" +
                    "NAME VARCHAR2(50)); /* another comment */";

    /* Script containing a block comment that spans multiple lines */
    private static final String SCRIPT_BLOCK_COMMENT_MULTIPLE_LINES =
            "/* this is a \n" +
                    " * multiline \n" +
                    " * comment */\n" +
                    "CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, /* inline comment\n" +
                    "-- ignored line comment\n" +
                    "*/\n" +
                    "NAME VARCHAR2(50)); /* another\n" +
                    "comment */";

    /* Script containing a ; within quotes */
    private static final String SCRIPT_SEMI_COLON_IN_QUOTES =
            "COMMENT ON TABLE PERSON IS 'This ; comment ; contains ; a semi-colon';";

    /* Script containing escaped single and double quotes within quotes */
    private static final String SCRIPT_QUOTES_IN_QUOTES =
            "COMMENT ON TABLE PERSON IS 'This \"comment\" '' contains quotes and double quotes';";

    /* Script containing a line and block comment within quotes */
    private static final String SCRIPT_COMMENT_IN_QUOTES =
            "COMMENT ON TABLE PERSON IS 'This /* comment */ contains a block and -- line comment';";


    /**
     * Initialize test fixture
     */
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        sqlScriptRunner = getConfiguredDatabaseTaskInstance(ScriptRunner.class, configuration, null, mockStatementHandler);
    }


    /**
     * Test a normal script, containing 2 statements and a blank line
     */
    public void testExecute() throws Exception {
        mockStatementHandler.handle("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50))");
        mockStatementHandler.handle("CREATE TABLE ROLE (ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20))");
        replay();

        sqlScriptRunner.execute(NORMAL_SCRIPT);
    }


    /**
     * Test a script that contains new lines and cariage returns, these should have been converted to spaces
     */
    public void testExecute_multiline() throws Exception {
        mockStatementHandler.handle("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50))");
        mockStatementHandler.handle("CREATE TABLE ROLE (ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20))");
        mockStatementHandler.handle("INSERT INTO USERS(NAME) VALUES ('This is\na multiline\rvalue')");
        replay();

        sqlScriptRunner.execute(SCRIPT_MULTILINE);
    }


    /**
     * Test a script that contains line comments (these should have been ignored)
     */
    public void testExecute_lineComments() throws Exception {
        mockStatementHandler.handle("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50))");
        replay();

        sqlScriptRunner.execute(SCRIPT_LINE_COMMENTS);
    }


    /**
     * Test with block comment on a single line
     */
    public void testExecute_blockCommentsSameLine() throws Exception {
        mockStatementHandler.handle("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY,   NAME VARCHAR2(50))");
        replay();

        sqlScriptRunner.execute(SCRIPT_BLOCK_COMMENTS);
    }


    /**
     * Test with a block comment that spans multiple lines
     */
    public void testExecute_blockCommentsMultipleLines() throws Exception {
        mockStatementHandler.handle("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY,  NAME VARCHAR2(50))");
        replay();

        sqlScriptRunner.execute(SCRIPT_BLOCK_COMMENT_MULTIPLE_LINES);
    }


    /**
     * Test with a statement that contains a ; within a ''
     */
    public void testExecute_semiColonInQuotes() throws Exception {
        mockStatementHandler.handle("COMMENT ON TABLE PERSON IS 'This ; comment ; contains ; a semi-colon'");
        replay();

        sqlScriptRunner.execute(SCRIPT_SEMI_COLON_IN_QUOTES);
    }


    /**
     * Test with a statement that contains escaped single and double quotes in quotes
     */
    public void testExecute_quotesInQuotes() throws Exception {
        mockStatementHandler.handle("COMMENT ON TABLE PERSON IS 'This \"comment\" '' contains quotes and double quotes'");
        replay();

        sqlScriptRunner.execute(SCRIPT_QUOTES_IN_QUOTES);
    }


    /**
     * Test with a statement that contains single and double quotes
     */
    public void testExecute_commentsInQuotes() throws Exception {
        mockStatementHandler.handle("COMMENT ON TABLE PERSON IS 'This /* comment */ contains a block and -- line comment'");
        replay();

        sqlScriptRunner.execute(SCRIPT_COMMENT_IN_QUOTES);
    }

}
