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
package org.unitils.dbmaintainer.clean.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.clean.DBCodeClearer;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;
import static org.unitils.util.PropertyUtils.getStringList;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Defines the contract for implementations that clear all database source code from a database schema, to avoid problems
 * on redeployment of database source code by the {@link org.unitils.dbmaintainer.DBMaintainer}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBCodeClearer extends BaseDatabaseTask implements DBCodeClearer {


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
            // todo support all schemas
            itemsToPreserve.add(defaultDbSupport.toCorrectCaseIdentifier(itemToPreserve));
        }
    }


    /**
     * Clears all code from the database schema.
     */
    public void clearSchemasCode() {
        for (DbSupport dbSupport : dbSupports) {
            logger.info("Clearing (dropping) code in database schema " + dbSupport.getSchemaName());
            dropTriggers(dbSupport);
            dropTypes(dbSupport);
            // todo drop functions, stored procedures.
        }
    }


    /**
     * Drops all triggers
     *
     * @param dbSupport The database support, not null
     */
    protected void dropTriggers(DbSupport dbSupport) {
        if (!dbSupport.supportsTriggers()) {
            return;
        }
        Set<String> triggerNames = dbSupport.getTriggerNames();
        for (String triggerName : triggerNames) {
            // check whether trigger needs to be preserved
            if (itemsToPreserve.contains(triggerName)) {
                continue;
            }
            logger.debug("Dropping trigger " + triggerName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropTrigger(triggerName);
        }
    }


    /**
     * Drops all types.
     *
     * @param dbSupport The database support, not null
     */
    protected void dropTypes(DbSupport dbSupport) {
        if (!dbSupport.supportsTypes()) {
            return;
        }
        Set<String> typeNames = dbSupport.getTypeNames();
        for (String typeName : typeNames) {
            // check whether type needs to be preserved
            if (itemsToPreserve.contains(typeName)) {
                continue;
            }
            logger.debug("Dropping type " + typeName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropType(typeName);
        }
    }

}
