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
package org.unitils.dbmaintainer.script.parsingstate;

import org.unitils.dbmaintainer.script.ParsingState;

/**
 * The initial state for Oracle. This parser adds PL/SQL statement recognition to the parser. In order for the
 * parser to recognize the ending of a code block, the SQLPlus standard must be followed, namely every code block
 * should end with line containing a single forward slash (/).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class OracleNormalParsingState extends NormalParsingState {

    /**
     * True if a code block is being parsed
     */
    protected boolean parsingCodeBlock;

    /**
     * Contains the current line
     */
    protected StringBuilder lineBuffer = new StringBuilder();

    /**
     * Contains the current statement letters (no signs..) with all double whitespace and newlines converted to single whitespace
     */
    protected StringBuilder statementBuffer = new StringBuilder();


    /**
     * Overridden to also correctly identify the starting of PL/SQL code blocks and to handle slashes / to end a statement.
     *
     * @param previousChar The previous char, 0 if none
     * @param currentChar  The current char
     * @param nextChar     The next char, 0 if none
     * @param statement    The statement that is built, not null
     * @return The next parsing state, null if the end of the statement is reached
     */
    protected ParsingState getNextParsingState(char previousChar, char currentChar, char nextChar, StringBuilder statement) {
        // track lines
        if (currentChar == '\n' || currentChar == '\r') {
            String trimmedLine = lineBuffer.toString().trim();
            lineBuffer.setLength(0);

            // if a line is found that only contains a slash (/) the end of a statementis reacheed
            if ("/".equals(trimmedLine)) {
                parsingCodeBlock = false;
                statementBuffer.setLength(0);
                return null;
            }
            return this;
        }
        lineBuffer.append(currentChar);

        // search for the beginning of a code statement
        if (!parsingCodeBlock && statementBuffer.length() < 100) {
            // track all characters, skip double whitespace and special characters
            if (Character.isWhitespace(currentChar)) {
                int statementBufferLength = statementBuffer.length();
                if (statementBufferLength != 0 && statementBuffer.charAt(statementBufferLength - 1) != ' ') {
                    statementBuffer.append(' ');
                }
            } else if (Character.isLetter(currentChar)) {
                statementBuffer.append(Character.toUpperCase(currentChar));
            }

            // check whether we found the start of a code statement
            if (isStartOfCodeStatement(statementBuffer)) {
                parsingCodeBlock = true;
                statementBuffer.setLength(0);
                return this;
            }
        }

        // Let the normal state handle the character
        ParsingState nextParsingState = super.getNextParsingState(previousChar, currentChar, nextChar, statement);

        // normal state found an end of a statement, i.e. a semi-colon (;)
        if (nextParsingState == null) {
            if (parsingCodeBlock) {
                // parsing a block of code, ignore statement end
                return this;
            }
            // found end of statement, reset state
            statementBuffer.setLength(0);
            lineBuffer.setLength(0);
        }
        return nextParsingState;
    }


    /**
     * Checks whether the statment contains the starting letters of a code statement (eg CREATE PACKAGE).
     * The statementBuffer should contain all letters (no signs) and every double space or newline converted to a single space.
     *
     * @param statement The buffer, not null
     * @return True if a code statment is recognized
     */
    protected boolean isStartOfCodeStatement(StringBuilder statement) {
        return matches("CREATE PACKAGE", statement) || matches("CREATE OR REPLACE PACKAGE", statement) ||
                matches("CREATE LIBRARY", statement) || matches("CREATE OR REPLACE LIBRARY", statement) ||
                matches("CREATE FUNCTION", statement) || matches("CREATE OR REPLACE FUNCTION", statement) ||
                matches("CREATE PROCEDURE", statement) || matches("CREATE OR REPLACE PROCEDURE", statement) ||
                matches("CREATE TRIGGER", statement) || matches("CREATE OR REPLACE TRIGGER", statement) ||
                matches("CREATE TYPE", statement) || matches("CREATE OR REPLACE TYPE", statement) ||
                matches("DECLARE", statement) || matches("BEGIN", statement);
    }


    /**
     * Utility method to check whether the given statement starts with the letters of the given text.
     *
     * @param text      The starting letters, not null
     * @param statement The statement to check
     * @return True if the statement starts with the text
     */
    protected boolean matches(String text, StringBuilder statement) {
        if (text.length() != statement.length()) {
            return false;
        }

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != statement.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}