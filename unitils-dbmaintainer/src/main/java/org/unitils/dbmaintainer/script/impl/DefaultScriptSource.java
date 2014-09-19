/*
 * Copyright 2008,  Unitils.org
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

import static org.unitils.util.PropertyUtils.getStringList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.BaseConfigurable;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptContentHandle;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.util.FileUtils;
import org.unitils.util.PropertyUtils;

/**
 * Implementation of {@link ScriptSource} that reads script files from the filesystem. <p/> Script
 * files should be located in the directory configured by {@link #PROPKEY_SCRIPT_LOCATIONS}.
 * Valid script files start with a version number followed by an underscore, and end with the
 * extension configured by {@link #PROPKEY_SCRIPT_EXTENSIONS}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultScriptSource extends BaseConfigurable implements ScriptSource {

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
    public static final String PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME = "dbMaintainer.postProcessingScript.directoryName";

    public static final String PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES = "dbMaintainer.useScriptFileLastModificationDates.enabled";
    
    public static final String PROPKEY_EXCLUDE_QUALIFIERS = "dbMaintainer.excludedQualifiers";
    
    public static final String PROPKEY_INCLUDE_QUALIFIERS = "dbMaintainer.includedQualifiers";
    
    public static final String PROPKEY_QUALIFIERS = "dbMaintainer.qualifiers";
    
    protected List<Script> allUpdateScripts, allPostProcessingScripts;


    /**
     * Gets a list of all available update scripts. These scripts can be used to completely recreate the
     * database from scratch, not null.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @return all available database update scripts, not null
     */
    public List<Script> getAllUpdateScripts(String dialect, String databaseName, boolean defaultDatabase) {
        if (allUpdateScripts == null) {
            loadAndOrganizeAllScripts(dialect, databaseName, defaultDatabase);
        }
        return allUpdateScripts;
    }


    /**
     * @return All scripts that are incremental, i.e. non-repeatable, i.e. whose file name starts with an index
     */
    protected List<Script> getIncrementalScripts(String dialect, String databaseName, boolean defaultDatabase) {
        List<Script> scripts = getAllUpdateScripts(dialect, databaseName, defaultDatabase);
        List<Script> indexedScripts = new ArrayList<Script>();
        for (Script script : scripts) {
            if (script.isIncremental()) {
                indexedScripts.add(script);
            }
        }
        return indexedScripts;
    }


    /**
     * Asserts that, in the given list of database update scripts, there are no two indexed scripts with the same version.
     *
     * @param scripts The list of scripts, must be sorted by version
     */
    protected void assertNoDuplicateIndexes(List<Script> scripts) {
        for (int i = 0; i < scripts.size() - 1; i++) {
            Script script1 = scripts.get(i);
            Script script2 = scripts.get(i + 1);
            if (script1.isIncremental() && script2.isIncremental() && script1.getVersion().equals(script2.getVersion())) {
                throw new UnitilsException("Found 2 database scripts with the same version index: "
                    + script1.getFileName() + " and " + script2.getFileName() + " both have version index "
                    + script1.getVersion().getIndexesString());
            }
        }
    }


    /**
     * Returns a list of scripts with a higher version or whose contents were changed.
     * <p/>
     * The scripts are returned in the order in which they should be executed.
     *
     * @param currentVersion The start version, not null
     * @return The scripts that have a higher index of timestamp than the start version, not null.
     */
    public List<Script> getNewScripts(Version currentVersion, Set<ExecutedScript> alreadyExecutedScripts, String dialect, String databaseName, boolean defaultDatabase) {
        Map<String, Script> alreadyExecutedScriptMap = convertToScriptNameScriptMap(alreadyExecutedScripts);

        List<Script> result = new ArrayList<Script>();

        List<Script> allScripts = getAllUpdateScripts(dialect, databaseName, defaultDatabase);
        for (Script script : allScripts) {
            Script alreadyExecutedScript = alreadyExecutedScriptMap.get(script.getFileName());

            // If the script is indexed and the version is higher than the highest one currently applied to the database,
            // add it to the list.
            if (script.isIncremental() && script.getVersion().compareTo(currentVersion) > 0) {
                result.add(script);
                continue;
            }
            // Add the script if it's not indexed and if it wasn't yet executed
            if (!script.isIncremental() && alreadyExecutedScript == null) {
                result.add(script);
                continue;
            }
            // Add the script if it's not indexed and if it's contents have changed
            if (!script.isIncremental() && !alreadyExecutedScript.isScriptContentEqualTo(script, useScriptFileLastModificationDates())) {
                logger.info("Contents of script " + script.getFileName() + " have changed since the last database update: "
                    + script.getCheckSum());
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
    public boolean isExistingIndexedScriptModified(Version currentVersion, Set<ExecutedScript> alreadyExecutedScripts, String dialect, String databaseName, boolean defaultDatabase) {
        Map<String, Script> alreadyExecutedScriptMap = convertToScriptNameScriptMap(alreadyExecutedScripts);
        List<Script> incrementalScripts = getIncrementalScripts(dialect, databaseName, defaultDatabase);
        // Search for indexed scripts that have been executed but don't appear in the current indexed scripts anymore
        for (ExecutedScript alreadyExecutedScript : alreadyExecutedScripts) {
            if (alreadyExecutedScript.getScript().isIncremental() && Collections.binarySearch(incrementalScripts, alreadyExecutedScript.getScript()) < 0) {
                logger.warn("Existing indexed script found that was executed, which has been removed: " + alreadyExecutedScript.getScript().getFileName());
                return true;
            }
        }

        // Search for indexed scripts whose version < the current version, which are new or whose contents have changed
        for (Script indexedScript : incrementalScripts) {
            if (indexedScript.getVersion().compareTo(currentVersion) <= 0) {
                Script alreadyExecutedScript = alreadyExecutedScriptMap.get(indexedScript.getFileName());
                if (alreadyExecutedScript == null) {
                    logger.warn("New index script has been added, with at least one already executed script having an higher index." + indexedScript.getFileName());
                    return true;
                }
                if (!alreadyExecutedScript.isScriptContentEqualTo(indexedScript, useScriptFileLastModificationDates())) {
                    logger.warn("Script found of which the contents have changed: " + indexedScript.getFileName());
                    return true;
                }
            }
        }
        return false;
    }


    protected boolean useScriptFileLastModificationDates() {
        return PropertyUtils.getBoolean(PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES, configuration);
    }


    /**
     * Gets the configured post-processing script files and verfies that they on the file system. If one of them
     * doesn't exist or is not a file, an exception is thrown.
     *
     * @return All the postprocessing code scripts, not null
     */
    public List<Script> getPostProcessingScripts(String dialect, String databaseName, boolean defaultDatabase) {
        if (allPostProcessingScripts == null) {
            loadAndOrganizeAllScripts(dialect, databaseName, defaultDatabase);
        }
        return allPostProcessingScripts;
    }


    /**
     * Loads all scripts and organizes them: Splits them into update and postprocessing scripts, sorts
     * them in their execution order, and makes sure there are no 2 update or postprocessing scripts with
     * the same index.
     */
    protected void loadAndOrganizeAllScripts(String dialect, String databaseName, boolean defaultDatabase) {
        List<Script> allScripts = loadAllScripts(dialect, databaseName, defaultDatabase);
        allUpdateScripts = new ArrayList<Script>();
        allPostProcessingScripts = new ArrayList<Script>();
        for (Script script : allScripts) {
            if (isPostProcessingScript(script)) {
                allPostProcessingScripts.add(script);
            } else {
                allUpdateScripts.add(script);
            }
        }
        Collections.sort(allUpdateScripts);
        assertNoDuplicateIndexes(allUpdateScripts);
        Collections.sort(allPostProcessingScripts);
        assertNoDuplicateIndexes(allPostProcessingScripts);

    }


    /**
     * @return A List containing all scripts in the given script locations, not null
     */
    protected List<Script> loadAllScripts(String dialect, String databaseName, boolean defaultDatabase) {
        List<String> scriptLocations = PropertyUtils.getStringList(PROPKEY_SCRIPT_LOCATIONS, configuration);
        List<Script> scripts = new ArrayList<Script>();
        for (String scriptLocation : scriptLocations) {
            if (!new File(scriptLocation).exists()) {
                throw new UnitilsException("File location " + scriptLocation + " defined in property " + PROPKEY_SCRIPT_LOCATIONS + " doesn't exist");
            }
            getScriptsAt(scripts, scriptLocation, "", databaseName, defaultDatabase);
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
    protected void getScriptsAt(List<Script> scripts, String scriptRoot, String relativeLocation, String databaseName, boolean defaultDatabase) {
        File currentLocation = new File(scriptRoot + "/" + relativeLocation);
        if (currentLocation.isFile() && isScriptFile(currentLocation)) {
            //check databaseName
            String nameFile = currentLocation.getName();
            if (checkIfScriptContainsCorrectDatabaseName(nameFile, databaseName, defaultDatabase) && containsOneOfQualifiers(nameFile)) {
                Script script = createScript(currentLocation, relativeLocation);
                scripts.add(script);
                return;
            }
        }
        // recursively scan sub folders for script files
        if (currentLocation.isDirectory()) {
            for (File subLocation : currentLocation.listFiles()) {
                getScriptsAt(scripts, scriptRoot, "".equals(relativeLocation) ? subLocation.getName() : relativeLocation + "/" + subLocation.getName(), databaseName, defaultDatabase);
            }
        }
    }
    
    /**
     * This method checks if a scriptfile is a file that should be used by every schema or if the scriptfile is a file for a specific schema.
     * @param nameFile
     * @param databaseName
     * @return {@link Boolean}
     * 
     * @see <a href="http://www.dbmaintain.org/tutorial.html#Multi-database__user_support">more info</a>
     */
    public boolean checkIfScriptContainsCorrectDatabaseName(String nameFile, String databaseName, boolean defaultDatabase) {
        String temp = nameFile.toLowerCase();
        
        if (!temp.contains("@")) {
            return (defaultDatabase ? true : false);
        }
        return temp.matches("(.*_)*@" + databaseName.toLowerCase()+ "_.+");
        
    }
    
    /**
     * Checks if the name of the script contains one of the qualifiers.
     * @param fileName
     * @return {@link Boolean}
     */
    public boolean containsOneOfQualifiers(String fileName){
        List<String> excludes = PropertyUtils.getStringList(PROPKEY_EXCLUDE_QUALIFIERS, configuration, false);
        List<String> includes = PropertyUtils.getStringList(PROPKEY_INCLUDE_QUALIFIERS, configuration, false);
        List<String> qualifiers = PropertyUtils.getStringList(PROPKEY_QUALIFIERS, configuration, false);

        if (excludes.isEmpty() && includes.isEmpty() && qualifiers.isEmpty()) {
            return true;
        }
        if (includes.isEmpty()) {
            /*
             * 1. The filename can be without qualifiers.
             * 2. Or the qualifier must be in the list of qualifiers and not in the exclude list.
             */
            return (containsQualifier(fileName, qualifiers) && !containsQualifier(fileName, excludes)) || checkIfThereAreNoQualifiers(fileName);
        } else {
            return containsQualifier(fileName, includes) && !containsQualifier(fileName, excludes);
        }
        
    }

    protected boolean containsQualifier(String fileName, List<String> qualifiers){
        for(String qualifier: qualifiers){
            if(fileName.contains("#" + qualifier + "_")){
                return true;
            }
        }
        return false;
    }
    
    protected boolean checkIfThereAreNoQualifiers(String fileName) {
        return !fileName.matches(".+_#\\w+_.+");
    }


    /**
     * @param script A database script, not null
     * @return True if the given script is a post processing script according to the script source configuration
     */
    protected boolean isPostProcessingScript(Script script) {
        List<String> startsWiths = PropertyUtils.getStringList(PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME, configuration);
        for (String startsWith : startsWiths) {
            if (script.getFileName().startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Indicates if the given file is a database update script file
     *
     * @param file The file, not null
     * @return True if the given file is a database update script file
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
     * Creates a script object for the given script file
     *
     * @param scriptFile The script file, not null
     * @return The script, not null
     */
    protected Script createScript(File scriptFile, String relativePath) {
        return new Script(relativePath, scriptFile.lastModified(), new ScriptContentHandle.UrlScriptContentHandle(FileUtils.getUrl(scriptFile)));
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


    protected Map<String, Script> convertToScriptNameScriptMap(Set<ExecutedScript> executedScripts) {
        Map<String, Script> scriptMap = new HashMap<String, Script>();
        for (ExecutedScript executedScript : executedScripts) {
            scriptMap.put(executedScript.getScript().getFileName(), executedScript.getScript());
        }
        return scriptMap;
    }

}
