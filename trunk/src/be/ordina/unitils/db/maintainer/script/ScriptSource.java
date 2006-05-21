/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.script;

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
     * Returns a <code>String</code> that describes the database change, that will update the database from state
     * version -1 to version, if the given version is supported, and null otherwise
     *
     * @param version The version for which the change is requested
     * @return A string describing the database change associated with the given version, or <code>null</code>
     *         if none exists.
     */
    String getScript(long version);

}
