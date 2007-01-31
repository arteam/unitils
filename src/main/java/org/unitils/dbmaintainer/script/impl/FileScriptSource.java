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
package org.unitils.dbmaintainer.script.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Implementation of {@link ScriptSource} that reads script files from the filesystem.
 * <p/>
 * Script files should be located in the directory configured by {@link #PROPKEY_SCRIPTFILES_LOCATION}. Valid script files
 * start with a version number followed by an underscore, and end with the extension configured by
 * {@link #PROPKEY_SCRIPTFILES_FILEEXTENSION}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class FileScriptSource extends DatabaseTask implements ScriptSource {

    private static final Log logger = LogFactory.getLog(FileScriptSource.class);

    /**
     * Property key for the directory in which the script files are located
     */
    public static final String PROPKEY_SCRIPTFILES_LOCATION = "dbMaintainer.fileScriptSource.scripts.location";

    /**
     * Property key for the directory in which the code script files are located
     */
    public static final String PROPKEY_CODESCRIPTFILES_LOCATION = "dbMaintainer.fileScriptSource.code.location";

    /**
     * Property key for the extension of the script files
     */
    public static final String PROPKEY_SCRIPTFILES_FILEEXTENSION = "dbMaintainer.fileScriptSource.fileExtension";

    /* The directory in which the script files are located */
    private List<String> scriptFilesLocation;

    /* The directory in which the code script files are located */
    private List<String> codeScriptFilesLocation;

    /* The extension of the script files */
    private String fileExtension;


    /**
     * Uses the given <code>Configuration</code> to initialize the script files directory, and the file extension
     * of the script files.
     */
    @SuppressWarnings("unchecked")
    public void doInit(Configuration configuration) {

        if (StringUtils.isNotEmpty(configuration.getString(PROPKEY_SCRIPTFILES_LOCATION))) {
            scriptFilesLocation = configuration.getList(PROPKEY_SCRIPTFILES_LOCATION);
        } else {
            scriptFilesLocation = Collections.EMPTY_LIST;
            logger.warn("No directory is specificied using the property " + PROPKEY_SCRIPTFILES_LOCATION + ". The Unitils" +
                    " database maintainer won't do anyting");
        }
        if (StringUtils.isNotEmpty(configuration.getString(PROPKEY_CODESCRIPTFILES_LOCATION))) {
            codeScriptFilesLocation = configuration.getList(PROPKEY_CODESCRIPTFILES_LOCATION);
        } else {
            codeScriptFilesLocation = Collections.EMPTY_LIST;
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
     * @param currentVersion The current database version, not null
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
        List<File> filesWithNewerVersion = getScriptFilesWithHigherIndex(currentVersion.getIndex());
        return getVersionScriptPairsFromFiles(filesWithNewerVersion);
    }


    /**
     * @return All available scripts
     */
    public List<VersionScriptPair> getAllScripts() {
        return getVersionScriptPairsFromFiles(getScriptFilesSorted(true, scriptFilesLocation));
    }

    public long getCodeScriptsTimestamp() {
        return getHighestScriptTimestamp(getScriptFiles(false, codeScriptFilesLocation));
    }

    public List<Script> getAllCodeScripts() {
        return getScriptsFromFiles(getScriptFilesSorted(false, codeScriptFilesLocation));
    }

    /**
     * @param currentVersion The current database version, not null
     * @return The highest timestamp of all the scripts that were already executed
     */
    private Long getTimestampOfAlreadyExecutedScripts(final Version currentVersion) {
        List<File> scriptFilesAlreadyExecuted = getScriptFiles(true, scriptFilesLocation);
        
        // filter out scripts that are not executed yet
        CollectionUtils.filter(scriptFilesAlreadyExecuted, new Predicate() {
            public boolean evaluate(Object file) {
                return getIndex((File) file) <= currentVersion.getIndex();
            }
        });

        // get highest timestamp
        return getHighestScriptTimestamp(scriptFilesAlreadyExecuted);
    }


    /**
     * @return All available script files, sorted according to their version number
     */
    protected List<File> getScriptFilesSorted(boolean excludeFilesWithoutIndex, List<String> filesLocation) {
        List<File> scriptFiles = getScriptFiles(excludeFilesWithoutIndex, filesLocation);
        return sortFilesByIndex(scriptFiles);
    }


    /**
     * @return All available script files
     */
    private List<File> getScriptFiles(final boolean excludeFilesWithoutIndex, List<String> filesLocation) {
        List<File> scriptFiles = new ArrayList<File>();
        getScriptFiles(excludeFilesWithoutIndex, filesLocation, scriptFiles);
        return scriptFiles;
    }

    /**
     * Adds all available script files in the given locations to the given List of files
     *
     * @param excludeFilesWithoutIndex
     * @param filesLocations
     * @param files
     */
    private void getScriptFiles(boolean excludeFilesWithoutIndex, List<String> filesLocations, List<File> files) {
        for (String filesLocation : filesLocations) {
            getAllFilesIn(excludeFilesWithoutIndex, new File(filesLocation), files);
        }
    }

    /**
     * Adds all available script files in the given location to the given List of files
     *
     * @param excludeFilesWithoutIndex
     * @param filesLocation
     * @param files
     */
    private void getAllFilesIn(boolean excludeFilesWithoutIndex, File filesLocation, List<File> files) {
        if (filesLocation.isDirectory()) {
            for (File subLocation: filesLocation.listFiles()) {
                getAllFilesIn(excludeFilesWithoutIndex, subLocation, files);
            }
        } else {
            if (isScriptFile(filesLocation, excludeFilesWithoutIndex)) {
                files.add(filesLocation);
            }
        }
    }

    /**
     * @param file
     * @param excludeFilesWithoutIndex
     * @return True if the given file is regarded as a script file.
     */
    private boolean isScriptFile(File file, boolean excludeFilesWithoutIndex) {
        String name = file.getName();
        if (!name.endsWith(fileExtension)) {
            return false;
        }
        if (excludeFilesWithoutIndex) {
            if (!StringUtils.contains(name, '_')) {
                return false;
            }
            String indexNrStr = StringUtils.substringBefore(name, "_");
            if (!StringUtils.isNumeric(indexNrStr)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Sorts the given list of script files according to their index number
     *
     * @param files the list of files, not null
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
        if (StringUtils.contains(scriptFile.getName(), "_")) {
            try {
                return new Long(StringUtils.substringBefore(scriptFile.getName(), "_"));
            } catch (NumberFormatException e) {
                return -1L;
            }
        } else {
            return -1L;
        }
    }


    /**
     * Returns the highest timestamp of the given list of scriptFiles.
     *
     * @param scriptFiles the list of files, not null
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
     * @param currentVersion The current database version, not null
     * @return all script files having a newer version than the given one
     */
    private List<File> getScriptFilesWithHigherIndex(long currentVersion) {
        List<File> filesSorted = getScriptFilesSorted(true, scriptFilesLocation);
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
        for (File file : files) {
            try {
                scripts.add(new VersionScriptPair(new Version(getIndex(file), timeStamp),
                        new Script(file.getName(), FileUtils.readFileToString(file, System.getProperty("file.encoding")))));
            } catch (IOException e) {
                throw new UnitilsException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }

    private List<Script> getScriptsFromFiles(List<File> files) {
        List<Script> scripts = new ArrayList<Script>();
        for (File file : files) {
            try {
                scripts.add(new Script(file.getName(), FileUtils.readFileToString(file, System.getProperty("file.encoding"))));
            } catch (IOException e) {
                throw new UnitilsException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }

}
