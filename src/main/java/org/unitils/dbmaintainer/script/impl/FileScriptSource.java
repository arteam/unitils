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

import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.readFileToString;
import static org.unitils.util.PropertyUtils.getStringList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;

/**
 * Implementation of {@link ScriptSource} that reads script files from the filesystem. <p/> Script
 * files should be located in the directory configured by {@link #PROPKEY_SCRIPTFILES_LOCATIONS}.
 * Valid script files start with a version number followed by an underscore, and end with the
 * extension configured by {@link #PROPKEY_SCRIPTFILES_FILEEXTENSIONS}.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class FileScriptSource extends BaseDatabaseTask implements ScriptSource {

    private static final Log logger = LogFactory.getLog(FileScriptSource.class);

    /**
     * Property key for the directory in which the script files are located
     */
    public static final String PROPKEY_SCRIPTFILES_LOCATIONS = "dbMaintainer.fileScriptSource.scripts.locations";

    /**
     * Property key for the directory in which the code script files are located
     */
    public static final String PROPKEY_CODESCRIPTFILES_LOCATIONS = "dbMaintainer.fileScriptSource.code.locations";
    
    
    /**
     * Property key for the directory in which the code script files are located
     */
    public static final String PROPKEY_POSTPROCESSINGCODESCRIPTFILES_LOCATIONS = "dbMaintainer.fileScriptSource.postProcessingCode.locations";

    /**
     * Property key for the extension of the script files
     */
    public static final String PROPKEY_SCRIPTFILES_FILEEXTENSIONS = "dbMaintainer.fileScriptSource.scripts.fileExtensions";

    /**
     * Property key for the extension of the code script files
     */
    public static final String PROPKEY_CODESCRIPTFILES_FILEEXTENSIONS = "dbMaintainer.fileScriptSource.code.fileExtensions";

    private ScriptFilesSpecification scriptFilesSpecification, codeScriptFilesSpecification, 
            postProcessingCodeScriptFilesSpecification;

    /**
     * Uses the given configuration to initialize the script files directory, and the file extension
     * of the script files.
     * 
     * @param configuration
     *        The configuration, not null
     */
    @Override
    @SuppressWarnings("unchecked")
    public void doInit(Properties configuration) {
        final List<String> scriptFileLocations = getStringList(PROPKEY_SCRIPTFILES_LOCATIONS, configuration);
        verifyExistence(scriptFileLocations, PROPKEY_SCRIPTFILES_LOCATIONS);
        if (scriptFileLocations.isEmpty()) {
            logger.warn("No directories or files are specificied using the property "
                    + PROPKEY_SCRIPTFILES_LOCATIONS
                    + ". The Unitils database maintainer won't do anyting");
        }
        final List<String> scriptFileExtensions = getStringList(PROPKEY_SCRIPTFILES_FILEEXTENSIONS, configuration);
        verifyScriptFileExtionsions(scriptFileExtensions);
        scriptFilesSpecification = new ScriptFilesSpecification() {
            public List<String> getFileExtensions() {
                return scriptFileExtensions;
            }
            public List<String> getFileLocations() {
                return scriptFileLocations;
            }
            public boolean isExcludeFilesWithoutIndex() {
                return true;
            }
        };
        
        final List<String> codeScriptFileLocations = getStringList(PROPKEY_CODESCRIPTFILES_LOCATIONS, configuration);
        verifyExistence(codeScriptFileLocations, PROPKEY_CODESCRIPTFILES_LOCATIONS);
        final List<String> codeScriptFileExtensions = getStringList(PROPKEY_CODESCRIPTFILES_FILEEXTENSIONS,
                configuration);
        verifyScriptFileExtionsions(codeScriptFileExtensions);
        codeScriptFilesSpecification = new ScriptFilesSpecification() {
            public List<String> getFileExtensions() {
                return codeScriptFileExtensions;
            }
            public List<String> getFileLocations() {
                return codeScriptFileLocations;
            }
            public boolean isExcludeFilesWithoutIndex() {
                return false;
            }
        };
        
        final List<String> postProcessingCodeScriptFileLocations = getStringList(PROPKEY_POSTPROCESSINGCODESCRIPTFILES_LOCATIONS, configuration);
        verifyExistence(postProcessingCodeScriptFileLocations, PROPKEY_POSTPROCESSINGCODESCRIPTFILES_LOCATIONS);
        postProcessingCodeScriptFilesSpecification = new ScriptFilesSpecification() {
            public List<String> getFileExtensions() {
                return codeScriptFileExtensions;
            }
            public List<String> getFileLocations() {
                return postProcessingCodeScriptFileLocations;
            }
            public boolean isExcludeFilesWithoutIndex() {
                return false;
            }
        };
    }

    /**
     * Verifies the correctness of the script file extension list
     * 
     * @param fileExtensions
     */
    private void verifyScriptFileExtionsions(List<String> fileExtensions) {
        for (String fileExtension : fileExtensions) {
            if (fileExtension.startsWith(".")) {
                throw new UnitilsException("FileScriptSource file extension defined by "
                        + PROPKEY_SCRIPTFILES_FILEEXTENSIONS + " should not start with a '.'");
            }
        }
    }

    /**
     * Verfies that directories and files in the given list of fileLocations exist on the file
     * system. If one of them doesn't exist, an exception is thrown
     * 
     * @param fileLocations
     *        The directories and files that need to be checked
     * @param propertyName
     *        The name of the property, to be included in the error message if one of the locations
     *        doesn't exist
     */
    protected void verifyExistence(List<String> fileLocations, String propertyName) {
        for (String fileLocation : fileLocations) {
            File file = new File(fileLocation);
            if (!file.exists()) {
                throw new UnitilsException("File location " + fileLocation
                        + " defined in property " + propertyName + " doesn't exist");
            }
        }
    }

    /**
     * Returns the current version of the scripts, i.e. the Version object as it would be returned
     * by a database that is up-to-date with the current script base.
     * 
     * @return the current version of the scripts
     */
    public Version getCurrentVersion() {
        List<File> scriptFilesSorted = getScriptFilesSorted(scriptFilesSpecification);
        long highestVersionIndex = getIndex(scriptFilesSorted.get(scriptFilesSorted.size() - 1));
        long highestVersionTimeStamp = getHighestScriptTimestamp(scriptFilesSpecification);
        return new Version(highestVersionIndex, highestVersionTimeStamp);
    }

    /**
     * Returns true if one or more scripts that have a version index equal to or lower than the
     * index specified by the given version object has been modified since the timestamp specfied by
     * the given version.
     * 
     * @param currentVersion
     *        The current database version, not null
     * @return true if an existing script has been modified, false otherwise
     */
    public boolean existingScriptsModified(Version currentVersion) {
        Long scriptsTimestamp = getTimestampOfAlreadyExecutedScripts(currentVersion);
        return (scriptsTimestamp > currentVersion.getTimeStamp());
    }

    /**
     * Returns a <code>List<VersionScriptPair></code> containing the statements that will update
     * the database from the given version to the latest one.
     * 
     * @param currentVersion
     *        The current database version
     * @return A List<VersionScriptPair> containing the scripts that need to be executed to update
     *         the database version to the latest one.
     */
    public List<VersionScriptPair> getNewScripts(Version currentVersion) {
        List<File> filesWithNewerVersion = getScriptFilesWithHigherIndex(currentVersion.getIndex());
        return getVersionScriptPairsFromFiles(filesWithNewerVersion);
    }

    /**
     * @return All available scripts
     */
    public List<VersionScriptPair> getAllScripts() {
        return getVersionScriptPairsFromFiles(getScriptFilesSorted(scriptFilesSpecification));
    }

    /**
     * @return The highest timestamp of all the code scripts that are currently available
     */
    public long getCodeScriptsTimestamp() {
        return getHighestScriptTimestamp(codeScriptFilesSpecification);
    }

    /**
     * @return All the code scripts that are currently available
     */
    public List<Script> getAllCodeScripts() {
        return getScriptsFromFiles(getScriptFilesSorted(codeScriptFilesSpecification));
    }
    
    
    /**
     * @return All the postprocessing code scripts that are currently available
     */
    public List<Script> getAllPostProcessingCodeScripts() {
        return getScriptsFromFiles(getScriptFilesSorted(postProcessingCodeScriptFilesSpecification));
    }

    
    /**
     * @param currentVersion
     *        The current database version, not null
     * @return The highest timestamp of all the scripts that were already executed
     */
    private Long getTimestampOfAlreadyExecutedScripts(final Version currentVersion) {
        List<File> scriptFilesAlreadyExecuted = getScriptFiles(scriptFilesSpecification);

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
     * Returns all available script files, sorted according to their version number
     * 
     * @param filesSpecification 
     *        Specification describing the files that can be regarded as a script file
     * @return All available script files, sorted according to their version number
     */
    protected List<File> getScriptFilesSorted(ScriptFilesSpecification filesSpecification) {
        List<File> scriptFiles = getScriptFiles(filesSpecification);
        return sortFilesByIndex(scriptFiles);
    }

    /**
     * Returns all available script files that can be found in one of the given directories or their
     * @param filesSpecification 
     *        Specification describing the files that can be regarded as a script file
     * @return All available script files
     */
    protected List<File> getScriptFiles(ScriptFilesSpecification filesSpecification) {
        List<File> scriptFiles = new ArrayList<File>();
        for (String filesLocation : filesSpecification.getFileLocations()) {
            getAllFilesIn(filesSpecification, new File(filesLocation), scriptFiles);
        }
        return scriptFiles;
    }

    /**
     * Adds all script files available in the given directory or one of its subdirectories to the
     * given List of files
     * @param filesSpecification 
     *        Specification describing the files that can be regarded as a script file
     * @param filesLocation
     *        The directory where the files are located
     * @param files
     *        The list to which the available script files have to be added
     */
    protected void getAllFilesIn(ScriptFilesSpecification filesSpecification,
            File filesLocation, List<File> files) {
        if (filesLocation.isDirectory()) {
            for (File subLocation : filesLocation.listFiles()) {
                getAllFilesIn(filesSpecification, subLocation, files);
            }
        } else {
            if (isScriptFile(filesLocation, filesSpecification)) {
                files.add(filesLocation);
            }
        }
    }

    /**
     * Indicates if the given file is regarded as a script file
     * 
     * @param file
     *        The file
     * @param scriptFileSpecification 
     *        Specification describing the files that can be regarded as a script file
     * @return True if the given file is regarded as a script file.
     */
    protected boolean isScriptFile(File file, ScriptFilesSpecification scriptFileSpecification) {
        String name = file.getName();
        boolean fileExtensionSupported = false;
        for (String fileExtension : scriptFileSpecification.getFileExtensions()) {
            if (name.endsWith(fileExtension)) {
                fileExtensionSupported = true;
                break;
            }
        }
        if (!fileExtensionSupported) {
            return false;
        }

        if (scriptFileSpecification.isExcludeFilesWithoutIndex()) {
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
     * @param files
     *        the list of files, not null
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
     * @param scriptFile
     *        The file containing a script
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
     * Returns the highest timestamp of all the scriptFiles that adhere to the given {@link ScriptFilesSpecification}
     * 
     * @param scriptFilesSpecification The specification the scriptFiles should adhere to
     * @return The highest timestamp 
     */
    protected Long getHighestScriptTimestamp(ScriptFilesSpecification scriptFilesSpecification) {
    	return getHighestScriptTimestamp(getScriptFiles(scriptFilesSpecification));
    }

    /**
     * Returns the highest timestamp of the given list of scriptFiles.
     * 
     * @param scriptFiles
     *        the list of files, not null
     * @return highest timestamp of the given scriptFiles with index lower than maxIndex
     */
    protected Long getHighestScriptTimestamp(List<File> scriptFiles) {
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
     *        The current database version, not null
     * @return all script files having a newer version than the given one
     */
    protected List<File> getScriptFilesWithHigherIndex(long currentVersion) {
        List<File> filesSorted = getScriptFilesSorted(scriptFilesSpecification);
        List<File> filesWithNewerVersion = new ArrayList<File>();
        for (File file : filesSorted) {
            if (getIndex(file) > currentVersion) {
                filesWithNewerVersion.add(file);
            }
        }
        return filesWithNewerVersion;
    }

    /**
     * Returns the scripts from the given list of script files as a list of
     * <code>VersionScriptPair</code> objects
     * 
     * @param files
     *        The script files
     * @return The scripts as a list of <code>VersionScriptPair</code> objects
     */
    protected List<VersionScriptPair> getVersionScriptPairsFromFiles(List<File> files) {
        List<VersionScriptPair> scripts = new ArrayList<VersionScriptPair>();
        long timeStamp = getHighestScriptTimestamp(scriptFilesSpecification);
        for (File file : files) {
            try {
                scripts.add(new VersionScriptPair(new Version(getIndex(file), timeStamp),
                        new Script(file.getName(), readFileToString(file, System
                                .getProperty("file.encoding")))));
            } catch (IOException e) {
                throw new UnitilsException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }

    /**
     * Returns a List with the content of the given files as strings
     * 
     * @param files
     *        The files containing database update scripts
     * @return The database update scripts as as list of strings
     */
    protected List<Script> getScriptsFromFiles(List<File> files) {
        List<Script> scripts = new ArrayList<Script>();
        for (File file : files) {
            try {
                scripts.add(new Script(file.getName(), readFileToString(file, System
                        .getProperty("file.encoding"))));
            } catch (IOException e) {
                throw new UnitilsException("Error while trying to read file " + file);
            }
        }
        return scripts;
    }
    
    protected interface ScriptFilesSpecification {
        
        List<String> getFileExtensions();
        
        List<String> getFileLocations();
        
        boolean isExcludeFilesWithoutIndex();
    }

}
