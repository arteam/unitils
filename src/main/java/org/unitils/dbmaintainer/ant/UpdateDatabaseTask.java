/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.ant;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

/**
 * Ant task that updates the database to the latest version. Invokes the {@link DBMaintainer} as it is configured in the
 * Unitils configuration.
 */
public class UpdateDatabaseTask extends BaseUnitilsTask {

    /* Logger for this class */
    private static final Logger logger = Logger.getLogger(UpdateDatabaseTask.class);

    /**
     * Updates the database to the latest version. Invokes {@link DBMaintainer} as it is configured in the Unitils
     * configuration.
     *
     * @throws BuildException
     */
    public void doExecute() throws BuildException {
        Configuration configuration = Unitils.getInstance().getConfiguration();
        DBMaintainer dbMaintainer = new DBMaintainer(configuration, dataSource);
        try {
            dbMaintainer.updateDatabase();
        } catch (StatementHandlerException e) {
            logger.error(e);
            throw new BuildException("Error updateing database", e);
        }
    }

}
