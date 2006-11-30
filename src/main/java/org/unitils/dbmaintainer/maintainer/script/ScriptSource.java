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
package org.unitils.dbmaintainer.maintainer.script;

import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;

import java.util.List;

/**
 * Defines the contract for a source that provides scripts for updating the database to a given state.<br>
 * Scripts are provided as {@link VersionScriptPair} objects, which indicate which scripts should be executed, to
 * update the database to a given state.
 *
 * @author Filip Neven
 */
public interface ScriptSource {

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
     * @param currentVersion The current database version
     * @return A List<VersionScriptPair> containing the scripts that need to be executed to update the database
     *         from the given version to the latest one.
     */
    List<VersionScriptPair> getNewScripts(Version currentVersion);

    /**
     * @return a <code>List<VersionScriptPair></code> containing all available database update scripts. These scripts
     * can be used to completely recreate the database from scratch.
     */
    List<VersionScriptPair> getAllScripts();

}
