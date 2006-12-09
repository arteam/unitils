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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of {@link ScriptSource} that reads script files from the filesystem.
 * <p/>
 * Script files should be located in the directory configured by {@link #PROPKEY_SCRIPTFILES_DIR}. Valid script files
 * start with a version number followed by an underscore, and end with the extension configured by
 * {@link #PROPKEY_SCRIPTFILES_FILEEXTENSION}.
 *
 * @author Filip Neven
 */
public class FileScriptSource extends DatabaseTask implements ScriptSource {

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
     */
    public void doInit(Configuration configuration) {

        scriptFilesDir = configuration.getString(PROPKEY_SCRIPTFILES_DIR);
        if (!new File(scriptFilesDir).exists()) {
            throw new UnitilsException("Script files directory '" + scriptFilesDir + "' does not exist");
        }
        fileExtension = configuration.getString(PROPKEY_SCRIPTFILES_FILEEXTENSION);
        if (fileExtension.startsWith(".")) {
            throw new UnitilsException("FileScriptSource file extension defined by " + PROPKEY_SCRIPTFILES_FILEEXTENSION + " should not start with a '.'");
        }
    }

    /**
     * This methods returns true if one or more scripts that have a version index equal to or lower than
     * the index specified by the given version object has been modified since the timestamp specfied by
     * the given version.
     *
     * @param currentVersion
     * @return true if an existing script has been modified, false otherwise
     */
    public boolean existingScriptsModified(Version currentVersion) {
        Long scriptsTimestamp = getTimestampOfAlreadyExecutedScripts(currentVersion);
        return (scriptsTimestamp > currentVersion.getTimeStamp());
    }

    /**
     * Returns a <code>List<VersionScriptPair></code> containing the statements that will update the database from the
     * given version to the latest one.
     *
     * @param currentVersion The current database version
     * @return A List<VersionScriptPair> containing the scripts that need to be executed to update the database
     *         version to the latest one.
     */
    public List<VersionScriptPair> getNewScripts(Version currentVersion) {
        List<File> filesWithNewerVersion = getFilesWithHigherIndex(currentVersion.getIndex());
        return getVersionScriptPairsFromFiles(filesWithNewerVersion);
    }

    /**
     * @return All available scripts
     */
    public List<VersionScriptPair> getAllScripts() {
        return getVersionScriptPairsFromFiles(getScriptFilesSorted());
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
     * Returns the version index of the given script file
     *
     * @param scriptFile The file containing a script
     * @return The version of the script file
     */
    protected Long getIndex(File scriptFile) {
        return new Long(StringUtils.substringBefore(scriptFile.getName(), "_"));
    }

    /**
     * Returns the highest timestamp of the given list of scriptFiles.
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
     * Returns all script files having a higher version index than the given one
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
    private List<VersionScriptPair> getVersionScriptPairsFromFiles(List<File> files) {
        List<VersionScriptPair> scripts = new ArrayList<VersionScriptPair>();
        long timeStamp = getHighestScriptTimestamp(files);
        List<File> filesSorted = sortFilesByIndex(files);
        for (File file : filesSorted) {
            try {
                scripts.add(new VersionScriptPair(new Version(getIndex(file), timeStamp),
                        FileUtils.readFileToString(file, System.getProperty("file.encoding"))));
            } catch (IOException e) {
                throw new UnitilsException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }

}
