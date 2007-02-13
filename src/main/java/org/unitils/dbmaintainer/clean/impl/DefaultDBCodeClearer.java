package org.unitils.dbmaintainer.clean.impl;

import org.unitils.dbmaintainer.clean.DBCodeClearer;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import static org.unitils.util.PropertyUtils.getStringList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.Properties;
import java.util.HashSet;
import java.util.List;

/**
 * Defines the contract for implementations that clear all database source code from a database schema, to avoid problems
 * on redeployment of database source code by the {@link org.unitils.dbmaintainer.DBMaintainer}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBCodeClearer extends DatabaseTask implements DBCodeClearer {


    /**
     * The key of the property that specifies which database items should not be deleted when clearing the database
     */
    public static final String PROPKEY_ITEMSTOPRESERVE = "dbMaintainer.clearDb.itemsToPreserve";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBCodeClearer.class);

    /**
     * Names of database items (tables, views, sequences or triggers) that should not be deleted when clearning the database
     */
    protected Set<String> itemsToPreserve = new HashSet<String>();


    protected void doInit(Properties configuration) {
        List<String> itemsToPreserveOrigCase = getStringList(PROPKEY_ITEMSTOPRESERVE, configuration);
        for (String itemToPreserve : itemsToPreserveOrigCase) {
            itemsToPreserve.add(dbSupport.toCorrectCaseIdentifier(itemToPreserve));
        }
    }


    /**
     * Clears all code from the database schema.
     */
    public void clearSchemaCode() throws StatementHandlerException {
        dropTriggers();
        dropTypes();
        // todo drop functions, stored procedures.
    }


    /**
     * Drops all triggers
     */
    protected void dropTriggers() throws StatementHandlerException {
        if (dbSupport.supportsTriggers()) {
            Set<String> triggerNames = dbSupport.getTriggerNames();
            for (String triggerName : triggerNames) {
                // check whether trigger needs to be preserved
                if (itemsToPreserve.contains(triggerName)) {
                    continue;
                }
                logger.debug("Dropping database trigger: " + triggerName);
                dbSupport.dropTrigger(triggerName);
            }
        }
    }


    /**
     * Drops all types.
     */
    protected void dropTypes() throws StatementHandlerException {
        if (dbSupport.supportsTypes()) {
            Set<String> typeNames = dbSupport.getTypeNames();
            for (String typeName : typeNames) {
                // check whether type needs to be preserved
                if (itemsToPreserve.contains(typeName)) {
                    continue;
                }
                logger.debug("Dropping database type: " + typeName);
                dbSupport.dropType(typeName);
            }
        }
    }

}
