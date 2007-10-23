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
package org.unitils.dbmaintainer.script;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbmaintainer.script.impl.SQLScriptRunner;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the SQL script runner
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLScriptRunnerTest extends UnitilsJUnit4 {


    /* Tested instance  */
    private SQLScriptRunner sqlScriptRunner;

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
    @Before
    public void setUp() throws Exception {
        sqlScriptRunner = new SQLScriptRunner();
    }


    /**
     * Test a normal script, containing 2 statements and a blank line
     */
    @Test
    public void testParseStatements() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(NORMAL_SCRIPT);
        assertLenEquals(Arrays.asList("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50))", "CREATE TABLE ROLE (ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20))"), result);
    }


    /**
     * Test a script that contains new lines and cariage returns, these should have been converted to spaces
     */
    @Test
    public void testParseStatements_multiline() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_MULTILINE);
        assertLenEquals(Arrays.asList("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50))", "CREATE TABLE ROLE (ID INTEGER PRIMARY KEY, ROLENAME VARCHAR2(20))", "INSERT INTO USERS(NAME) VALUES ('This is\na multiline\rvalue')"), result);
    }


    /**
     * Test a script that contains line comments (these should have been ignored)
     */
    @Test
    public void testParseStatements_lineComments() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_LINE_COMMENTS);
        assertLenEquals(Arrays.asList("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY, NAME VARCHAR2(50))"), result);
    }


    /**
     * Test with block comment on a single line
     */
    @Test
    public void testParseStatements_blockCommentsSameLine() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_BLOCK_COMMENTS);
        assertLenEquals(Arrays.asList("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY,   NAME VARCHAR2(50))"), result);
    }


    /**
     * Test with a block comment that spans multiple lines
     */
    @Test
    public void testParseStatements_blockCommentsMultipleLines() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_BLOCK_COMMENT_MULTIPLE_LINES);
        assertLenEquals(Arrays.asList("CREATE TABLE PERSON (ID INTEGER PRIMARY KEY,  NAME VARCHAR2(50))"), result);
    }


    /**
     * Test with a statement that contains a ; within a ''
     */
    @Test
    public void testParseStatements_semiColonInQuotes() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_SEMI_COLON_IN_QUOTES);
        assertLenEquals(Arrays.asList("COMMENT ON TABLE PERSON IS 'This ; comment ; contains ; a semi-colon'"), result);
    }


    /**
     * Test with a statement that contains escaped single and double quotes in quotes
     */
    @Test
    public void testParseStatements_quotesInQuotes() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_QUOTES_IN_QUOTES);
        assertLenEquals(Arrays.asList("COMMENT ON TABLE PERSON IS 'This \"comment\" '' contains quotes and double quotes'"), result);
    }


    /**
     * Test with a statement that contains single and double quotes
     */
    @Test
    public void testParseStatements_commentsInQuotes() throws Exception {
        List<String> result = sqlScriptRunner.parseStatements(SCRIPT_COMMENT_IN_QUOTES);
        assertLenEquals(Arrays.asList("COMMENT ON TABLE PERSON IS 'This /* comment */ contains a block and -- line comment'"), result);
    }

}
