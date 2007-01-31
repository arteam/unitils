package org.unitils.dbmaintainer.script;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseScriptRunner extends DatabaseTask {

    /**
     * Initializes the script runner.
     *
     * @param configuration The config, not null
     */
    protected void doInit(Configuration configuration) {
    }

    /**
     * Executes the given script
     *
     * @param script The script as a string, not null
     */
    public void execute(String script) throws StatementHandlerException {
        List<String> statements = loadStatements(IOUtils.toInputStream(script));
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
            return parseStatements(script);

        } catch (IOException e) {
            throw new UnitilsException("Error while reading script", e);

        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    protected abstract List<String> parseStatements(String script);
}
