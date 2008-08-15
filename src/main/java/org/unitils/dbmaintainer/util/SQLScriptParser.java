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
package org.unitils.dbmaintainer.util;

import java.util.List;

/**
 * todo javadoc
 * <p/>
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
public class SQLScriptParser extends BaseScriptParser {


    public SQLScriptParser() {
        super(false, false);
    }


    @Override
    protected boolean reachedEndOfStatement(char[] script, int currentIndexInScript, StatementBuilder statementBuilder, List<String> statements) {
        return (getCurrentChar(script, currentIndexInScript) == ';');
    }
}
