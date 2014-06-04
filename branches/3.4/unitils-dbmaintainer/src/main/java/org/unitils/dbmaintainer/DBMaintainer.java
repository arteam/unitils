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
package org.unitils.dbmaintainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.core.util.ConfigUtils;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;

import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;

import org.unitils.dbmaintainer.version.ExecutedScriptInfoSource;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.util.PropertyUtils;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * A class for performing automatic maintenance of a database.<br>
 * This class must be configured with implementations of a {@link ExecutedScriptInfoSource},
 * {@link ScriptSource}, a {@link ScriptRunner}, {@link DBClearer}, {@link DBCleaner},
 * {@link ConstraintsDisabler}, {@link SequenceUpdater} and a {@link DataSetStructureGenerator}
 * <p/> The {@link #updateDatabase()} method check what is the current version of the database, and
 * see if existing scripts have been modified. If yes, the database is cleared and all available
 * database scripts, are executed on the database. If no existing scripts have been modified, but
 * new scripts were added, only the new scripts are executed. Before executing an update, data from
 * the database is removed, to avoid problems when e.g. adding a not null column. <p/> If a database
 * update causes an error, a {@link UnitilsException} is thrown. After a failing update, the
 * database is always completely recreated from scratch. <p/> After updating the database, following
 * steps are optionally executed on the database (depending on the configuration):
 * <ul>
 * <li>Foreign key and not null constraints are disabled.</li>
 * <li>Sequences and identity columns that have a value lower than a configured treshold, are
 * updated to a value equal to or larger than this treshold</li>
 * <li>A DTD is generated that describes the database's table structure, to use in test data XML
 * files</li>
 * </ul>
 * <p/> To obtain a properly configured <code>DBMaintainer</code>, invoke the constructor
 * {@link #DBMaintainer(Properties,SQLHandler)} with a <code>TestDataSource</code> providing
 * access to the database and a <code>Configuration</code> object containing all necessary
 * properties.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainer {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBMaintainer.class);

    /**
     * Property indicating if deleting all data from all tables before updating is enabled
     */
    public static final String PROPKEY_DB_CLEANER_ENABLED = "dbMaintainer.cleanDb.enabled";

    /**
     * Property indicating if updating the database from scratch is enabled
     */
    public static final String PROPKEY_FROM_SCRATCH_ENABLED = "dbMaintainer.fromScratch.enabled";

    /**
     * Property indicating if database code should be cleared before installing a new version of
     * the code or when updating the database from scratch
     */
    public static final String PROPKEY_CLEAR_DB_CODE_ENABLED = "dbMaintainer.clearDbCode.enabled";

    /**
     * Property indicating if an retry of an update should only be performed when changes to script files were made
     */
    public static final String PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED = "dbMaintainer.keepRetryingAfterError.enabled";

    /**
     * Property indicating if the database constraints should org disabled after updating the database
     */
    public static final String PROPKEY_DISABLE_CONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /**
     * Property indicating if the database constraints should org disabled after updating the database
     */
    public static final String PROPKEY_UPDATE_SEQUENCES_ENABLED = "dbMaintainer.updateSequences.enabled";

    /**
     * Property that indicates if a data set DTD or XSD is to be generated or not
     */
    public static final String PROPKEY_GENERATE_DATA_SET_STRUCTURE_ENABLED = "dbMaintainer.generateDataSetStructure.enabled";

    /**
     * Provider of the current version of the database, and means to increment it
     */
    protected ExecutedScriptInfoSource versionSource;

    /**
     * Provider of scripts for updating the database to a higher version
     */
    protected ScriptSource scriptSource;

    /**
     * Executer of the scripts
     */
    protected ScriptRunner scriptRunner;

    /**
     * Clearer of the database (removed all tables, sequences, ...) before updating
     */
    protected DBClearer dbClearer;

    /**
     * Cleaner of the database (deletes all data from all tables before updating
     */
    protected DBCleaner dbCleaner;

    /**
     * Disabler of constraints
     */
    protected ConstraintsDisabler constraintsDisabler;

    /**
     * Database sequence updater
     */
    protected SequenceUpdater sequenceUpdater;

    /**
     * Database DTD generator
     */
    protected DataSetStructureGenerator dataSetStructureGenerator;

    /**
     * Indicates whether updating the database from scratch is enabled. If true, the database is
     * cleared before updating if an already executed script is modified
     */
    protected boolean fromScratchEnabled;

    /**
     * Indicates if foreign key and not null constraints should removed after updating the database
     * structure
     */
    protected boolean disableConstraintsEnabled;

    /**
     * Indicates whether a from scratch update should be performed when the previous update failed,
     * but none of the scripts were modified since that last update. If true a new update will be
     * tried only when changes were made to the script files
     */
    protected boolean keepRetryingAfterError;

    protected String dialect;

    /**
     * Default constructor for testing.
     */
    protected DBMaintainer() {
    }


    /**
     * Create a new instance of <code>DBMaintainer</code>, The concrete implementations of all
     * helper classes are derived from the given <code>Configuration</code> object.
     *
     * @param configuration the configuration, not null
     * @param sqlHandler    the data source, not null
     */
    public DBMaintainer(Properties configuration, SQLHandler sqlHandler, String dialect, List<String> schemaNames) {
        try {
            scriptRunner = getConfiguredDatabaseTaskInstance(ScriptRunner.class, configuration, sqlHandler, dialect, schemaNames);
            versionSource = getConfiguredDatabaseTaskInstance(ExecutedScriptInfoSource.class, configuration, sqlHandler, dialect, schemaNames);
            scriptSource = ConfigUtils.getConfiguredInstanceOf(ScriptSource.class, configuration);

            boolean cleanDbEnabled = PropertyUtils.getBoolean(PROPKEY_DB_CLEANER_ENABLED, configuration);
            if (cleanDbEnabled) {
                dbCleaner = getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, sqlHandler, dialect, schemaNames);
            }

            fromScratchEnabled = PropertyUtils.getBoolean(PROPKEY_FROM_SCRATCH_ENABLED, configuration);
            keepRetryingAfterError = PropertyUtils.getBoolean(PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED, configuration);
            if (fromScratchEnabled) {
                dbClearer = getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, sqlHandler, dialect, schemaNames);
            }

            disableConstraintsEnabled = PropertyUtils.getBoolean(PROPKEY_DISABLE_CONSTRAINTS_ENABLED, configuration);
            constraintsDisabler = getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class, configuration, sqlHandler, dialect, schemaNames);

            boolean updateSequences = PropertyUtils.getBoolean(PROPKEY_UPDATE_SEQUENCES_ENABLED, configuration);
            if (updateSequences) {
                sequenceUpdater = getConfiguredDatabaseTaskInstance(SequenceUpdater.class, configuration, sqlHandler, dialect, schemaNames);
            }

            boolean generateDtd = PropertyUtils.getBoolean(PROPKEY_GENERATE_DATA_SET_STRUCTURE_ENABLED, configuration);
            if (generateDtd) {
                dataSetStructureGenerator = getConfiguredDatabaseTaskInstance(DataSetStructureGenerator.class, configuration, sqlHandler, dialect, schemaNames);
            }
        } catch (UnitilsException e) {
            logger.error("Error while initializing DbMaintainer", e);
            throw e;
        }
        this.dialect = dialect;
    }


    /**
     * Checks if the new scripts are available to update the version of the database. If yes, these
     * scripts are executed and the version number is increased. If an existing script has been
     * modified, the database is cleared and completely rebuilt from scratch. If an error occurs
     * with one of the scripts, a {@link UnitilsException} is thrown.
     */
    public void updateDatabase(String databaseName, boolean defaultDatabase) {
        // Check if the executed scripts info source recommends a from-scratch update
        boolean fromScratchUpdateRecommended = versionSource.isFromScratchUpdateRecommended();

        Set<ExecutedScript> alreadyExecutedScripts = versionSource.getExecutedScripts();
        Version highestExecutedScriptVersion = getHighestExecutedScriptVersion(alreadyExecutedScripts);

        // check whether an from scratch update should be performed
        boolean shouldUpdateFromScratch = shouldUpdateDatabaseFromScratch(highestExecutedScriptVersion, alreadyExecutedScripts, databaseName, defaultDatabase);
        if (fromScratchEnabled && (fromScratchUpdateRecommended || shouldUpdateFromScratch)) {
            // From scratch needed, clear the database and retrieve scripts
            // constraints are removed before clearing the database, to be sure there will be no
            // conflicts when dropping tables
            constraintsDisabler.disableConstraints();
            dbClearer.clearSchemas();
            // reset the database version
            versionSource.clearAllExecutedScripts();
            // update database with all scripts
            updateDatabase(scriptSource.getAllUpdateScripts(dialect, databaseName, defaultDatabase), databaseName, defaultDatabase);
            return;
        }

        // perform an incremental update
        updateDatabase(scriptSource.getNewScripts(highestExecutedScriptVersion, alreadyExecutedScripts, dialect, databaseName, defaultDatabase), databaseName, defaultDatabase);
    }


    protected Version getHighestExecutedScriptVersion(Set<ExecutedScript> executedScripts) {
        Version highest = new Version("0");
        for (ExecutedScript executedScript : executedScripts) {
            if (executedScript.getScript().isIncremental()) {
                if (executedScript.getScript().getVersion().compareTo(highest) > 0) {
                    highest = executedScript.getScript().getVersion();
                }
            }
        }
        return highest;
    }


    /**
     * Updates the database version to the current version of the update scripts, without changing
     * anything else in the database. Can be used to initialize the database for future updates,
     * knowning that the current state of the database is synchronized with the current state of the
     * scripts.
     */
    public void resetDatabaseState(String databaseName, boolean defaultDatabase) {
        versionSource.clearAllExecutedScripts();

        List<Script> allScripts = scriptSource.getAllUpdateScripts(dialect, databaseName, defaultDatabase);
        for (Script script : allScripts) {
            versionSource.registerExecutedScript(new ExecutedScript(script, new Date(), true));
        }
    }


    /**
     * Updates the state of the database using the given scripts.
     *
     * @param scripts The scripts, not null
     */
    protected void updateDatabase(List<Script> scripts, String schema, boolean defaultDatabase) {
        if (scripts.isEmpty()) {
            // nothing to do
            logger.info("Database is up to date");
            return;
        }
        logger.info("Database update scripts have been found and will be executed on the database.");

        // Remove data from the database, that could cause errors when executing scripts. Such
        // as for example when added a not null column.
        if (dbCleaner != null) {
            dbCleaner.cleanSchemas();
        }

        // Excute all of the scripts
        executeScripts(scripts);

        // Execute postprocessing scripts, if any
        executePostProcessingScripts(scriptSource.getPostProcessingScripts(dialect, schema, defaultDatabase));

        // Disable FK and not null constraints, if enabled
        if (disableConstraintsEnabled) {
            constraintsDisabler.disableConstraints();
        }
        // Update sequences to a sufficiently high value, if enabled
        if (sequenceUpdater != null) {
            sequenceUpdater.updateSequences();
        }
        // Generate a DTD to enable validation and completion in data xml files, if enabled
        if (dataSetStructureGenerator != null) {
            dataSetStructureGenerator.generateDataSetStructure();
        }
    }


    /**
     * Executes the given scripts and updates the database version and state appropriatly. After
     * each successful script execution, the new version is stored in the database and marked as
     * succesful. If a script execution fails and fromScratch is enabled, that script version is
     * stored in the database and marked as unsuccesful. If fromScratch is not enabled, the last
     * succesful version is stored in the database that way, the next time an update is tried, the
     * execution restarts from the last unsuccessful script.
     *
     * @param scripts The scripts to execute, not null
     */
    protected void executeScripts(List<Script> scripts) {
        for (Script script : scripts) {
            try {
                // We register the script execution, but we indicate it to be unsuccessful. If anything goes wrong or if the update is
                // interrupted before being completed, this will be the final state and the DbMaintainer will do a from-scratch update the next time
                ExecutedScript executedScript = new ExecutedScript(script, new Date(), false);
                versionSource.registerExecutedScript(executedScript);

                logger.info("Executing script " + script.getFileName());
                scriptRunner.execute(script.getScriptContentHandle());
                // We now register the previously registered script execution as being successful
                executedScript.setSuccessful(true);
                versionSource.updateExecutedScript(executedScript);

            } catch (UnitilsException e) {
                logger.error("Error while executing script " + script.getFileName(), e);
                throw e;
            }
        }
    }


    /**
     * Executes the given post processing scripts on the database. If not successful, the scripts update
     * is registered as not successful, so that an update from scratch will be triggered the next time.
     *
     * @param postProcessingScripts The scripts to execute, not null
     */
    protected void executePostProcessingScripts(List<Script> postProcessingScripts) {
        for (Script script : postProcessingScripts) {
            try {
                logger.info("Executing post processing script " + script.getFileName());
                scriptRunner.execute(script.getScriptContentHandle());

            } catch (UnitilsException e) {
                logger.error("Error while executing post processing script " + script.getFileName(), e);
                throw e;
            }
        }
    }


    /**
     * Checks whether the database should be updated from scratch or just incrementally. The
     * database needs to be rebuilt in following cases:
     * <ul>
     * <li>Some existing scripts were modified.</li>
     * <li>The last update of the database was unsuccessful.</li>
     * </ul>
     * The database will only be rebuilt from scratch if {@link #PROPKEY_FROM_SCRATCH_ENABLED} is
     * set to true. If the {@link #PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED} is set to false, the
     * database will only be rebuilt again after an unsuccessful build when changes were made to the
     * script files.
     *
     * @param currentVersion The current database version, not null
     * @return True if a from scratch rebuild is needed, false otherwise
     */
    protected boolean shouldUpdateDatabaseFromScratch(Version currentVersion, Set<ExecutedScript> alreadyExecutedScripts, String databaseName, boolean defaultDatabase) {
        // check whether an existing script was updated
        if (scriptSource.isExistingIndexedScriptModified(currentVersion, alreadyExecutedScripts, dialect, databaseName, defaultDatabase)) {
            if (!fromScratchEnabled) {
                throw new UnitilsException("One or more existing incremental database update scripts have been modified, but updating from scratch is disabled. " +
                        "You should either revert to the original version of the modified script and add an new incremental script that performs the desired " +
                        "update, or perform the update manually on the database and then reset the database state by invoking resetDatabaseState()");
            }
            logger.info("One or more existing database update scripts have been modified. Database will be cleared and rebuilt from scratch.");
            return true;
        }

        // check whether the last run was successful
        if (errorInIndexedScriptDuringLastUpdate(alreadyExecutedScripts)) {
            if (fromScratchEnabled) {
                if (!keepRetryingAfterError) {
                    throw new UnitilsException("During a previous database update, the execution of an incremental script failed! Since " +
                            PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED + " is set to false, the database will not be rebuilt " +
                            "from scratch, unless the failed (or another) incremental script is modified.");
                }
                logger.info("During a previous database update, the execution of a incremental script failed! " +
                        "Database will be cleared and rebuilt from scratch.");
                return true;
            } else {
                logger.warn("During a previous database update, the execution of an incremental script failed! " +
                        "Since from scratch updates are disabled, you should fix the erroneous script, solve the problem " +
                        "manually on the database, and then reset the database state by invoking resetDatabaseState()");
                return false;
            }
        }

        // from scratch is not needed
        return false;
    }


    protected boolean errorInIndexedScriptDuringLastUpdate(Set<ExecutedScript> alreadyExecutedScripts) {
        for (ExecutedScript script : alreadyExecutedScripts) {
            if (!script.isSucceeded() && script.getScript().isIncremental()) {
                return true;
            }
        }
        return false;
    }

    protected void setDialect(String dialect) {
        this.dialect = dialect;
    }

    
}
