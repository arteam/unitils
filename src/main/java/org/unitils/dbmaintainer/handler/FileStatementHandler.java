/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.handler;

import org.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementation of {@link StatementHandler} that writes each statement to a file. The fileName is specified
 * on invocation of the <code>init</code> method with a <code>Properties</code> object that contains the property
 * with the key <code>PROPKEY_FILENAME</code>.
 */
public class FileStatementHandler implements StatementHandler {

    /**
     * The key of the property that specifies the name of the file to which the statements are written.
     */
    public static final String PROPKEY_FILENAME = "fileStatementHandler.fileName";

    /**
     * The name of the file to which the statements are written
     */
    private String fileName;

    /**
     * @see StatementHandler#init(javax.sql.DataSource)
     */
    public void init(DataSource dataSource) {

        Configuration configuration = UnitilsConfiguration.getInstance();
        fileName = configuration.getString(PROPKEY_FILENAME);
    }

    /**
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
