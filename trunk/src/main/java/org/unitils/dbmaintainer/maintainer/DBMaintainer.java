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
package org.unitils.dbmaintainer.maintainer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.constraints.ConstraintsDisabler;
import org.unitils.dbmaintainer.dtd.DtdGenerator;
import org.unitils.dbmaintainer.handler.LoggingStatementHandlerDecorator;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.script.ScriptSource;
import org.unitils.dbmaintainer.maintainer.version.Version;
import org.unitils.dbmaintainer.maintainer.version.VersionSource;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.sequences.SequenceUpdater;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * A class for performing automatic maintenance of a database.<br>
 * This class must be configured with implementations of a {@link VersionSource}, {@link ScriptSource}, a
 * {@link ScriptRunner}, {@link DBClearer}, {@link DBCleaner}, {@link ConstraintsDisabler}, {@link SequenceUpdater} and
 * a {@link DtdGenerator}
 * <p/>
 * The {@link #updateDatabase()} method check what is the current version of the database, and see if existing scripts
 * have been modified. If yes, the database is cleared and all available database scripts, are executed on the database.
 * If no existing scripts have been modified, but new scripts were added, only the new scripts are executed.
 * Before executing an update, data from the database is removed, to avoid problems when e.g. adding a not null column.
 * <p/>
 * If a database update causes an error, a {@link StatementHandlerException} is thrown. After a failing update, the
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
 * {@link #DBMaintainer(Configuration,DataSource)} with a <code>TestDataSource</code> providing access
 * to the database and a <code>Configuration</code> object containing all necessary properties.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainer {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBMaintainer.class);

    /* Property key of the database dialect */
    public static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /* Property key of the implementation of the DbSupport interface */
    public static final String PROPKEY_DBSUPPORT_CLASSNAME = "dbMaintainer.dbSupport.className";

    /* Property indicating if deleting all data from all tables before updating is enabled */
    public static final String PROPKEY_DBCLEANER_ENABLED = "dbMaintainer.cleanDb.enabled";

    /* Property key indicating if updating the database from scratch is enabled */
    public static final String PROPKEY_FROMSCRATCH_ENABLED = "dbMaintainer.fromScratch.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_UPDATESEQUENCES_ENABLED = "dbMaintainer.updateSequences.enabled";

    /* Property key that indicates if a DTD is to be generated or not */
    public static final String PROPKEY_GENERATEDTD_ENABLED = "dbMaintainer.generateDTD.enabled";

    /* Provider of the current version of the database, and means to increment it */
    private VersionSource versionSource;

    /* Provider of scripts for updating the database to a higher version */
    private ScriptSource scriptSource;

    /* Executer of the scripts */
    private ScriptRunner scriptRunner;

    /* Clearer of the database (removed all tables, sequences, ...) before updating */
    private DBClearer dbClearer;

    /* Cleaner of the database (deletes all data from all tables before updating */
    private DBCleaner dbCleaner;

    /* Disabler of constraints */
    private ConstraintsDisabler constraintsDisabler;

    /* Database sequence updater */
    private SequenceUpdater sequenceUpdater;

    /* Database DTD generator */
    private DtdGenerator dtdGenerator;

    /* Indicates if updateing the database from scratch is enabled. If yes, the database is cleared before updateing
      if an already executed script is modified */
    private boolean fromScratchEnabled;


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
    public DBMaintainer(Configuration configuration, DataSource dataSource) {

        StatementHandler statementHandler = new LoggingStatementHandlerDecorator(DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration, dataSource));

        scriptRunner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(ScriptRunner.class, configuration, dataSource, statementHandler);
        versionSource = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(VersionSource.class, configuration, dataSource, statementHandler);
        scriptSource = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(ScriptSource.class, configuration, dataSource, statementHandler);

        boolean cleanDbEnabled = configuration.getBoolean(PROPKEY_DBCLEANER_ENABLED);
        if (cleanDbEnabled) {
            dbCleaner = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBCleaner.class, configuration, dataSource, statementHandler);
        }

        fromScratchEnabled = configuration.getBoolean(PROPKEY_FROMSCRATCH_ENABLED);
        if (fromScratchEnabled) {
            dbClearer = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource, statementHandler);
        }

        boolean disableConstraints = configuration.getBoolean(PROPKEY_DISABLECONSTRAINTS_ENABLED);
        if (disableConstraints) {
            constraintsDisabler = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(ConstraintsDisabler.class, configuration, dataSource, statementHandler);
        }

        boolean updateSequences = configuration.getBoolean(PROPKEY_UPDATESEQUENCES_ENABLED);
        if (updateSequences) {
            sequenceUpdater = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(SequenceUpdater.class, configuration, dataSource, statementHandler);
        }

        boolean generateDtd = configuration.getBoolean(PROPKEY_GENERATEDTD_ENABLED);
        if (generateDtd) {
            dtdGenerator = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DtdGenerator.class, configuration, dataSource, statementHandler);
        }
    }


    /**
     * Checks if the new scripts are available to update the version of the database. If yes, these scripts are
     * executed and the version number is increased. If an existing script has been modified, the database is
     * cleared and completely rebuilt from scratch. If an error occurs with one of the scripts, a
     * {@link StatementHandlerException} is thrown.
     *
     * @throws StatementHandlerException If an error occurs with one of the scripts
     */
    public void updateDatabase() throws StatementHandlerException {
        Version currentVersion = versionSource.getDbVersion();
        boolean rebuildDatabaseFromScratch = false;
        if (scriptSource.existingScriptsModified(currentVersion)) {
            if (fromScratchEnabled) {
                logger.info("One or more existing database update scripts have been modified. Database will be cleared and rebuilt from scratch");
                rebuildDatabaseFromScratch = true;
            } else {
                logger.warn("Existing database update scripts have been modified, but updating from scratch is disabled. The updated scripts are not executed again!!");
            }
        }

        List<VersionScriptPair> versionScriptPairs;
        if (rebuildDatabaseFromScratch) {
            dbClearer.clearDatabase();
            versionScriptPairs = scriptSource.getAllScripts();
        } else {
            versionScriptPairs = scriptSource.getNewScripts(currentVersion);
        }

        if (versionScriptPairs.size() > 0) {
            logger.info("Database update scripts have been found and will be executed on the database");
            // Remove data from the database, that could cause errors when executing scripts. Such as for example
            // when added a not null column.
            if (dbCleaner != null) {
                dbCleaner.cleanDatabase();
            }
            for (VersionScriptPair versionScriptPair : versionScriptPairs) {
                try {
                    scriptRunner.execute(versionScriptPair.getScript());
                } catch (StatementHandlerException e) {
                    logger.error("Error while executing script with version number " + versionScriptPair.getVersion() + ": " + versionScriptPair.getScript(), e);
                    if (fromScratchEnabled) {
                        // If rebuilding from scratch is disabled, the version is not incremented, to give the chance
                        // of fixing the erroneous script.
                        // If rebuilding from scratch is enabled, the version is set to the version of the erroneous
                        // script anyway, so that the database is rebuilt from scratch when the erroneous script is
                        // fixed.
                        versionSource.setDbVersion(versionScriptPair.getVersion());
                        logger.info("Database version incremented to " + versionScriptPair.getVersion());
                    }
                    logger.error("Current database version is " + versionSource.getDbVersion());
                    // The fact that the update didn't succeed, is only stored for information purposes
                    versionSource.registerUpdateSucceeded(false);
                    throw e;
                }
                versionSource.setDbVersion(versionScriptPair.getVersion());
                logger.info("Database version successfully incremented to " + versionScriptPair.getVersion());
                // The fact that the update didn't succeed, is only stored for information purposes
                versionSource.registerUpdateSucceeded(true);
            }

            // Disable FK and not null constraints, if enabled
            if (constraintsDisabler != null) {
                constraintsDisabler.disableConstraints();
            }
            // Update sequences to a sufficiently high value, if enabled
            if (sequenceUpdater != null) {
                sequenceUpdater.updateSequences();
            }
            // Generate a DTD to enable validation and completion in data xml files, if enabled
            if (dtdGenerator != null) {
                dtdGenerator.generateDtd();
            }
        }
    }


    /**
     * Sets the fromScratchEnabled property
     *
     * @param fromScratchEnabled enabled or not
     */
    void setFromScratchEnabled(boolean fromScratchEnabled) {
        this.fromScratchEnabled = fromScratchEnabled;
    }

}
