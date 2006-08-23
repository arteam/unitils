package be.ordina.unitils.db.ant;

import be.ordina.unitils.db.config.DataSourceFactory;
import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.db.maintainer.DBMaintainer;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.ReflectionUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.sql.DataSource;
import java.util.Properties;

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
