package be.ordina.unitils.db.maintainer.version;

/**
 * Class representing the version of a database, or the version of a number of DDL scripts. Objects of this class
 * have value semantics, and are by consequence immutable.
 */
public class Version {

    /* The index of the last (executed) script */
    private Long index;

    /* The timestamp of the script that was last modified, expressed as the number of milliseconds since January 1, 1970 */
    private Long timeStamp;

    public Version(Long index, Long timeStamp) {
        this.index = index;
        this.timeStamp = timeStamp;
    }

    public Long getIndex() {
        return index;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

}
