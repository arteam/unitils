/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer.script;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;

/**
 * Implementation of {@link ScriptSource} that reads script files from the filesystem. This implementation can work
 * both incrementally and from scratch.
 * <p>Script files should be located in the directory configured by {@link #PROPKEY_SCRIPTFILES_DIR}. Valid script files
 * start with a version number followed by an underscore, and end with the extension configured by 
 * {@link #PROPKEY_SCRIPTFILES_FILEEXTENSION}.
 * <p>
 * When script files have been added having a higher version number, {@link #existingScriptsModified(Version)} will return false,
 * and only the newer version scripts are returned by {@link #getNewScripts(Version)}. When existing scripts
 * have been modified, {@link #existingScriptsModified(Version)} returns true, and {@link #getNewScripts(Version)} returns all 
 * scripts.
 */
public class FileScriptSource implements ScriptSource {

    /**
     * Property key for the directory in which the script files are located
     */
    public static final String PROPKEY_SCRIPTFILES_DIR = "dbMaintainer.fileScriptSource.dir";

    /**
     * Property key for the extension of the script files
     */
    public static final String PROPKEY_SCRIPTFILES_FILEEXTENSION = "dbMaintainer.fileScriptSource.fileExtension";

    /* The directory in which the script files are located */
    private String scriptFilesDir;

    /* The extension of the script files */
    private String fileExtension;


    /**
     * Uses the given <code>Configuration</code> to initialize the script files directory, and the file extension
     * of the script files.
     * @see ScriptSource#init(Configuration)
     */
    public void init(Configuration configuration) {

        scriptFilesDir = configuration.getString(PROPKEY_SCRIPTFILES_DIR);
        if (!new File(scriptFilesDir).exists()) {
            throw new UnitilsException("Script files directory '" + scriptFilesDir + "' does not exist");
        }
        fileExtension = configuration.getString(PROPKEY_SCRIPTFILES_FILEEXTENSION);
        if (fileExtension.startsWith(".")) {
            throw new UnitilsException("Extension should not start with a '.'");
        }
    }

    /**
     * Given the current {@link Version} of the database, returns true if the database should be rebuilt from 
     * scratch, or if it can be updated incrementally to the latest version.
     * 
     * @see ScriptSource#existingScriptsModified(org.unitils.dbmaintainer.maintainer.version.Version)
     */
    public boolean existingScriptsModified(Version currentVersion) {
        Long scriptsTimestamp = getTimestampOfAlreadyExecutedScripts(currentVersion);
        return (scriptsTimestamp > currentVersion.getTimeStamp());
    }

    /**
     * @return the scripts that should be run to update the database to the latest version incrementally
     */
    public List<VersionScriptPair> getNewScripts(Version currentVersion) {
        List<File> filesWithNewerVersion = getFilesWithHigherIndex(currentVersion.getIndex());
        return getStatementsFromFiles(filesWithNewerVersion);
    }
    
    /**
     * @return the scripts that should be run to update the database to the latest version from scratch
     */
    public List<VersionScriptPair> getAllScripts() {
        return getStatementsFromFiles(getScriptFilesSorted());
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
