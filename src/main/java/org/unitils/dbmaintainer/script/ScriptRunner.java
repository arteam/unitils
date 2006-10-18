/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.script;

import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Defines the contract for an executer of a database update script.
 */
public interface ScriptRunner {

    /**
     * Executes the given script
     * 
     * @param script
     * @throws StatementHandlerException
     */
    void execute(String script) throws StatementHandlerException;

}
