/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.script;

import be.ordina.unitils.db.maintainer.VersionScriptPair;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * Interface that gives access to a source that provides scripts for updating the database to a given state
 */
public interface ScriptSource {

    /**
     * Initialize using the properties in the given <code>Properties</code> object
     *
     * @param properties
     */
    void init(Properties properties);

    /**
     * Returns a <code>List<VersionScriptPair></code> containing the statements that will update the database from the
     * given currentVersion to the latest one.
     *
     * @param currentVersion The current database version
     * @return A List<VersionScriptPair> containing the scripts that need to be executed to update the database
     * version to the latest state together with their version
     */
    List<VersionScriptPair> getScripts(Long currentVersion);

}
