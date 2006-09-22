package be.ordina.unitils.dbmaintainer.ant;

import be.ordina.unitils.dbmaintainer.config.DataSourceFactory;
import be.ordina.unitils.util.ReflectionUtils;
import be.ordina.unitils.util.UnitilsConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 */
public abstract class BaseUnitilsTask extends Task {

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property keys of the database schema name */
    private static final String PROPKEY_DATABASE_USERNAME = "dataSource.userName";

    /* The pooled datasource instance */
    protected DataSource dataSource;

    /* The name of the database schema */
    protected String schemaName;


    public final void execute() throws BuildException {

        Configuration configuration = UnitilsConfiguration.getInstance();
        DataSourceFactory dataSourceFactory = ReflectionUtils.getInstance(configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init();
        dataSource = dataSourceFactory.createDataSource();
        schemaName = configuration.getString(PROPKEY_DATABASE_USERNAME);
        doExecute();
    }

    protected abstract void doExecute() throws BuildException;

}
