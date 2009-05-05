package org.unitils.database.databaseupdater.dbmaintain;

import org.unitils.core.util.Configurable;
import org.dbmaintain.dbsupport.DbSupport;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 24-feb-2009
 */
public interface DbMaintainFacade extends Configurable {

    boolean updateDatabase();

    void markDatabaseAsUpToDate();

    void clearDatabase();

    void cleanDatabase();

    void disableConstraints();

    void updateSequences();

    DbSupport getDefaultDbSupport();
}
