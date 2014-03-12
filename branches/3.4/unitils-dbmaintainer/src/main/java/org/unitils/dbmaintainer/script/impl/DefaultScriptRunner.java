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

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Reader;

import org.unitils.dbmaintainer.script.ScriptContentHandle;
import org.unitils.dbmaintainer.script.ScriptParser;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;

/**
 * Default implementation of a script runner.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultScriptRunner extends BaseDatabaseAccessor implements ScriptRunner {

    /**
     * Executes the given script.
     * <p/>
     * All statements should be separated with a semicolon (;). The last statement will be
     * added even if it does not end with a semicolon.
     *
     * @param scriptContentHandle The script as a string, not null
     */
    public void execute(ScriptContentHandle scriptContentHandle) {

        Reader scriptContentReader = null;
        try {
            // get content stream
            scriptContentReader = scriptContentHandle.openScriptContentReader();

            // create a parser
            ScriptParser scriptParser = createScriptParser(dialect);
            scriptParser.init(configuration, scriptContentReader);

            // parse and execute the statements
            String statement;
            while ((statement = scriptParser.getNextStatement()) != null) {
                sqlHandler.executeUpdateAndCommit(statement);
            }
        } finally {
            closeQuietly(scriptContentReader);
        }
    }


    /**
     * Creates a script parser.
     *
     * @return The parser, not null
     */
    protected ScriptParser createScriptParser(String dialect) {
        return getInstanceOf(ScriptParser.class, configuration, dialect);
    }
}
