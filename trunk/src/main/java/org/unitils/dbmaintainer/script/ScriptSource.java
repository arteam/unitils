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

import java.util.List;

import org.unitils.dbmaintainer.util.DatabaseTask;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;

/**
 * Defines the contract for a source that provides scripts for updating the database to a given state.<br>
 * Database update scripts are provided as {@link VersionScriptPair} objects, which indicate which scripts should be
 * executed, to update the database to a given state.
 * Code scripts (stored procedures etc) are provides as {@link Script} objects. Database code scripts are regarded to
 * be repeatably executable on the database. Therefore, all code scripts are always returned as a whole.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScriptSource extends DatabaseTask {


    /**
     * Returns the current version of the scripts, i.e. the Version object as it would 
     * be returned by a database that is up-to-date with the current script base.
     * 
     * @return the current version of the scripts
     */
    public Version getCurrentVersion();
    
    /**
     * This methods returns true if one or more scripts that have a version index equal to or lower than
     * the index specified by the given version object has been modified since the timestamp specfied by
     * the given version.
     *
     * @param currentVersion The current database version, not null
     * @return True if an existing script has been modified, false otherwise
     */
    boolean existingScriptsModified(Version currentVersion);


    /**
     * @param currentVersion The current database version, not null
     * @return A List containing the scripts that need to be executed to update the database
     *         from the given version to the latest one.
     */
    List<VersionScriptPair> getNewScripts(Version currentVersion);


    /**
     * @return A List containing all available database update scripts. These scripts
     *         can be used to completely recreate the database from scratch.
     */
    List<VersionScriptPair> getAllScripts();


    /**
     * @return The highest timestamp of all the code scripts that are currently available
     */
    long getCodeScriptsTimestamp();


    /**
     * @return All the code scripts that are currently available
     */
    List<Script> getAllCodeScripts();

}
