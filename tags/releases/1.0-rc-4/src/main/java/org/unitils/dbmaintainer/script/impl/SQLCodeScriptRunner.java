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

import java.util.List;
import java.util.Properties;

import org.unitils.dbmaintainer.script.CodeScriptRunner;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;
import org.unitils.dbmaintainer.util.SQLCodeScriptParser;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLCodeScriptRunner extends BaseDatabaseTask implements CodeScriptRunner {


    /**
     * Initializes the script runner.
     *
     * @param configuration The config, not null
     */
    @Override
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
            sqlHandler.executeCodeUpdate(statement);
        }
    }


    /**
     * Parses the given string containing database code into a list of individual souce code statement. The way in which
     * individual pieces of code are recognized depends fully on the implementation. The resulting strings must be
     * individually applyable to the database.
     *
     * @param script The script content, not null
     * @return A <code>List</code> containing individual pieces of database code, each individually applyable to the
     *         database.
     */
    public List<String> parseStatements(String script) {
        SQLCodeScriptParser sqlCodeScriptParser = new SQLCodeScriptParser();
        return sqlCodeScriptParser.parseStatements(script);
    }


}
