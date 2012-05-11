/*
 * Copyright 2012,  Unitils.org
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
package org.unitilsnew.database.dbmaintain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.DbMaintainer;
import org.dbmaintain.MainFactory;
import org.dbmaintain.structure.clean.DBCleaner;
import org.dbmaintain.structure.clear.DBClearer;
import org.dbmaintain.structure.constraint.ConstraintsDisabler;
import org.dbmaintain.structure.sequence.SequenceUpdater;
import org.unitilsnew.core.annotation.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbMaintainWrapper {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DbMaintainWrapper.class);

    protected DbMaintainConfiguration dbMaintainConfiguration;
    protected DbMaintainDatabaseConnectionManager dbMaintainDatabaseConnectionManager;
    /* Indicates if the DBMaintain should be invoked to update the database */
    protected boolean dbMaintainEnabled;

    protected MainFactory mainFactory;
    /* The registered database update listeners that will be called when db-maintain has updated the database */
    protected List<DatabaseUpdateListener> databaseUpdateListeners = new ArrayList<DatabaseUpdateListener>();
    /* True if update database has already been called */
    protected boolean updateDatabaseCalled = false;


    public DbMaintainWrapper(DbMaintainConfiguration dbMaintainConfiguration, DbMaintainDatabaseConnectionManager dbMaintainDatabaseConnectionManager, @Property("database.dbMaintain.enabled") boolean dbMaintainEnabled) {
        this.dbMaintainConfiguration = dbMaintainConfiguration;
        this.dbMaintainDatabaseConnectionManager = dbMaintainDatabaseConnectionManager;
        this.dbMaintainEnabled = dbMaintainEnabled;
    }


    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes.
     *
     * @return True if an update occurred, false if the database was up to date
     * @see {@link org.dbmaintain.DbMaintainer}
     */
    public boolean updateDatabaseIfNeeded() {
        if (updateDatabaseCalled) {
            return false;
        }
        if (!dbMaintainEnabled) {
            logger.info("DbMaintain is disabled. No database updates will be performed.");
            return false;
        }
        boolean databaseUpdated = updateDatabase();
        updateDatabaseCalled = true;
        if (databaseUpdated) {
            notifyDatabaseUpdateListeners();
        }
        return databaseUpdated;
    }

    public void markDatabaseAsUpToDate() {
        MainFactory mainFactory = getMainFactory();
        DbMaintainer dbMaintainer = mainFactory.createDbMaintainer();
        dbMaintainer.markDatabaseAsUpToDate();
    }

    public void clearDatabase() {
        MainFactory mainFactory = getMainFactory();
        DBClearer dbClearer = mainFactory.createDBClearer();
        dbClearer.clearDatabase();
    }

    public void cleanDatabase() {
        MainFactory mainFactory = getMainFactory();
        DBCleaner dbCleaner = mainFactory.createDBCleaner();
        dbCleaner.cleanDatabase();
    }

    public void disableConstraints() {
        MainFactory mainFactory = getMainFactory();
        ConstraintsDisabler constraintsDisabler = mainFactory.createConstraintsDisabler();
        constraintsDisabler.disableConstraints();
    }

    public void updateSequences() {
        MainFactory mainFactory = getMainFactory();
        SequenceUpdater sequenceUpdater = mainFactory.createSequenceUpdater();
        sequenceUpdater.updateSequences();
    }


    public void registerDatabaseUpdateListener(DatabaseUpdateListener databaseUpdateListener) {
        databaseUpdateListeners.add(databaseUpdateListener);
    }

    public void unregisterDatabaseUpdateListener(DatabaseUpdateListener databaseUpdateListener) {
        databaseUpdateListeners.remove(databaseUpdateListener);
    }


    protected boolean updateDatabase() {
        logger.info("Checking if database(s) have to be updated.");
        MainFactory mainFactory = getMainFactory();
        DbMaintainer dbMaintainer = mainFactory.createDbMaintainer();
        return dbMaintainer.updateDatabase(false);
    }

    protected void notifyDatabaseUpdateListeners() {
        for (DatabaseUpdateListener databaseUpdateListener : databaseUpdateListeners) {
            databaseUpdateListener.databaseWasUpdated();
        }
    }

    protected MainFactory getMainFactory() {
        if (mainFactory == null) {
            Properties dbMaintainProperties = dbMaintainConfiguration.getProperties();
            mainFactory = new MainFactory(dbMaintainProperties, dbMaintainDatabaseConnectionManager);
        }
        return mainFactory;
    }
}
