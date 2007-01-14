package org.unitils.database;

import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.util.BaseDataSourceDecorator;

import javax.sql.DataSource;

/**
 * DataSource that wraps the DataSource that is provided by the {@link DatabaseModule}, but that doesn't need any context
 * information to be initialized. It will instead lookup its context information itself when the empty constructor is called.
 * <p/>
 * This class can be useful for configuring other code to make use of the <code>DataSource</code> provided by Unitils
 * with minimal effort. For example when using Spring, you could specify a bean definition named dataSource that
 * connects to the test database as follows:
 * <pre><code>
 *     &lt;bean id="dataSource" class="org.unitils.database.UnitilsDataSource"&gt;
 * </code></pre>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsDataSource extends BaseDataSourceDecorator {


    /**
     * Creates a datasource.
     */
    public UnitilsDataSource() {
        super(getUnitilsDataSource());
    }


    /**
     * Gets the Unitils datasource from the {@link DatabaseModule}.
     *
     * @return The DataSource, not null
     */
    private static DataSource getUnitilsDataSource() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        return databaseModule.getDataSource();
    }

}
