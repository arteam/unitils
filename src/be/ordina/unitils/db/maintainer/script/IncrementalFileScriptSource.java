/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.script;

import be.ordina.unitils.db.maintainer.VersionScriptPair;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementation of <code>ScriptSource</code> that reads script files from the filesystem, given a version in the
 * form of a version nr. This implementation is meant for incrementally updating the unit test databases.
 * <p>
 * The directory and the file extension are configured using a Properties object by invoking the init method.
 */
public class IncrementalFileScriptSource extends AbstractFileScriptSource {

    /**
     * @see ScriptSource#getScripts(Long)
     */
    public List<VersionScriptPair> getScripts(Long currentVersion) {
        List<File> filesWithNewerVersion = getFilesWithNewerVersion(currentVersion);
        List<File> filesSorted = sortFilesByVersion(filesWithNewerVersion);
        List<VersionScriptPair> scripts = getStatementsFromFiles(filesSorted);
        return scripts;
    }

    /**
     * Returns all script files having a newer version than the given one
     * @param currentVersion
     * @return all script files having a newer version than the given one
     */
    private List<File> getFilesWithNewerVersion(long currentVersion) {
        List<File> filesSorted = getScriptFilesSorted();
        List<File> filesWithNewerVersion = new ArrayList<File>();
        for (int i = 0; i < filesSorted.size(); i++) {
            File file = filesSorted.get(i);
            if (getVersion(file) > currentVersion) {
                filesWithNewerVersion.add(file);
            }
        }
        return filesWithNewerVersion;
    }

}
