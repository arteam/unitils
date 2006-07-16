/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer;

import be.ordina.unitils.db.constraints.ConstraintsDisabler;
import be.ordina.unitils.db.dtd.DtdGenerator;
import be.ordina.unitils.db.handler.StatementHandler;
import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.db.maintainer.script.ScriptSource;
import be.ordina.unitils.db.maintainer.version.VersionSource;
import be.ordina.unitils.db.script.SQLScriptRunner;
import be.ordina.unitils.db.script.ScriptRunner;
import be.ordina.unitils.db.sequences.SequenceUpdater;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.ReflectionUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * A class for performing automatic maintenance of a database.<br>
 * This class can org injected with implementations of a {@link VersionSource}, {@link ScriptSource} and a
 * {@link StatementHandler} to customize it's behavior, or a <code>java.lang.Properties</code> object and a
 * <code>javax.sql.DataSource</code>.
 * <p/>
 * The {@link DBMaintainer#updateDatabase()} method will use the {@link VersionSource} to check what is
 * the current version of the database, and use the {@link ScriptSource} to verify if there are scripts
 * available with a higher version number. If yes, the statements in these scripts will org passed to the
 * {@link StatementHandler}, and the version will org set to the new value using the {@link VersionSource}.
 * <p/>
 * To obtain a properly configured <code>DBMaintainer</code>, invoke the contructor
 * {@link DBMaintainer(java.util.Properties, javax.sql.DataSource)} with a <code>DataSource</code> providing access
 * to the database and a <code>Properties</code> object containing following properties:
 * <ul>
 * <li>dbMaintainer.versionSource.className: Fully qualified name of the implementation of {@link VersionSource}
 * that is used. The recommeded value is {@link be.ordina.unitils.db.maintainer.version.DBVersionSource}, which will retrieve
 * the database version from the updated database schema itself. Another implementation could e.g. retrieve the version
 * from a file.</li>
 * <li>dbMaintainer.scriptSource.className: Fully qualified name of the implementation of {@link ScriptSource} that is
 * used. The recommeded value is {@link be.ordina.unitils.db.maintainer.script.FileScriptSource}, which will retrieve the
 * scripts from the local file system. Another implementation could e.g. retrieve the scripts directly from the
 * Version Control System.</li>
 * <li>dbMaintainer.statementHandler.className: Fully qualified name of the implementation of {@link StatementHandler}
 * that is used. The recommeded value is {@link be.ordina.unitils.db.handler.JDBCStatementHandler}, which will execute the
 * scripts using JDBC. Another implementation could e.g. execute these scripts with a vendor specific script executer.
 * </li>
 * </ul>
 */
public class DBMaintainer {

    /* Logger */
    private static final Logger logger = Logger.getLogger(DBMaintainer.class);

    /* Property key of the implementation class of {@link VersionSource} */
    private static final String PROPKEY_VERSIONSOURCE = "dbMaintainer.versionSource.className";

    /* Property key of the implementation class of {@link ScriptSource} */
    private static final String PROPKEY_SCRIPTSOURCE = "dbMaintainer.scriptSource.className";

    /* Property key of the implementation class of {@link VersionSource}  */
    private static final String PROPKEY_STATEMENTHANDLER = "dbMaintainer.statementHandler.className";

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

    /* Property key of the impelementation class of {@link DtdGenerator} */
    private static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";


    /* Provider of the current version of the database, and means to increment it */
    private VersionSource versionSource;

    /* Provider of scripts for updating the database to a higher version */
    private ScriptSource scriptSource;

    /* Executer of the scripts */
    private ScriptRunner scriptRunner;

    /* Disabler of constraints */
    private ConstraintsDisabler constraintsDisabler;

    /* Database sequence updater */
    private SequenceUpdater sequenceUpdater;

    /* Database DTD generator */
    private DtdGenerator dtdGenerator;


    /* Indicates if the database constraints are to be disabled */
    private boolean disableConstraints;

    /* Indicates if the database sequences have to be updated */
    private boolean updateSequences;

    /* Indicates if a DTD is to be generated */
    private boolean generateDtd;


    /**
     * Create a new instance of <code>DBMaintainer</code>, using the given instances of {@link VersionSource},
     * {@link ScriptSource} and {@link SQLScriptRunner}.
     *
     * @param versionSource
     * @param scriptSource
     * @param scriptRunner
     */
    public DBMaintainer(VersionSource versionSource, ScriptSource scriptSource,
                        SQLScriptRunner scriptRunner, ConstraintsDisabler constraintsDisabler,
                        SequenceUpdater sequenceUpdater, DtdGenerator dtdGenerator) {
        this.versionSource = versionSource;
        this.scriptSource = scriptSource;
        this.scriptRunner = scriptRunner;

        disableConstraints = (constraintsDisabler != null);
        this.constraintsDisabler = constraintsDisabler;

        updateSequences = (sequenceUpdater != null);
        this.sequenceUpdater = sequenceUpdater;

        generateDtd = (dtdGenerator != null);
        this.dtdGenerator = dtdGenerator;
    }

    /**
     * Create a new instance of <code>DBMaintainer</code>, The concrete implementations of {@link VersionSource},
     * {@link ScriptSource} and {@link StatementHandler} are derived from the given <code>Properties</code> object.
     * These objects are initialized too using their init method and the given <code>Properties</code> and
     * <code>DataSource</code> object.
     *
     * @param properties
     * @param dataSource
     */
    public DBMaintainer(Properties properties, DataSource dataSource) {
        versionSource = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_VERSIONSOURCE));
        versionSource.init(properties, dataSource);

        scriptSource = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SCRIPTSOURCE));
        scriptSource.init(properties);

        StatementHandler statementHandler = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_STATEMENTHANDLER));
        statementHandler.init(properties, dataSource);
        scriptRunner = new SQLScriptRunner(statementHandler);

        disableConstraints = PropertiesUtils.getBooleanPropertyRejectNull(properties, PROPKEY_DISABLECONSTRAINTS_ENABLED);
        if (disableConstraints) {
            constraintsDisabler = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_CONSTRAINTSDISABLER_START + "." + PropertiesUtils.getPropertyRejectNull(properties,
                        PROPKEY_DATABASE_DIALECT)));
            constraintsDisabler.init(dataSource, statementHandler);
        }

        updateSequences = PropertiesUtils.getBooleanPropertyRejectNull(properties, PROPKEY_UPDATESEQUENCES_ENABLED);
        if (updateSequences) {
            sequenceUpdater = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_SEQUENCEUPDATER_START + "." + PropertiesUtils.getPropertyRejectNull(properties,
                    PROPKEY_DATABASE_DIALECT)));
            sequenceUpdater.init(properties, dataSource);
        }

        generateDtd = PropertiesUtils.getBooleanPropertyRejectNull(properties, PROPKEY_GENERATEDTD_ENABLED);
        if (generateDtd) {
            dtdGenerator = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DTDGENERATOR_CLASSNAME));
            dtdGenerator.init(properties, dataSource);
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
        long version = versionSource.getDbVersion();
        version += 1;
        String dbChange = scriptSource.getScript(version);
        if (dbChange != null) {
            while (dbChange != null) {
                try {
                    scriptRunner.execute(dbChange);
                } catch (StatementHandlerException e) {
                    logger.error("Error while executing script: " + dbChange + "\nDatabase version not incremented", e);
                    logger.error("Current database version is " + versionSource.getDbVersion());
                    throw e;
                }
                versionSource.setDbVersion(version);
                logger.info("Database version incremented to " + version);
                version += 1;
                dbChange = scriptSource.getScript(version);
            }
            if (disableConstraints) {
                constraintsDisabler.disableConstraints();
            }
            if (updateSequences) {
                sequenceUpdater.updateSequences();
            }
            if (generateDtd) {
                dtdGenerator.generateDtd();
            }
        }
    }

}
