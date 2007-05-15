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
package org.unitils.dbmaintainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.clean.DBCodeClearer;
import org.unitils.dbmaintainer.script.CodeScriptRunner;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;
import org.unitils.dbmaintainer.version.VersionSource;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * A class for performing automatic maintenance of a database.<br>
 * This class must be configured with implementations of a {@link VersionSource}, {@link ScriptSource}, a
 * {@link ScriptRunner}, {@link DBClearer}, {@link DBCleaner}, {@link ConstraintsDisabler}, {@link SequenceUpdater} and
 * a {@link DataSetStructureGenerator}
 * <p/>
 * The {@link #updateDatabase()} method check what is the current version of the database, and see if existing scripts
 * have been modified. If yes, the database is cleared and all available database scripts, are executed on the database.
 * If no existing scripts have been modified, but new scripts were added, only the new scripts are executed.
 * Before executing an update, data from the database is removed, to avoid problems when e.g. adding a not null column.
 * <p/>
 * If a database update causes an error, a {@link UnitilsException} is thrown. After a failing update, the
 * database is always completely recreated from scratch.
 * <p/>
 * After updating the database, following steps are optionally executed on the database (depending on the configuration):
 * <ul>
 * <li>Foreign key and not null constraints are disabled.</li>
 * <li>Sequences and identity columns that have a value lower than a configured treshold, are updated to a value equal
 * to or largen than this treshold</li>
 * <li>A DTD is generated that describes the database's table structure, to use in test data XML files</li>
 * </ul>
 * <p/>
 * To obtain a properly configured <code>DBMaintainer</code>, invoke the contructor
 * {@link #DBMaintainer(Properties,DataSource)} with a <code>TestDataSource</code> providing access
 * to the database and a <code>Configuration</code> object containing all necessary properties.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainer {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBMaintainer.class);

    /* Property indicating if deleting all data from all tables before updating is enabled */
    public static final String PROPKEY_DB_CLEANER_ENABLED = "dbMaintainer.cleanDb.enabled";

    /* Property key indicating if updating the database from scratch is enabled */
    public static final String PROPKEY_FROM_SCRATCH_ENABLED = "dbMaintainer.fromScratch.enabled";

    /* Property key indicating if database code should be cleared before installing a new version of the code or when
     updating the database from scratch */
    public static final String PROPKEY_CLEAR_DB_CODE_ENABLED = "dbMaintainer.clearDbCode.enabled";

    /* Property key indicating if an retry of an update should only be performed when changes to script files were made */
    public static final String PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED = "dbMaintainer.keepRetryingAfterError.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_DISABLE_CONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_UPDATE_SEQUENCES_ENABLED = "dbMaintainer.updateSequences.enabled";

    /* Property key that indicates if a data set DTD or XSD is to be generated or not */
    public static final String PROPKEY_GENERATE_DATA_SET_STRUCTURE_ENABLED = "dbMaintainer.generateDataSetStructure.enabled";

    /* Provider of the current version of the database, and means to increment it */
    protected VersionSource versionSource;

    /* Provider of scripts for updating the database to a higher version */
    protected ScriptSource scriptSource;

    /* Executer of the scripts */
    protected ScriptRunner scriptRunner;

    /* Executer of code scripts */
    private CodeScriptRunner codeScriptRunner;

    /* Clearer of the database (removed all tables, sequences, ...) before updating */
    protected DBClearer dbClearer;

    /* Clearer of all database source code (types, triggers, functions, procedures, ...) */
    protected DBCodeClearer dbCodeClearer;

    /* Cleaner of the database (deletes all data from all tables before updating */
    protected DBCleaner dbCleaner;

    /* Disabler of constraints */
    protected ConstraintsDisabler constraintsDisabler;

    /* Database sequence updater */
    protected SequenceUpdater sequenceUpdater;

    /* Database DTD generator */
    protected DataSetStructureGenerator dataSetStructureGenerator;

    /* Indicates whether updating the database from scratch is enabled. If true, the database is cleared before updating
      if an already executed script is modified */
    protected boolean fromScratchEnabled;

    /* Indicates if database code should be cleared before installing a new version of the code  */
    protected boolean clearDbCodeEnabled;

    /* Indicates if foreign key and not null constraints should removed after updating the database structure */
    protected boolean disableConstraintsEnabled;

    /* Indicates whether a from scratch update should be performed when the previous update failed, but
       none of the scripts were modified since that last update. If true a new update will be tried only when
       changes were made to the script files */
    protected boolean keepRetryingAfterError;


    /**
     * Default constructor for testing.
     */
    public DBMaintainer() {
    }


    /**
     * Create a new instance of <code>DBMaintainer</code>, The concrete implementations of all helper classes are
     * derived from the given <code>Configuration</code> object.
     *
     * @param configuration the configuration, not null
     * @param dataSource    the data source, not null
     */
    public DBMaintainer(Properties configuration, DataSource dataSource) {
        scriptRunner = getConfiguredDatabaseTaskInstance(ScriptRunner.class, configuration, dataSource);
        codeScriptRunner = getConfiguredDatabaseTaskInstance(CodeScriptRunner.class, configuration, dataSource);
        versionSource = getConfiguredDatabaseTaskInstance(VersionSource.class, configuration, dataSource);
        scriptSource = getConfiguredDatabaseTaskInstance(ScriptSource.class, configuration, dataSource);

        boolean cleanDbEnabled = PropertyUtils.getBoolean(PROPKEY_DB_CLEANER_ENABLED, configuration);
        if (cleanDbEnabled) {
            dbCleaner = getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, dataSource);
        }

        fromScratchEnabled = PropertyUtils.getBoolean(PROPKEY_FROM_SCRATCH_ENABLED, configuration);
        keepRetryingAfterError = PropertyUtils.getBoolean(PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED, configuration);
        if (fromScratchEnabled) {
            dbClearer = getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource);
        }
        clearDbCodeEnabled = PropertyUtils.getBoolean(PROPKEY_CLEAR_DB_CODE_ENABLED, configuration);
        dbCodeClearer = getConfiguredDatabaseTaskInstance(DBCodeClearer.class, configuration, dataSource);

        disableConstraintsEnabled = PropertyUtils.getBoolean(PROPKEY_DISABLE_CONSTRAINTS_ENABLED, configuration);
        constraintsDisabler = getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class, configuration, dataSource);

        boolean updateSequences = PropertyUtils.getBoolean(PROPKEY_UPDATE_SEQUENCES_ENABLED, configuration);
        if (updateSequences) {
            sequenceUpdater = getConfiguredDatabaseTaskInstance(SequenceUpdater.class, configuration, dataSource);
        }

        boolean generateDtd = PropertyUtils.getBoolean(PROPKEY_GENERATE_DATA_SET_STRUCTURE_ENABLED, configuration);
        if (generateDtd) {
            dataSetStructureGenerator = getConfiguredDatabaseTaskInstance(DataSetStructureGenerator.class, configuration, dataSource);
        }
    }


    /**
     * Checks if the new scripts are available to update the version of the database. If yes, these scripts are
     * executed and the version number is increased. If an existing script has been modified, the database is
     * cleared and completely rebuilt from scratch. If an error occurs with one of the scripts, a
     * {@link UnitilsException} is thrown.
     */
    public void updateDatabase() {
        // Get current version
        Version currentVersion = versionSource.getDbVersion();

        // Clear the database and retrieve scripts
        List<VersionScriptPair> versionScriptPairs;
        boolean updateDatabaseFromScratch = updateDatabaseFromScratch(currentVersion);
        if (updateDatabaseFromScratch) {
            // constraints are removed before clearing the database, to be sure there will be no conflicts when dropping tables
            constraintsDisabler.disableConstraints();
            dbClearer.clearSchemas();
            dbCodeClearer.clearSchemasCode();
            versionScriptPairs = scriptSource.getAllScripts();
        } else {
            versionScriptPairs = scriptSource.getNewScripts(currentVersion);
        }

        // Check whether there are new scripts
        if (!versionScriptPairs.isEmpty()) {
            logger.info("Database update scripts have been found and will be executed on the database");

            // Remove data from the database, that could cause errors when executing scripts. Such as for example
            // when added a not null column.
            if (dbCleaner != null) {
                dbCleaner.cleanSchemas();
            }

            // Excute all of the scripts
            executeScripts(versionScriptPairs);

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

        if (updateDatabaseFromScratch // If the database structure was recreated from scratch, also recreate the database code
                || (!versionSource.isLastCodeUpdateSucceeded() && keepRetryingAfterError) // If the last code update failed, retry if configured to do so
                || scriptSource.getCodeScriptsTimestamp() > versionSource.getCodeScriptsTimestamp()) { // If a code script was added of changed, recreate the database code

            List<Script> codeScripts = scriptSource.getAllCodeScripts();
            if (!codeScripts.isEmpty()) {
                if (clearDbCodeEnabled && !updateDatabaseFromScratch) { // If updateDatabaseFromScratch == true, the schema code has already been cleared.
                    dbCodeClearer.clearSchemasCode();
                }
                executeCodeScripts(codeScripts);
            }
        }
    }


    /**
     * Checks whether the database should be updated from scratch or just incrementally.
     * The database needs to be rebuild in following cases:<ul>
     * <li>Some existing scripts were modified.</li>
     * <li>The last update of the database was unsuccessful.</li>
     * </ul>
     * The database will only be rebuilt from scratch if {@link #PROPKEY_FROM_SCRATCH_ENABLED} is set to true.
     * If the {@link #PROPKEY_KEEP_RETRYING_AFTER_ERROR_ENABLED} is set to false, the database
     * will only be rebuilt again after an unsuccessful build when changes were made to the script files.
     *
     * @param currentVersion The current database version, not null
     * @return True if a from scratch rebuild is needed, false otherwise
     */
    protected boolean updateDatabaseFromScratch(Version currentVersion) {
        if (scriptSource.existingScriptsModified(currentVersion)) {
            if (!fromScratchEnabled) {
                logger.warn("Existing database update scripts have been modified, but updating from scratch is disabled. The updated scripts are not executed again!!");
                return false;
            } else {
                logger.info("One or more existing database update scripts have been modified. Database will be cleared and rebuilt from scratch");
                return true;
            }
        } else if (!versionSource.isLastUpdateSucceeded()) {
            if (!fromScratchEnabled) {
                logger.warn("The previous database update failed, so it would be a good idea to rebuild the database from scratch. " +
                        "This is not done since updating from scratch is disabled!");
                return false;
            } else if (!keepRetryingAfterError) {
                logger.warn("The previous database update did not succeed and there were no modified script files. The updated scripts are not executed again!!");
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    /**
     * Executes the given scripts and updates the database version and state appropriatly. After each successful
     * script execution, the new version is stored in the database and marked as succesful.
     * If a script execution fails and fromScratch is enabled, that script version is stored in the database and
     * marked as unsuccesful. If fromScratch is not enabled, the last succesful version is stored in the database
     * that way, the next time an update is tried, the execution restarts from the last unsuccessful script.
     *
     * @param versionScriptPairs The scripts to execute, not null
     */
    protected void executeScripts(List<VersionScriptPair> versionScriptPairs) {
        for (VersionScriptPair versionScriptPair : versionScriptPairs) {
            try {
                scriptRunner.execute(versionScriptPair.getScript().getScriptContent());

            } catch (UnitilsException e) {
                logger.error("Error while executing script " + versionScriptPair.getScript().getFileName(), e);
                if (fromScratchEnabled) {
                    // If rebuilding from scratch is disabled, the version is not incremented, to give the chance
                    // of fixing the erroneous script.
                    // If rebuilding from scratch is enabled, the version is set to the version of the erroneous
                    // script anyway, so that the database is rebuilt from scratch when the erroneous script is
                    // fixed.
                    versionSource.setDbVersion(versionScriptPair.getVersion());
                    logger.info("Database version incremented to " + versionScriptPair.getVersion());
                }

                // mark the db update as unsuccessful
                logger.error("Current database version is " + versionSource.getDbVersion());
                versionSource.registerUpdateSucceeded(false);
                throw e;
            }

            // update the db version and mark as successful
            versionSource.setDbVersion(versionScriptPair.getVersion());
            versionSource.registerUpdateSucceeded(true);
            logger.info("Database version successfully incremented to " + versionScriptPair.getVersion());
        }
    }


    /**
     * Executes the given code scripts on the database and registers wether the update succeeded or not. If succeeded,
     * the timestamp of the scripts is registered in the database.
     *
     * @param codeScripts The code scripts to execute, not null
     */
    protected void executeCodeScripts(List<Script> codeScripts) {
        if (codeScripts.isEmpty()) {
            // nothing to do
            return;
        }

        for (Script codeScript : codeScripts) {
            try {
                codeScriptRunner.execute(codeScript.getScriptContent());

            } catch (UnitilsException e) {

                logger.error("Error while executing code script " + codeScript.getFileName(), e);
                versionSource.registerCodeUpdateSucceeded(false);
                throw e;
            }
        }
        // if the execution of all scripts succeeded, update the code scripts timestamp and mark as successful
        versionSource.setCodeScriptsTimestamp(scriptSource.getCodeScriptsTimestamp());
        versionSource.registerCodeUpdateSucceeded(true);
    }

}
