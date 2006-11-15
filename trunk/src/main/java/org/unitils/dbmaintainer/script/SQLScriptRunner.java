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
package org.unitils.dbmaintainer.script;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.configuration.Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ScriptRunner} that runs an SQL script. All statements are passed on to
 * a {@link StatementHandler}
 */
public class SQLScriptRunner extends DatabaseTask implements ScriptRunner {

    protected void doInit(Configuration configuration) {
    }

    /**
     * @see ScriptRunner#execute(String)
     */
    public void execute(String script) throws StatementHandlerException {
        List<String> statements = loadStatements(IOUtils.toInputStream(script));
        for (String statement : statements) {
            statementHandler.handle(statement);
        }
    }

    /**
     * Parses the given <code>InputStream</code> and returns a <code>List</code> containing the individual scripts.
     *
     * @param in The InputStream containing the SQL statements
     * @return a List containing the individual SQL statements
     */
    private List<String> loadStatements(InputStream in) {
        BufferedReader br = null;
        try {
            List<String> statements = new ArrayList<String>();
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean inBlockComment = false;
            StringBuffer statement = new StringBuffer();
            while ((line = br.readLine()) != null) {
                line = StringUtils.trimToNull(line);
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                if (line.startsWith("--")) {
                    continue;
                }

                if (line.endsWith("*/")) {
                    inBlockComment = false;
                    continue;
                }

                if (inBlockComment) {
                    continue;
                }

                if (line.startsWith("/*")) {
                    inBlockComment = true;
                    continue;
                }

                //TODO WATCH out FIX so that ; can exist within comment '' 
                int pos = line.indexOf(";");
                if (pos > 0) {
                    statement.append(line.substring(0, pos));
                    statements.add(statement.toString());
                    statement = new StringBuffer();
                    continue;
                }
                statement.append(line);
                statement.append(" ");
            }
            return statements;
        } catch (IOException e) {
            throw new UnitilsException("Error while reading script", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }
}
