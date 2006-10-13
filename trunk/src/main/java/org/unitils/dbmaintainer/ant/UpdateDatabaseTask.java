package org.unitils.dbmaintainer.ant;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

/**
 * @author Filip Neven
 */
public class UpdateDatabaseTask extends BaseUnitilsTask {

    private static final Logger logger = Logger.getLogger(UpdateDatabaseTask.class);

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
