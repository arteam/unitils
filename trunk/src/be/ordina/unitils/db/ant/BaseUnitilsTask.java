package be.ordina.unitils.db.ant;

import be.ordina.unitils.db.config.DataSourceFactory;
import be.ordina.unitils.util.PropertiesUtils;
import be.ordina.unitils.util.ReflectionUtils;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public abstract class BaseUnitilsTask extends Task {

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property keys of the database schema name */
    private static final String PROPKEY_DATABASE_USERNAME = "dataSource.userName";

    private static final Logger logger = Logger.getLogger(UpdateDatabaseTask.class);

    private String propertiesFileName;

    /* The configuration (daotest.properties) */
    protected Properties properties;

    /* The pooled datasource instance */
    protected DataSource dataSource;

    /* The name of the database schema */
    protected String schemaName;


    public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    public final void execute() throws BuildException {
        properties = PropertiesUtils.loadPropertiesFromFile(propertiesFileName);
        DataSourceFactory dataSourceFactory = ReflectionUtils.getInstance(PropertiesUtils.getPropertyRejectNull(properties,
                PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init(properties);
        dataSource = dataSourceFactory.createDataSource();
        schemaName = PropertiesUtils.getPropertyRejectNull(properties, PROPKEY_DATABASE_USERNAME);
        doExecute();
    }

    protected abstract void doExecute() throws BuildException;

}
