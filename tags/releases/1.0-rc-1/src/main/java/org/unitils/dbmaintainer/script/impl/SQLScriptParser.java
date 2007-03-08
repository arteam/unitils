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
package org.unitils.dbmaintainer.script.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for parsing statements out of sql scripts.
 * <p/>
 * All statements should be separated with a semicolon (;). The last statement will be
 * added even if it does not end with a semicolon. The semicolons will not be included in the returned statements.
 * <p/>
 * All comments in-line (--comment) and block (/ * comment * /) are removed from the statements.
 * This parser also takes quotedOrEmpty literals and double quotedOrEmpty text into account when parsing the statements and treating
 * the comments.
 * <p/>
 * New line charactars within quotes and double quotes will be inclded in the statements, other new lines will
 * be replaced by a single space.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SQLScriptParser {

    /**
     * The normal, not in quotes, not in comment state.
     */
    protected static final int NORMAL = 0;

    /**
     * The in an in-line comment (-- comment) state.
     */
    protected static final int IN_LINE_COMMENT = 1;

    /**
     * The in a block comment (/ * comment * /) state.
     */
    protected static final int IN_BLOCK_COMMENT = 2;

    /**
     * The in single quotes ('text') state.
     */
    protected static final int IN_SINGLE_QUOTES = 3;

    /**
     * The in double quotes ("text") state.
     */
    protected static final int IN_DOUBLE_QUOTES = 4;

    /**
     * The current state
     */
    protected int state = NORMAL;


    /**
     * Parses all statements out of the given script as described in the class javadoc.
     *
     * @param script the script, not null
     * @return the statements, not null
     */
    public List<String> parseStatements(String script) {
        List<String> statements = new ArrayList<String>();
        StringBuffer statement = new StringBuffer();

        // loop over all chars and pass current and next char to handle methods (use 0 for next of last char)
        char[] chars = script.toCharArray();
        int length = chars.length;
        for (int i = 0; i < length; i++) {
            boolean skipNext = handleChar(chars[i], (i + 1 < length) ? chars[i + 1] : 0, statement, statements);
            if (skipNext) {
                i++;
            }
        }

        // Check whether last statement was not ended with a ;
        if (statement.length() > 0) {
            String trimmedStatement = statement.toString().trim();
            if (trimmedStatement.length() > 0) {
                statements.add(trimmedStatement);
            }
        }
        return statements;
    }


    /**
     * Handles a char by delegating it to the method corresponding to the current state.
     *
     * @param current    the current char
     * @param next       the next char, 0 if there is no next char
     * @param statement  the current statement buffer, not null
     * @param statements the statement list, not null
     * @return true if the next char should be skipped
     */
    protected boolean handleChar(char current, char next, StringBuffer statement, List<String> statements) {
        switch (state) {
            case IN_LINE_COMMENT:
                return handleCharInLineComment(current, next, statement, statements);
            case IN_BLOCK_COMMENT:
                return handleCharInBlockComment(current, next, statement, statements);
            case IN_SINGLE_QUOTES:
                return handleCharInSingleQuotes(current, next, statement, statements);
            case IN_DOUBLE_QUOTES:
                return handleCharInDoubleQuotes(current, next, statement, statements);
            default:
                return handleCharNormal(current, next, statement, statements);
        }
    }


    /**
     * Handles a char in the normal (not quotedOrEmpty, not commented) state. Checks for the beginning of a
     * line comment (-- comment), block comment (/ * comment * /), quotedOrEmpty text ('text') and double
     * quotedOrEmpty text ("text) and changes the state correspondingly. It also checks for the ending of
     * statements by a ;. If a statement is ended the it is trimmed and added to the statement list ( ; not included).
     * New line chars (\n and \r) will be replaced by a single space.
     *
     * @param current    the current char
     * @param next       the next char, 0 if there is no next char
     * @param statement  the current statement buffer, not null
     * @param statements the statement list, not null
     * @return true if the next char should be skipped
     */
    public boolean handleCharNormal(char current, char next, StringBuffer statement, List<String> statements) {
        // check line comment
        if (current == '-' && next == '-') {
            state = IN_LINE_COMMENT;
            return true;
        }
        // check block comment
        if (current == '/' && next == '*') {
            state = IN_BLOCK_COMMENT;
            return true;
        }
        // check new line: replace by space if previous char or next char is not a space
        if (current == '\n' || current == '\r') {
            if (next != ' ' && (statement.length() == 0 || statement.charAt(statement.length() - 1) != ' ')) {
                statement.append(' ');
            }
            return false;
        }
        // check escaped characters (do not interpreted next char)
        if (current == '\\') {
            statement.append(current);
            statement.append(next);
            return true;
        }
        // check ending of statement
        if (current == ';') {
            statements.add(statement.toString().trim());
            statement.setLength(0);
            return false;
        }
        // check single and double quotes
        if (current == '\'') {
            state = IN_SINGLE_QUOTES;
        } else if (current == '"') {
            state = IN_DOUBLE_QUOTES;
        }
        // append all chars
        statement.append(current);
        return false;
    }


    /**
     * Handles a char in a line comment (-- comment). Looks for an end of line to end the comment. Skips all other chars.
     *
     * @param current    the current char
     * @param next       the next char, 0 if there is no next char
     * @param statement  the current statement buffer, not null
     * @param statements the statement list, not null
     * @return true if the next char should be skipped
     */
    protected boolean handleCharInLineComment(char current, char next, StringBuffer statement, List<String> statements) {
        // check for ending chars
        if (current == '\n' || current == '\r') {
            if (next != ' ' && (statement.length() == 0 || statement.charAt(statement.length() - 1) != ' ')) {
                statement.append(' ');
            }
            state = NORMAL;
        }
        // skip all chars
        return false;
    }


    /**
     * Handles char in a block comment. Checks for the ending of the comment * followed by /. Skips all other chars.
     *
     * @param current    the current char
     * @param next       the next char, 0 if there is no next char
     * @param statement  the current statement buffer, not null
     * @param statements the statement list, not null
     * @return true if the next char should be skipped
     */
    protected boolean handleCharInBlockComment(char current, char next, StringBuffer statement, List<String> statements) {
        // check for ending chars
        if (current == '*' && next == '/') {
            statement.append(' ');
            state = NORMAL;
            return true;
        }
        // skip all chars
        return false;
    }


    /**
     * Handles a char in a quotedOrEmpty literal ('text'). Checks for the ending quote, but ignores escaped quotes. All
     * chars, including newlines (\n \r), are appended to the statement.
     *
     * @param current    the current char
     * @param next       the next char, 0 if there is no next char
     * @param statement  the current statement buffer, not null
     * @param statements the statement list, not null
     * @return true if the next char should be skipped
     */
    public boolean handleCharInSingleQuotes(char current, char next, StringBuffer statement, List<String> statements) {
        // check for escaped quotes
        if (current == '\\' || (current == '\'' && next == '\'')) {
            statement.append(current);
            statement.append(next);
            return true;
        }
        // check for ending quote
        if (current == '\'') {
            state = NORMAL;
        }
        // append all chars
        statement.append(current);
        return false;
    }


    /**
     * Handles a char in a double quotedOrEmpty string ("text"). Checks for the ending double quote, but ignores escaped
     * double quotes. All chars, including newlines (\n \r), are appended to the statement.
     *
     * @param current    the current char
     * @param next       the next char, 0 if there is no next char
     * @param statement  the current statement buffer, not null
     * @param statements the statement list, not null
     * @return true if the next char should be skipped
     */
    public boolean handleCharInDoubleQuotes(char current, char next, StringBuffer statement, List<String> statements) {
        // check for escaped double quotes
        if (current == '\\' || (current == '"' && next == '"')) {
            statement.append(current);
            statement.append(next);
            return true;
        }
        // check for ending quote
        if (current == '"') {
            state = NORMAL;
        }
        // append all chars
        statement.append(current);
        return false;
    }

}
