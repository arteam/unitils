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

import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.toInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of {@link ScriptRunner} that runs an SQL script. All statements are passed on to
 * a {@link org.unitils.dbmaintainer.script.StatementHandler}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLScriptRunner extends DatabaseTask implements ScriptRunner {


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
    public void execute(String script) throws StatementHandlerException {
        List<String> statements = loadStatements(toInputStream(script));
        for (String statement : statements) {
            statementHandler.handle(statement);
        }
    }


    /**
     * Parses the given stream and returns the list of statements that were in the script.
     * The stream will be closed afterwards.
     *
     * @param in The Stream containing the SQL statements, not null
     * @return The individual SQL statements, not null
     */
    private List<String> loadStatements(InputStream in) {
        try {
            String script = IOUtils.toString(in);
            return dbSupport.parseStatements(script);

        } catch (IOException e) {
            throw new UnitilsException("Error while reading script", e);
        } finally {
            closeQuietly(in);
        }
    }
}
