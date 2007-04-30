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

import static org.unitils.core.util.SQLUtils.executeUpdate;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;

import java.util.List;
import java.util.Properties;

/**
 * Implementation of {@link ScriptRunner} that runs an SQL script.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLScriptRunner extends BaseDatabaseTask implements ScriptRunner {


    /**
     * Initializes the script runner.
     *
     * @param configuration The config, not null
     */
    protected void doInit(Properties configuration) {
    }


    /**
     * Executes the given script
     *
     * @param script The script as a string, not null
     */
    public void execute(String script) {
        List<String> statements = parseStatements(script);
        for (String statement : statements) {
            executeUpdate(statement, dataSource);
        }
    }


    /**
     * Parses all statements out of the given sql script.
     * <p/>
     * All statements should be separated with a semicolon (;). The last statement will be
     * added even if it does not end with a semicolon. The semicolons will not be included in the returned statements.
     * <p/>
     * All comments in-line (--comment) and block (/ * comment * /) are removed from the statements.
     * This parser also takes quotedOrEmpty literals and double quotedOrEmpty text into account when parsing the statements and treating
     * the comments.
     * <p/>
     * New line charactars in the statements will be replaced by spaces.
     *
     * @param script The sql script, not null
     * @return The statements, not null
     */
    public List<String> parseStatements(String script) {
        SQLScriptParser sqlScriptParser = new SQLScriptParser();
        return sqlScriptParser.parseStatements(script);
    }

}
