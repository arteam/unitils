package org.unitils.database.databaseupdater;

import org.unitils.core.util.Configurable;

import java.util.Properties;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 11-feb-2009
 */
public interface DatabaseUpdater extends Configurable {

    boolean updateDatabase();
    
}
