/*
 * Copyright 2006-2007,  Unitils.org
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;
import org.unitils.dbmaintainer.version.Version;
import static org.unitils.util.PropertyUtils.getStringList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link ScriptSource} that reads script files from the filesystem. <p/> Script
 * files should be located in the directory configured by {@link #PROPKEY_SCRIPT_LOCATIONS}.
 * Valid script files start with a version number followed by an underscore, and end with the
 * extension configured by {@link #PROPKEY_SCRIPT_EXTENSIONS}.
 * <p/>
 * todo refactor -> this is not a database task
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultScriptSource extends BaseDatabaseTask implements ScriptSource {

    /* Logger instance for this class */
    private static final Log logger = LogFactory.getLog(DefaultScriptSource.class);

    /**
     * Property key for the directory in which the script files are located
     */
    public static final String PROPKEY_SCRIPT_LOCATIONS = "dbMaintainer.script.locations";

    /**
     * Property key for the extension of the script files
     */
    public static final String PROPKEY_SCRIPT_EXTENSIONS = "dbMaintainer.script.fileExtensions";

    /**
     * Property key for the directory in which the code script files are located
     */
    public static final String PROPKEY_POSTPROCESSINGSCRIPT_LOCATIONS = "dbMaintainer.postProcessingScript.locations";


    /**
     * Returns the highest version of the scripts, i.e. the Version object as it would
     * be returned by a database that is up-to-date with the current script base.
     *
     * @return the current version of the scripts
     */
    public Version getHighestVersion() {
        List<Script> scripts = getAllScripts();
        if (scripts.isEmpty()) {
            return new Version(new ArrayList<Long>(), 0);
        }

        List<Long> highestIndexes = new ArrayList<Long>();
        long highestTimeStamp = 0;
        for (Script script : scripts) {
            Version version = script.getVersion();
            // last non-empty index is the highest (list is ordered)
            if (version.getScriptIndex() != null) {
                highestIndexes = version.getIndexes();
            }
            highestTimeStamp = Math.max(highestTimeStamp, version.getTimeStamp());
        }
        return new Version(highestIndexes, highestTimeStamp);
    }


    /**
     * Gets a list of all available update scripts. These scripts can be used to completely recreate the
     * database from scratch, not null.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @return all available database update scripts, not null
     */
    public List<Script> getAllScripts() {
        List<Script> scripts = new ArrayList<Script>();
        for (File scriptLocation : getScriptLocations()) {
            getScripts(scriptLocation, new ArrayList<Long>(), scripts);
        }
        Collections.sort(scripts);
        assertNoDuplicateVersionIndexes(scripts);
        return scripts;
    }


    /**
     * Asserts that, in the given list of database update scripts, there are no two scripts with the same version.
     * 
     * @param scripts The list of scripts, must be sorted by version
     */
    protected void assertNoDuplicateVersionIndexes(List<Script> scripts) {
    	for (int i = 0; i < scripts.size() - 1; i++) {
    		Script script1 = scripts.get(i);
			Script script2 = scripts.get(i + 1);
			if (script1.getVersion().getIndexes().equals(script2.getVersion().getIndexes())) {
    			throw new UnitilsException("Found 2 database scripts with the same version index: " 
    					+ script1.getName() + " and " + script2.getName() + " both have version index " 
    					+ script1.getVersion().getIndexesString());
    		}
    	}
    }


	/**
     * Returns a list of scripts with a higher index or timestamp than the given version.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @param currentVersion The start version, not null
     * @return The scripts that have a higher index of timestamp than the start version, not null.
     */
    public List<Script> getNewScripts(Version currentVersion) {
        List<Script> result = new ArrayList<Script>();
        long currentTimeStamp = currentVersion.getTimeStamp();

        List<Script> scripts = getAllScripts();
        for (Script script : scripts) {
            Version version = script.getVersion();

            // check for new indexes
            if (version.getScriptIndex() != null && version.compareTo(currentVersion) > 0) {
                result.add(script);
            }
            // check for modified scripts without an index
            if (version.getScriptIndex() == null && version.getTimeStamp() > currentTimeStamp) {
                result.add(script);
            }
        }
        return result;
    }


    /**
     * Returns true if one or more scripts that have a version index equal to or lower than
     * the index specified by the given version object has been modified since the timestamp specfied by
     * the given version.
     *
     * @param currentVersion The current database version, not null
     * @return True if an existing script has been modified, false otherwise
     */
    public boolean isExistingScriptModified(Version currentVersion) {
        long currentTimeStamp = currentVersion.getTimeStamp();

        List<Script> scripts = getAllScripts();
        for (Script script : scripts) {
            Version version = script.getVersion();
            if (version.getScriptIndex() == null) {
                // skip files that do not have an index
                continue;
            }
            if (version.compareTo(currentVersion) <= 0 && version.getTimeStamp() > currentTimeStamp) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the configured post-processing script files and verfies that they on the file system. If one of them
     * doesn't exist or is not a file, an exception is thrown.
     *
     * @return All the postprocessing code scripts, not null
     */
    public List<Script> getPostProcessingScripts() {
        List<Script> scripts = new ArrayList<Script>();

        List<String> scriptNames = getStringList(PROPKEY_POSTPROCESSINGSCRIPT_LOCATIONS, configuration);
        List<File> scriptFiles = getFiles(scriptNames, PROPKEY_POSTPROCESSINGSCRIPT_LOCATIONS);
        for (File scriptFile : scriptFiles) {
            if (!scriptFile.isFile()) {
                throw new UnitilsException("Post processing script is not a file. Script: " + scriptFile.getName());
            }
            Script script = createScript(new ArrayList<Long>(), scriptFile);
            scripts.add(script);
        }
        return scripts;
    }


    /**
     * Adds all scripts available in the given directory or one of its subdirectories to the
     * given List of files
     *
     * @param scriptLocation       The current script location, not null
     * @param currentParentIndexes The indexes of the current parent folders, not null
     * @param scriptFiles          The list to which the available script have to be added
     */
    protected void getScripts(File scriptLocation, List<Long> currentParentIndexes, List<Script> scriptFiles) {
        if (scriptLocation.isFile() && isScriptFile(scriptLocation)) {
            Script script = createScript(currentParentIndexes, scriptLocation);
            scriptFiles.add(script);
            return;
        }
        // recursively scan sub folders for script files
        if (scriptLocation.isDirectory()) {
            List<Long> parentIndexes = new ArrayList<Long>();
            parentIndexes.addAll(currentParentIndexes);
            parentIndexes.add(extractIndex(scriptLocation.getName()));

            for (File subLocation : scriptLocation.listFiles()) {
                getScripts(subLocation, parentIndexes, scriptFiles);
            }
        }
    }


    /**
     * Indicates if the given file is regarded as a script file
     *
     * @param file The file
     * @return True if the given file is regarded as a script file.
     */
    protected boolean isScriptFile(File file) {
        String name = file.getName();
        for (String fileExtension : getScriptExtensions()) {
            if (name.endsWith(fileExtension)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Creates a version for the given script file. The index -1 is used for files that do not have a version in the
     * file name.
     *
     * @param parentIndexes The indexes of the parent folders, not null
     * @param scriptFile    The script file, not null
     * @return The version of the script file, not null
     */
    protected Version createVersion(List<Long> parentIndexes, File scriptFile) {
        List<Long> indexes = new ArrayList<Long>();
        indexes.addAll(parentIndexes);
        indexes.add(extractIndex(scriptFile.getName()));
        return new Version(indexes, scriptFile.lastModified());
    }


    /**
     * Extracts the index part out of a given file name.
     *
     * @param fileName The file name, not null
     * @return The index, null if there is no index
     */
    protected Long extractIndex(String fileName) {
        if (StringUtils.contains(fileName, "_")) {
            try {
                return Long.parseLong(StringUtils.substringBefore(fileName, "_"));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return null;
    }


    /**
     * Creates a script object for the given script files
     *
     * @param parentIndexes The indexes of the parent folders, not null
     * @param scriptFile    The script file, not null
     * @return The script, not null
     */
    protected Script createScript(List<Long> parentIndexes, File scriptFile) {
        Version version = createVersion(parentIndexes, scriptFile);
        return new Script(scriptFile, version);
    }


    /**
     * Gets the configured script locations and verfies that they on the file system. If one of them
     * doesn't exist, an exception is thrown.
     *
     * @return The files, not null
     */
    protected List<File> getScriptLocations() {
        List<String> locations = getStringList(PROPKEY_SCRIPT_LOCATIONS, configuration);

        // check whether a location is configured
        if (locations.isEmpty()) {
            logger.warn("No directories or files are specificied using the property " + PROPKEY_SCRIPT_LOCATIONS + ". The Unitils database maintainer won't do anyting");
        }
        return getFiles(locations, PROPKEY_SCRIPT_LOCATIONS);
    }


    /**
     * Gets the configured extensions for the script files.
     *
     * @return The extensions, not null
     */
    protected List<String> getScriptExtensions() {
        List<String> extensions = getStringList(PROPKEY_SCRIPT_EXTENSIONS, configuration);

        // check whether an extension is configured
        if (extensions.isEmpty()) {
            logger.warn("No extensions are specificied using the property " + PROPKEY_SCRIPT_EXTENSIONS + ". The Unitils database maintainer won't do anyting");
        }
        // Verify the correctness of the script extensions
        for (String extension : extensions) {
            if (extension.startsWith(".")) {
                throw new UnitilsException("DefaultScriptSource file extension defined by " + PROPKEY_SCRIPT_EXTENSIONS + " should not start with a '.'");
            }
        }
        return extensions;
    }


    /**
     * Verfies that directories and files in the given list of fileLocations exist on the file
     * system. If one of them doesn't exist, an exception is thrown
     *
     * @param locations    The directories and files that need to be checked
     * @param propertyName The name of the property, for the error message if a location does not exist
     * @return The list of files, not null
     */
    private List<File> getFiles(List<String> locations, String propertyName) {
        List<File> result = new ArrayList<File>();
        for (String fileLocation : locations) {
            File file = new File(fileLocation);
            if (!file.exists()) {
                throw new UnitilsException("File location " + fileLocation + " defined in property " + propertyName + " doesn't exist");
            }
            result.add(file);
        }
        return result;
    }

}
