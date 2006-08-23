package be.ordina.unitils.db.maintainer;

import java.util.List;

/**
 * Class representing the database update scripts for updateing the database to a given version.
 */
public class VersionScriptPair {

    /**
     * The version to which the database will be updated after all the scripts have been executed
     */
    private Long version;

    /**
     *  The list of DDL scripts that will bring the database into the version
     */
    private List<String> scripts;

    public VersionScriptPair(Long version, List<String> scripts) {
        this.version = version;
        this.scripts = scripts;
    }

    public Long getVersion() {
        return version;
    }

    public List<String> getScripts() {
        return scripts;
    }
}
