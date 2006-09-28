/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer;

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
import org.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.List;

/**
 * A class for performing automatic maintenance of a database.<br>
 * This class can org injected with implementations of a {@link VersionSource}, {@link ScriptSource} and a
 * {@link StatementHandler} to customize it's behavior, or a <code>java.lang.Properties</code> object and a
 * <code>javax.sql.DataSource</code>.
 * <p/>
 * The {@link DBMaintainer#updateDatabase()} method will use the {@link VersionSource} to check what is
 * the current version of the database, and use the {@link ScriptSource} to verifyAll if there are scripts
 * available with a higher version number. If yes, the statements in these scripts will org passed to the
 * {@link StatementHandler}, and the version will org set to the new value using the {@link VersionSource}.
 * <p/>
 * To obtain a properly configured <code>DBMaintainer</code>, invoke the contructor
 * {@link DBMaintainer(java.util.Properties, javax.sql.DataSource)} with a <code>DataSource</code> providing access
 * to the database and a <code>Properties</code> object containing following properties:
 * <ul>
 * <li>dbMaintainer.versionSource.className: Fully qualified name of the implementation of {@link VersionSource}
 * that is used. The recommeded value is {@link org.unitils.dbmaintainer.maintainer.version.DBVersionSource}, which will retrieve
 * the database version from the updated database schema itself. Another implementation could e.g. retrieve the version
 * from a file.</li>
 * <li>dbMaintainer.scriptSource.className: Fully qualified name of the implementation of {@link ScriptSource} that is
 * used. The recommeded value is {@link org.unitils.dbmaintainer.maintainer.script.FileScriptSource}, which will retrieve the
 * scripts from the local file system. Another implementation could e.g. retrieve the scripts directly from the
 * Version Control System.</li>
 * <li>dbMaintainer.statementHandler.className: Fully qualified name of the implementation of {@link StatementHandler}
 * that is used. The recommeded value is {@link org.unitils.dbmaintainer.handler.JDBCStatementHandler}, which will execute the
 * scripts using JDBC. Another implementation could e.g. execute these scripts with a vendor specific script executer.
 * </li>
 * </ul>
 */
public class DBMaintainer {

    /* Logger */
    private static final Logger logger = Logger.getLogger(DBMaintainer.class);

    /* Property key indicating if updating the database from scratch is enabled */
    private static final String PROPKEY_FROMSCRATCH_ENABLED = "dbMaintainer.fromScratch.enabled";

    /* Property key of the implementation class of {@link VersionSource} */
    private static final String PROPKEY_VERSIONSOURCE = "dbMaintainer.versionSource.className";

    /* Property key of the implementation class of {@link ScriptSource} */
    private static final String PROPKEY_SCRIPTSOURCE = "dbMaintainer.scriptSource.className";

    /* Property key of the implementation class of {@link VersionSource}  */
    private static final String PROPKEY_STATEMENTHANDLER = "dbMaintainer.statementHandler.className";

    /* Property key of the implementation class of the {@link DBClearer} */
    private static final String PROPKEY_DBCLEARER_START = "dbMaintainer.dbClearer.className";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    private static final String PROPKEY_DISABLECONSTRAINTS_ENABLED = "dbMaintainer.disableConstraints.enabled";

    /* Property key of the implementation class of {@link ConstraintsDisabler} */
    private static final String PROPKEY_CONSTRAINTSDISABLER_START = "constraintsDisabler.className";

    /* Property key indicating if the database constraints should org disabled after updating the database */
    private static final String PROPKEY_UPDATESEQUENCES_ENABLED = "dbMaintainer.updateSequences.enabled";

    /* Property key that indicates if a DTD is to be generated or not */
    private static final String PROPKEY_GENERATEDTD_ENABLED = "dbMaintainer.generateDTD.enabled";

    /* Property key of the implementation class of {@link SequenceDisabler} */
    private static final String PROPKEY_SEQUENCEUPDATER_START = "sequenceUpdater.className";

    /* Property key of the implementation class of {@link DtdGenerator} */
    private static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";


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
     * {@link ScriptSource} and {@link StatementHandler} are derived from the given <code>Properties</code> object.
     * These objects are initialized too using their init method and the given <code>Properties</code> and
     * <code>DataSource</code> object.
     *
     * @param dataSource
     */
    public DBMaintainer(DataSource dataSource) {

        Configuration configuration = UnitilsConfiguration.getInstance();

        String databaseDialect = configuration.getString(PROPKEY_DATABASE_DIALECT);

        versionSource = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_VERSIONSOURCE));
        versionSource.init(dataSource);

        scriptSource = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_SCRIPTSOURCE));
        scriptSource.init();

        StatementHandler statementHandler = new LoggingStatementHandlerDecorator((StatementHandler) ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_STATEMENTHANDLER)));
        statementHandler.init(dataSource);
        scriptRunner = new SQLScriptRunner(statementHandler);

        fromScratchEnabled = configuration.getBoolean(PROPKEY_FROMSCRATCH_ENABLED);
        if (fromScratchEnabled) {
            dbClearer = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DBCLEARER_START + "." + databaseDialect));
            dbClearer.init(dataSource, statementHandler);
        }

        boolean disableConstraints = configuration.getBoolean(PROPKEY_DISABLECONSTRAINTS_ENABLED);
        if (disableConstraints) {
            constraintsDisabler = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_CONSTRAINTSDISABLER_START + "." + databaseDialect));
            constraintsDisabler.init(dataSource, statementHandler);
        }

        boolean updateSequences = configuration.getBoolean(PROPKEY_UPDATESEQUENCES_ENABLED);
        if (updateSequences) {
            sequenceUpdater = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_SEQUENCEUPDATER_START + "." + databaseDialect));
            sequenceUpdater.init(dataSource, statementHandler);
        }

        boolean generateDtd = configuration.getBoolean(PROPKEY_GENERATEDTD_ENABLED);
        if (generateDtd) {
            dtdGenerator = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DTDGENERATOR_CLASSNAME));
            dtdGenerator.init(dataSource);
        }
    }

    /**
     * Checks if the new scripts are available to update the version of the database. If yes, these scripts are
     * executed and the version number is increased. If an error occurs with one of the scripts, a
     * {@link StatementHandlerException} is thrown
     *
     * @throws StatementHandlerException If an error occurs with one of the scripts
     */
    public void updateDatabase() throws StatementHandlerException {
        Version currentVersion = versionSource.getDbVersion();
        List<VersionScriptPair> versionScriptPairs = scriptSource.getScripts(currentVersion);
        if (versionScriptPairs.size() > 0) {
            if (scriptSource.shouldRunFromScratch(currentVersion)) {
                if (fromScratchEnabled) {
                    dbClearer.clearDatabase();
                } else {
                    throw new RuntimeException("Existing database update script has been modified, but updateing " +
                            "from scratch is not enabled");
                }
            }
            for (VersionScriptPair versionScriptPair : versionScriptPairs) {
                try {
                    scriptRunner.execute(versionScriptPair.getScript());
                } catch (StatementHandlerException e) {
                    logger.error("Error while executing script: " + versionScriptPair + "\nDatabase version not incremented", e);
                    logger.error("Current database version is " + versionSource.getDbVersion());
                    throw e;
                }
                versionSource.setDbVersion(versionScriptPair.getVersion());
                logger.info("Database version incremented to " + versionScriptPair.getVersion());
            }
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
     * Allows to set fromScratchEnabled property for unittesting purposes
     *
     * @param fromScratchEnabled
     */
    void setFromScratchEnabled(boolean fromScratchEnabled) {
        this.fromScratchEnabled = fromScratchEnabled;
    }

}
