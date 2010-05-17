/*
 * Copyright Unitils.org
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
package org.unitils.dataset.core;

/**
 * The data set settings.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetSettings {

    /* The token that identifies a literal, e.g. for '=literal value' */
    protected char literalToken;
    /* The token that identifies a variable, e.g. $ for $0 $1 etc */
    protected char variableToken;
    /* True if the schema name is case sensitive */
    protected boolean caseSensitive;


    /**
     * Creates a data set
     *
     * @param literalToken  The token that identifies a literal, e.g. for '=literal value'
     * @param variableToken The token that identifies a variable, e.g. $ for $0 $1 etc
     * @param caseSensitive True if the name of the schema is case sensitive
     */
    public DataSetSettings(char literalToken, char variableToken, boolean caseSensitive) {
        this.literalToken = literalToken;
        this.variableToken = variableToken;
        this.caseSensitive = caseSensitive;
    }

    /**
     * @return The token that identifies a literal, e.g. for '=literal value'
     */
    public char getLiteralToken() {
        return literalToken;
    }

    /**
     * @return The token that identifies a variable, e.g. $ for $0 $1 etc
     */
    public char getVariableToken() {
        return variableToken;
    }

    /**
     * @return True if the name of the schema is case sensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }
}