/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer.script;

import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;

/**
 * Defines the contract for a source that provides scripts for updating the database to a given state.<br> 
 * Scripts are provides a {@link org.unitils.dbmaintainer.maintainer.VersionScriptPair} objects, which indicate
 * which scripts should be executed, to update the database to which state.<br>
 */
public interface ScriptSource {

    /**
     * Initialize using the properties in the given <code>Configuration</code> object
     * @param configuration 
     */
    void init(Configuration configuration);

    /**
     * This methods returns true if one or more scripts that have a version index equal to or lower than
     * the index specified by the given version object has been modified since the timestamp specfied by 
     * the given version.
     *
     * @param currentVersion
     * @return true if an existing script has been modified, false otherwise
     */
    public boolean existingScriptsModified(Version currentVersion);

    /**
     * Returns a <code>List<VersionScriptPair></code> containing the statements that will update the database from the
     * given version to the latest one.
     *
     * @param currentVersion The current database version
     * @return A List<VersionScriptPair> containing the scripts that need to be executed to update the database
     *         version to the latest one.
     */
    List<VersionScriptPair> getNewScripts(Version currentVersion);
    
    /**
     * Returns a <code>List<VersionScriptPair></code> containing all statements that complete create the database
     * from scratch.
     * 
     * @return a <code>List<VersionScriptPair></code> containing all statements that complete create the database
     * from scratch.
     */
    List<VersionScriptPair> getAllScripts();

}
