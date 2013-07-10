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
package org.unitils.database.dbmaintain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.DbMaintainer;
import org.dbmaintain.MainFactory;
import org.dbmaintain.database.Database;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.Databases;
import org.dbmaintain.structure.clean.DBCleaner;
import org.dbmaintain.structure.clear.DBClearer;
import org.dbmaintain.structure.constraint.ConstraintsDisabler;
import org.dbmaintain.structure.sequence.SequenceUpdater;
import org.unitils.core.UnitilsException;
import org.unitilsnew.core.annotation.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbMaintainWrapper {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DbMaintainWrapper.class);

    protected MainFactory mainFactory;
    /* Indicates if the DBMaintain should be invoked to update the database */
    protected boolean dbMaintainEnabled;
    /* The registered database update listeners that will be called when db-maintain has updated the database */
    protected List<DatabaseUpdateListener> databaseUpdateListeners = new ArrayList<DatabaseUpdateListener>();
    /* True if update database has already been called */
    protected boolean updateDatabaseCalled = false;


    public DbMaintainWrapper(MainFactory mainFactory, @Property("database.dbMaintain.enabled") boolean dbMaintainEnabled) {
        this.mainFactory = mainFactory;
        this.dbMaintainEnabled = dbMaintainEnabled;
    }


    public Database getDatabase(String databaseName) {
        Databases databases = getDatabases();
        if (databaseName == null) {
            return databases.getDefaultDatabase();
        }
        try {
            return databases.getDatabase(databaseName);
        } catch (DatabaseException e) {
            throw new UnitilsException("Unable to get database with name: " + databaseName, e);
        }
    }

    public Databases getDatabases() {
        try {
            return mainFactory.getDatabases();
        } catch (DatabaseException e) {
            throw new UnitilsException("Unable to get databases", e);
        }
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
            updateDatabaseCalled = true;
            return false;
        }
        updateDatabaseCalled = true;
        return updateDatabase();
    }

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes.
     *
     * @return True if an update occurred, false if the database was up to date
     * @see {@link org.dbmaintain.DbMaintainer}
     */
    public boolean updateDatabase() {
        logger.info("Checking if database(s) have to be updated.");
        DbMaintainer dbMaintainer = mainFactory.createDbMaintainer();

        boolean databaseUpdated = dbMaintainer.updateDatabase(false);
        if (databaseUpdated) {
            notifyDatabaseUpdateListeners();
        }
        return databaseUpdated;
    }

    public void markDatabaseAsUpToDate() {
        DbMaintainer dbMaintainer = mainFactory.createDbMaintainer();
        dbMaintainer.markDatabaseAsUpToDate();
    }

    public void clearDatabase() {
        DBClearer dbClearer = mainFactory.createDBClearer();
        dbClearer.clearDatabase();
    }

    public void cleanDatabase() {
        DBCleaner dbCleaner = mainFactory.createDBCleaner();
        dbCleaner.cleanDatabase();
    }

    public void disableConstraints() {
        ConstraintsDisabler constraintsDisabler = mainFactory.createConstraintsDisabler();
        constraintsDisabler.disableConstraints();
    }

    public void updateSequences() {
        SequenceUpdater sequenceUpdater = mainFactory.createSequenceUpdater();
        sequenceUpdater.updateSequences();
    }


    public void registerDatabaseUpdateListener(DatabaseUpdateListener databaseUpdateListener) {
        databaseUpdateListeners.add(databaseUpdateListener);
    }

    public void unregisterDatabaseUpdateListener(DatabaseUpdateListener databaseUpdateListener) {
        databaseUpdateListeners.remove(databaseUpdateListener);
    }


    protected void notifyDatabaseUpdateListeners() {
        for (DatabaseUpdateListener databaseUpdateListener : databaseUpdateListeners) {
            databaseUpdateListener.databaseWasUpdated();
        }
    }
}
