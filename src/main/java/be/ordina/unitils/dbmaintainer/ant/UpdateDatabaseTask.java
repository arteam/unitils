package be.ordina.unitils.dbmaintainer.ant;

import be.ordina.unitils.dbmaintainer.handler.StatementHandlerException;
import be.ordina.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;

/**
 * @author Filip Neven
 */
public class UpdateDatabaseTask extends BaseUnitilsTask {

    private static final Logger logger = Logger.getLogger(UpdateDatabaseTask.class);

    public void doExecute() throws BuildException {
        DBMaintainer dbMaintainer = new DBMaintainer(properties, dataSource);
        try {
            dbMaintainer.updateDatabase();
        } catch (StatementHandlerException e) {
            logger.error(e);
            throw new BuildException("Error updateing database", e);
        }
    }

}
