package org.unitils.dbmaintainer.maintainer;

import org.unitils.dbmaintainer.maintainer.version.Version;

/**
 * Class representing the database update scripts for updateing the database to a given version.
 */
public class VersionScriptPair {

    /**
     * The version to which the database will be updated after all the scripts have been executed
     */
    private Version version;

    /**
     * The list of DDL scripts that will bring the database into the version
     */
    private String script;

    public VersionScriptPair(Version version, String script) {
        this.version = version;
        this.script = script;
    }

    public Version getVersion() {
        return version;
    }

    public String getScript() {
        return script;
    }
}
