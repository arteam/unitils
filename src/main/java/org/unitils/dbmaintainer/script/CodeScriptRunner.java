package org.unitils.dbmaintainer.script;

import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface CodeScriptRunner {

    /**
     * Executes the given code script
     *
     * @param script The script as a string, not null
     */
    void execute(String script) throws StatementHandlerException;
}
