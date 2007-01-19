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

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.script.StatementHandler;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementation of {@link StatementHandler} that writes each statement to a file. The fileName is specified
 * on invocation of the {@link #init} method with a <code>Configuration</code> object that contains the property
 * with the key {@link #PROPKEY_FILENAME}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class FileStatementHandler implements StatementHandler {

    /**
     * The key of the property that specifies the name of the file to which the statements are written.
     */
    public static final String PROPKEY_FILENAME = "fileStatementHandler.fileName";

    /* The name of the file to which the statements are written */
    private String fileName;


    /**
     * @see StatementHandler#init(Configuration,DataSource)
     */
    public void init(Configuration configuration, DataSource dataSource) {

        fileName = configuration.getString(PROPKEY_FILENAME);
    }


    /**
     * Writes the given statement to the file, configured by the property {@link #PROPKEY_FILENAME}
     *
     * @see StatementHandler#handle(String)
     */
    public void handle(String statement) throws StatementHandlerException {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName, true));
            pw.println(statement + ";\n");
            pw.close();

        } catch (IOException e) {
            throw new StatementHandlerException("Error while trying to write to file " + fileName, e);
        }
    }

}
