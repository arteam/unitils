/*
 * Copyright 2006-2007,  Unitils.org
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

import org.unitils.dbmaintainer.util.DatabaseTask;
import org.unitils.dbmaintainer.version.Version;

import java.util.List;

/**
 * A source that provides scripts for updating the database to a given state.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScriptSource extends DatabaseTask {


    /**
     * Returns the highest version of the scripts, i.e. the Version object as it would
     * be returned by a database that is up-to-date with the current script base.
     *
     * @return the current version of the scripts
     */
    Version getHighestVersion();


    /**
     * Gets a list of all available update scripts. These scripts can be used to completely recreate the
     * database from scratch, not null.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @return all available database update scripts, not null
     */
    List<Script> getAllScripts();


    /**
     * Returns a list of scripts with a higher index or timestamp than the given version.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @param currentVersion The start version, not null
     * @return The scripts that have a higher index of timestamp than the start version, not null.
     */
    List<Script> getNewScripts(Version currentVersion);


    /**
     * Returns true if one or more scripts that have a version index equal to or lower than
     * the index specified by the given version object has been modified since the timestamp specfied by
     * the given version.
     *
     * @param currentVersion The current database version, not null
     * @return True if an existing script has been modified, false otherwise
     */
    boolean isExistingScriptModified(Version currentVersion);


    /**
     * Gets a list of all post processing scripts.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @return All the postprocessing code scripts, not null
     */
    List<Script> getPostProcessingScripts();

}
