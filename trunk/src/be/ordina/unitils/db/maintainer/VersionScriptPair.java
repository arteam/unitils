package be.ordina.unitils.db.maintainer;

/**
 * Class representing an update script associated with a given version
 */
public class VersionScriptPair {

    private Long version;

    private String script;

    public VersionScriptPair(Long version, String script) {
        this.version = version;
        this.script = script;
    }

    public Long getVersion() {
        return version;
    }

    public String getScript() {
        return script;
    }
}
