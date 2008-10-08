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
package org.unitils.dbmaintainer.script.parsingstate;

import org.unitils.dbmaintainer.script.ParsingState;
import org.unitils.dbmaintainer.script.StatementBuilder;

/**
 * Base class for a parsing state. This will handle a character by simply adding it to the statement builder.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseParsingState implements ParsingState {


    /**
     * Handles the next character by adding it to the statement builder.
     *
     * @param previousChar The previous char, 0 if none
     * @param currentChar  The current char
     * @param nextChar     The next char, 0 if none
     * @param statementBuilder The statement builder, not null
     * @return The next parsing state, null if the end of the statement is reached
     */
    public ParsingState handleNextChar(char previousChar, char currentChar, char nextChar, StatementBuilder statementBuilder) {
        statementBuilder.append(currentChar);
        return getNextParsingState(previousChar, currentChar, nextChar, statementBuilder);
    }

    
    /**
     * Determines the next state.
     *
     * @param previousChar     The previous char, 0 if none
     * @param currentChar      The current char
     * @param nextChar         The next char, 0 if none
     * @param statementBuilder The statement builder, not null
     * @return The next parsing state, null if the end of the statement is reached
     */
    protected abstract ParsingState getNextParsingState(char previousChar, char currentChar, char nextChar, StatementBuilder statementBuilder);

}
