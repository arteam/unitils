/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.script;

import be.ordina.unitils.db.handler.StatementHandler;
import be.ordina.unitils.db.handler.StatementHandlerException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ScriptRunner} that runs an SQL script
 */
public class SQLScriptRunner implements ScriptRunner {

    /**
     * Logger to which the statements which are executed are logged
     */
    private static final Logger logger = Logger.getLogger(SQLScriptRunner.class);

    /**
     * StatementHandler to which the individual SQL statements in the script are passed
     */
    private StatementHandler statementHandler;

    /**
     * Constructs a new <code>SQLScriptRunner</code>. The SQL statements in the scripts will org handled by the given
     * {@link StatementHandler}
     *
     * @param statementHandler Will handle the SQL statements in the executed scripts
     */
    public SQLScriptRunner(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
    }

    /**
     * @see ScriptRunner#execute(String)
     */
    public void execute(String script) throws StatementHandlerException {
        List<String> statements = loadStatements(IOUtils.toInputStream(script));
        for (String statement : statements) {
            logger.info("Executing statement: " + statement);
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
            throw new RuntimeException("Error while reading script", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
    }
}
