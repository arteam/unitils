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
package org.unitils.dbmaintainer.version;

import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.script.Script;

/**
 * Class representing the database update script for updating the database to a given version.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class VersionScriptPair {

    /* The version to which the database will be updated after all the scripts have been executed */
    private Version version;

    /* The list of DDL scripts that will update the database to this version */
    private Script script;


    /**
     * Constructs a new instance with the given version and script
     *
     * @param version The version, not null
     * @param script  The script, not null
     */
    public VersionScriptPair(Version version, Script script) {
        this.version = version;
        this.script = script;
    }


    /**
     * @return The version, not null
     */
    public Version getVersion() {
        return version;
    }

    public Script getScript() {
    /**
     * @return The script, not null
     */
        return script;
    }
}
