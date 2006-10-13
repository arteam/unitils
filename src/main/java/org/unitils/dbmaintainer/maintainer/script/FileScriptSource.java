/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer.script;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of <code>ScriptSource</code> that reads script files from the filesystem.
 * <p/>
 * The directory and the file extension are configured using a Properties object by invoking the init method
 */
public class FileScriptSource implements ScriptSource {

    /**
     * Property key for the directory in which the script files are located
     */
    private static final String PROPKEY_SCRIPTFILES_DIR = "dbMaintainer.fileScriptSource.dir";
    /**
     * Property key for the extension of the script files
     */
    private static final String PROPKEY_SCRIPTFILES_FILEEXTENSION = "dbMaintainer.fileScriptSource.fileExtension";
    /**
     * The directory in which the script files are located
     */
    private String scriptFilesDir;
    /**
     * The extension of the script files
     */
    private String fileExtension;

    /**
     * @see ScriptSource#init(Configuration)
     */
    public void init(Configuration configuration) {

        scriptFilesDir = configuration.getString(PROPKEY_SCRIPTFILES_DIR);
        if (!new File(scriptFilesDir).exists()) {
            throw new IllegalArgumentException("Script files directory '" + scriptFilesDir + "' does not exist");
        }
        fileExtension = configuration.getString(PROPKEY_SCRIPTFILES_FILEEXTENSION);
        if (fileExtension.startsWith(".")) {
            throw new IllegalArgumentException("Extension should not start with a '.'");
        }
    }

    /**
     * @see ScriptSource#shouldRunFromScratch(org.unitils.dbmaintainer.maintainer.version.Version)
     */
    public boolean shouldRunFromScratch(Version currentVersion) {
        Long scriptsTimestamp = getTimestampOfAlreadyExecutedScripts(currentVersion);
        return (scriptsTimestamp > currentVersion.getTimeStamp());
    }

    /**
     * @see ScriptSource#getScripts(org.unitils.dbmaintainer.maintainer.version.Version)
     */
    public List<VersionScriptPair> getScripts(Version currentVersion) {
        boolean shouldRunFromScratch = shouldRunFromScratch(currentVersion);
        if (shouldRunFromScratch) {
            return getScriptsFromScratch();
        } else {
            return getScriptsIncremental(currentVersion);
        }
    }

    /**
     * @return the scripts that should be run to update the database to the latest version from scratch
     */
    private List<VersionScriptPair> getScriptsFromScratch() {
        return getStatementsFromFiles(getScriptFilesSorted());
    }

    /**
     * @return the scripts that should be run to update the database to the latest version incrementally
     */
    private List<VersionScriptPair> getScriptsIncremental(Version currentVersion) {
        List<File> filesWithNewerVersion = getFilesWithHigherIndex(currentVersion.getIndex());
        return getStatementsFromFiles(filesWithNewerVersion);
    }

    /**
     * @return the highest timestamp of all the scripts
     */
    private long getScriptsTimestamp() {
        List<File> scriptFiles = getScriptFiles();
        return getHighestScriptTimestamp(scriptFiles);
    }

    /**
     * @param currentVersion
     * @return The highest timestamp of all the scripts that were already executed
     */
    private Long getTimestampOfAlreadyExecutedScripts(final Version currentVersion) {
        List<File> scriptFilesAlreadyExecuted = getScriptFiles();
        CollectionUtils.filter(scriptFilesAlreadyExecuted, new Predicate() {
            public boolean evaluate(Object file) {
                return getIndex((File) file) <= currentVersion.getIndex();
            }
        });
        return getHighestScriptTimestamp(scriptFilesAlreadyExecuted);
    }

    /**
     * @return All available script files, sorted according to their version number
     */
    protected List<File> getScriptFilesSorted() {
        List<File> scriptFiles = getScriptFiles();
        return sortFilesByIndex(scriptFiles);
    }

    /**
     * @return All available script files
     */
    private List<File> getScriptFiles() {
        return new ArrayList<File>(Arrays.asList(new File(scriptFilesDir).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (!name.endsWith(fileExtension)) {
                    return false;
                }
                if (!StringUtils.contains(name, '_')) {
                    return false;
                }
                String indexNrStr = StringUtils.substringBefore(name, "_");
                if (!StringUtils.isNumeric(indexNrStr)) {
                    return false;
                }
                return true;
            }
        })));
    }

    /**
     * Sorts the given list of script files according to their index number
     *
     * @param files
     * @return The sorted list of script files
     */
    protected List<File> sortFilesByIndex(List<File> files) {
        Comparator<File> indexComparator = new Comparator<File>() {
            public int compare(File file1, File file2) {
                Long file1IndexNr = getIndex(file1);
                Long file2IndexNr = getIndex(file2);
                return file1IndexNr.compareTo(file2IndexNr);
            }
        };
        Collections.sort(files, indexComparator);
        return files;
    }

    /**
     * Returns the version of the given script file
     *
     * @param scriptFile The file containing a script
     * @return The version of the script file
     */
    protected Long getIndex(File scriptFile) {
        return new Long(StringUtils.substringBefore(scriptFile.getName(), "_"));
    }

    /**
     * Returns all scripts that the given files contain as a List of strings
     *
     * @param scriptFiles
     * @return all scripts that the given files contain as a List of strings
     */
    private List<String> getScriptsFromFiles(List<File> scriptFiles) {
        List<String> scripts = new ArrayList<String>();
        for (File scriptFile : scriptFiles) {
            try {
                String script = FileUtils.readFileToString(scriptFile, System.getProperty("file.encoding"));
                scripts.add(script);
            } catch (IOException e) {
                throw new RuntimeException("Error while trying to read file " + scriptFile);
            }
        }
        return scripts;
    }

    /**
     * Returns the highest timestamp of the given scriptFiles. Only the files with an index lower than maxIndex are
     * considered.
     *
     * @param scriptFiles
     * @return highest timestamp of the given scriptFiles with index lower than maxIndex
     */
    private Long getHighestScriptTimestamp(List<File> scriptFiles) {
        Long highestTimestamp = 0L;
        for (File scriptFile : scriptFiles) {
            highestTimestamp = Math.max(highestTimestamp, scriptFile.lastModified());
        }
        return highestTimestamp;
    }

    /**
     * Returns all script files having a newer version than the given one
     *
     * @param currentVersion
     * @return all script files having a newer version than the given one
     */
    private List<File> getFilesWithHigherIndex(long currentVersion) {
        List<File> filesSorted = getScriptFilesSorted();
        List<File> filesWithNewerVersion = new ArrayList<File>();
        for (File file : filesSorted) {
            if (getIndex(file) > currentVersion) {
                filesWithNewerVersion.add(file);
            }
        }
        return filesWithNewerVersion;
    }

    /**
     * Returns the scripts from the given list of script files as a list of <code>VersionScriptPair</code> objects
     *
     * @param files The script files
     * @return The scripts as a list of <code>VersionScriptPair</code> objects
     */
    private List<VersionScriptPair> getStatementsFromFiles(List<File> files) {
        List<VersionScriptPair> scripts = new ArrayList<VersionScriptPair>();
        long timeStamp = getHighestScriptTimestamp(files);
        List<File> filesSorted = sortFilesByIndex(files);
        for (File file : filesSorted) {
            try {
                scripts.add(new VersionScriptPair(new Version(getIndex(file), timeStamp),
                        FileUtils.readFileToString(file, System.getProperty("file.encoding"))));
            } catch (IOException e) {
                throw new RuntimeException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }

}
