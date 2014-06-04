/*
 * Copyright 2008,  Unitils.org
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
import java.util.Set;

import org.unitils.core.util.Configurable;
import org.unitils.dbmaintainer.version.Version;

/**
 * A source that provides scripts for updating the database to a given state.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScriptSource extends Configurable {


    /**
     * @param dialect
     * @param databaseName
     * @return a list of all available update scripts, in the order in which they must be executed on the database. 
     * These scripts can be used to completely recreate the database from scratch. Not null
     */
    List<Script> getAllUpdateScripts(String dialect, String databaseName, boolean defaultDatabase);


    /**
     * Returns a list of scripts including the ones that:
     * <ol><li>have a higher version than the given version</li>
     * <li>are unversioned, and they weren't yet applied on the database</li>
     * <li>are unversioned, and their contents differ from the one currently applied to the database</li>
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @param highestExecutedScriptVersion The highest version of the versioned scripts that were already applied to the database
     * @param alreadyExecutedScripts The scripts which were already executed on the database
     * @param dialect
     * @param databaseName
     * @return The new scripts.
     */
    List<Script> getNewScripts(Version highestExecutedScriptVersion, Set<ExecutedScript> alreadyExecutedScripts, String dialect, String databaseName, boolean defaultDatabase);


    /**
     * Returns true if one or more scripts that have a version index equal to or lower than
     * the index specified by the given version object has been modified since the timestamp specfied by
     * the given version.
     *
     * @param currentVersion The current database version, not null
     * @param alreadyExecutedScripts 
     * @param dialect 
     * @param databaseName 
     * @return True if an existing script has been modified, false otherwise
     */
    boolean isExistingIndexedScriptModified(Version currentVersion, Set<ExecutedScript> alreadyExecutedScripts, String dialect, String databaseName, boolean defaultDatabase);


    /**
     * Gets a list of all post processing scripts.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @param dialect
     * @param databaseName
     * @return All the postprocessing code scripts, not null
     */
    List<Script> getPostProcessingScripts(String dialect, String databaseName, boolean defaultDatabase);

}
