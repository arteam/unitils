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
package org.unitils.dbmaintainer.script.parsingstate.impl;

import org.unitils.dbmaintainer.script.parsingstate.ParsingState;
import org.unitils.dbmaintainer.script.StatementBuilder;


/**
 * A state for parsing an in-line comment (-- comment) part of a script.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InLineCommentParsingState extends BaseParsingState {

    /**
     * The normal parsing state, that should be returned when the comment end is reached.
     */
    protected ParsingState normalParsingState;


    /**
     * Initializes the state with the normal parsing state, that should be returned when the comment end is reached..
     *
     * @param normalParsingState The normal state, not null
     */
    public void init(ParsingState normalParsingState) {
        this.normalParsingState = normalParsingState;
    }


    /**
     * Determines whether the end of the line comment is reached.
     * If that is the case, the normal parsing state is returned.
     *
     * @param previousChar The previous char, 0 if none
     * @param currentChar  The current char
     * @param nextChar     The next char, 0 if none
     * @param statementBuilder The statement builder, not null
     * @return The next parsing state, null if the end of the statement is reached
     */
    protected ParsingState getNextParsingState(char previousChar, char currentChar, char nextChar, StatementBuilder statementBuilder) {
        // check for ending chars
        if (currentChar == '\n' || currentChar == '\r') {
            return normalParsingState;
        }
        return this;
    }

}
