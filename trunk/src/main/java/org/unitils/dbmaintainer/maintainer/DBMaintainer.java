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
import org.apache.log4j.Logger;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.constraints.ConstraintsDisabler;
import org.unitils.dbmaintainer.dtd.DtdGenerator;
import org.unitils.dbmaintainer.handler.LoggingStatementHandlerDecorator;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.script.ScriptSource;
import org.unitils.dbmaintainer.maintainer.version.Version;
import org.unitils.dbmaintainer.maintainer.version.VersionSource;
import org.unitils.dbmaintainer.script.SQLScriptRunner;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.sequences.SequenceUpdater;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * A class for performing automatic maintenance of a database.<br>
 * This class must be configured with implementations of a {@link VersionSource}, {@link ScriptSource}, a
 * {@link ScriptRunner}, {@link DBClearer}, {@link ConstraintsDisabler}, {@link SequenceUpdater} and a
 * {@link DtdGenerator}
 * <p/>
 * The {@link #updateDatabase()} method will use the {@link VersionSource} to check what is
 * the current version of the database. The {@link ScriptSource} is used to check if existing scripts have
 * been modified. If yes, the database is cleared by invoking the {@link DBClearer} and all database scripts,
 * obtained from the {@link ScriptSource} are executed on the database. If no existing scripts have been
 * modified, but new scripts have been added, only the new scripts are executed.
 * <p/>
 * After updating the database, following steps are optionally executed on the database (depending on the configuration):
 * <ul>
 * <li>Foreign key and not null constraints are disabled</li>
 * <li>Sequences that have a value lower than a configured treshold, are updated to a value equal to or largen than this
 * treshold</li>
 * <li>A DTD is generated that describes the database's table structure, to use in test data XML files</li>
 * </ul>
 * <p/>
 * To obtain a properly configured <code>DBMaintainer</code>, invoke the contructor
 * {@link #DBMaintainer(Configuration,DataSource)} with a <code>TestDataSource</code> providing access
 * to the database and a <code>Configuration</code> object containing all necessary properties.
 * <p/>
 * todo clear database before updating
 */
public class DBMaintainer {

    /* Logger */
    private static final Logger logger = Logger.getLogger(DBMaintainer.class);

    /* Property key indicating if updating the database from scratch is enabled */
    public static final String PROPKEY_FROMSCRATCH_ENABLED = "dbMaintainer.fromScratch.enabled";

    /* Property key of the implementation class of {@link VersionSource} */
    public static final String PROPKEY_VERSIONSOURCE = "dbMaintainer.versionSource.className";

    /* Property key of the implementation class of {@link ScriptSource} */
    public static final String PROPKEY_SCRIPTSOURCE = "dbMaintainer.scriptSource.className";

    /* Property key of the implementation class of {@link VersionSource}  */
    public static final String PROPKEY_STATEMENTHANDLER = "dbMaintainer.statementHandler.className";

    /* Property key of the implementation class of the {@link DBClearer} */
    public static final String PROPKEY_DBCLEARER_START = "dbMaintainer.dbClearer.className";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* Property key of the implementation class of {@link ConstraintsDisabler} */
    public static final String PROPKEY_CONSTRAINTSDISABLER_START = "constraintsDisabler.className";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    public static final String PROPKEY_UPDATESEQUENCES_ENABLED = "dbMaintainer.updateSequences.enabled";

    /* Property key that indicates if a DTD is to be generated or not */
    public static final String PROPKEY_GENERATEDTD_ENABLED = "dbMaintainer.generateDTD.enabled";

    /* Property key of the implementation class of {@link SequenceDisabler} */
    public static final String PROPKEY_SEQUENCEUPDATER_START = "sequenceUpdater.className";

    /* Property key of the implementation class of {@link DtdGenerator} */
    public static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    public static final String PROPKEY_DATABASE_DIALECT = "database.dialect";


    /* Provider of the current version of the database, and means to increment it */
    private VersionSource versionSource;

    /* Provider of scripts for updating the database to a higher version */
    private ScriptSource scriptSource;

    /* Executer of the scripts */
    private ScriptRunner scriptRunner;

    /* Clearer of the database (removed all tables, sequences, ...) before updating */
    private DBClearer dbClearer;

    /* Disabler of constraints */
    private ConstraintsDisabler constraintsDisabler;

    /* Database sequence updater */
    private SequenceUpdater sequenceUpdater;

    /* Database DTD generator */
    private DtdGenerator dtdGenerator;


    /* Indicates if updateing the database from scratch is enabled. If yes, the database is cleared before updateing
      if an already executed script is modified */
    private boolean fromScratchEnabled;

    public DBMaintainer() {
    }

    /**
     * Create a new instance of <code>DBMaintainer</code>, The concrete implementations of {@link VersionSource},
     * {@link ScriptSource} and {@link StatementHandler} are derived from the given <code>Configuration</code> object.
     * These objects are initialized using their init method and the given <code>Configuration</code> and
     * <code>TestDataSource</code> object.
     *
     * @param configuration
     * @param dataSource
     */
    public DBMaintainer(Configuration configuration, DataSource dataSource) {

        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);
        StatementHandler statementHandler = new LoggingStatementHandlerDecorator((StatementHandler) ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_STATEMENTHANDLER)));
        statementHandler.init(configuration, dataSource);

        versionSource = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_VERSIONSOURCE));
        versionSource.init(configuration, dataSource, statementHandler);

        scriptSource = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_SCRIPTSOURCE));
        scriptSource.init(configuration);

        scriptRunner = new SQLScriptRunner(statementHandler);

        fromScratchEnabled = configuration.getBoolean(PROPKEY_FROMSCRATCH_ENABLED);
        if (fromScratchEnabled) {
            dbClearer = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DBCLEARER_START + "." + databaseDialect));
            dbClearer.init(configuration, dataSource, statementHandler);
        }

        boolean disableConstraints = configuration.getBoolean(PROPKEY_DISABLECONSTRAINTS_ENABLED);
        if (disableConstraints) {
            constraintsDisabler = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_CONSTRAINTSDISABLER_START + "." + databaseDialect));
            constraintsDisabler.init(configuration, dataSource, statementHandler);
        }

        boolean updateSequences = configuration.getBoolean(PROPKEY_UPDATESEQUENCES_ENABLED);
        if (updateSequences) {
            sequenceUpdater = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_SEQUENCEUPDATER_START + "." + databaseDialect));
            sequenceUpdater.init(configuration, dataSource, statementHandler);
        }

        boolean generateDtd = configuration.getBoolean(PROPKEY_GENERATEDTD_ENABLED);
        if (generateDtd) {
            dtdGenerator = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DTDGENERATOR_CLASSNAME));
            dtdGenerator.init(configuration, dataSource);
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
        if (!versionSource.lastUpdateSucceeded()) {
            if (fromScratchEnabled) {
                logger.info("Last update didn't succeed. Database will be cleared and rebuilt from scratch");
                rebuildDatabaseFromScratch = true;
            } else {
                logger.warn("Last update didn't succeed, but updating the database from scratch is not enabled. Trying" +
                        " incremental update anyway");
            }
        } else {
            if (scriptSource.existingScriptsModified(currentVersion)) {
                if (fromScratchEnabled) {
                    rebuildDatabaseFromScratch = true;
                } else {
                    throw new UnitilsException("Existing database update scripts have been modified, but updating " +
                            "from scratch is disabled");
                }
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
            for (VersionScriptPair versionScriptPair : versionScriptPairs) {
                try {
                    scriptRunner.execute(versionScriptPair.getScript());
                } catch (StatementHandlerException e) {
                    logger.error("Error while executing script: " + versionScriptPair.getScript() + "\nDatabase version not incremented", e);
                    logger.error("Current database version is " + versionSource.getDbVersion());
                    versionSource.registerUpdateSucceeded(false);
                    throw e;
                }
                versionSource.setDbVersion(versionScriptPair.getVersion());
                logger.info("Database version incremented to " + versionScriptPair.getVersion());
            }
            versionSource.registerUpdateSucceeded(true);
            if (constraintsDisabler != null) {
                constraintsDisabler.disableConstraints();
            }
            if (sequenceUpdater != null) {
                sequenceUpdater.updateSequences();
            }
            if (dtdGenerator != null) {
                dtdGenerator.generateDtd();
            }
        }
    }

    /**
     * Allows setting fromScratchEnabled property programmatically
     *
     * @param fromScratchEnabled
     */
    void setFromScratchEnabled(boolean fromScratchEnabled) {
        this.fromScratchEnabled = fromScratchEnabled;
    }

}
