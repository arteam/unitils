package org.unitils.dbmaintainer.ant;

import org.apache.commons.configuration.Configuration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.config.DataSourceFactory;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 */
public abstract class BaseUnitilsTask extends Task {

    /* Property keys of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property keys of the database schema name */
    private static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /* The pooled datasource instance */
    protected DataSource dataSource;

    /* The name of the database schema */
    protected String schemaName;


    public final void execute() throws BuildException {

        //todo move implementation to module?
        Configuration configuration = Unitils.getInstance().getConfiguration();
        DataSourceFactory dataSourceFactory = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init(configuration);
        dataSource = dataSourceFactory.createDataSource();
        schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME);
        doExecute();
    }

    protected abstract void doExecute() throws BuildException;

}
