package org.unitils.database;

import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.util.BaseDataSourceDecorator;

/**
 * DataSource that wraps the DataSource that is provided by the {@link DatabaseModule}, but that doesn't need any context
 * information to be initialized. It will instead lookup its context information itself when the empty constructor is called.
 * <p/>
 * This class can be useful for configuring other code to make use of the <code>DataSource</code> provided by Unitils
 * with minimal effort. For example when using <a href="www.springframework.org>Spring</a>, you could specify a bean
 * definition named dataSource that connects to the test database as follows:<br/>
 * <code><bean id="dataSource" class="org.unitils.database.UnitilsDataSource"></code>
 */
public class UnitilsDataSource extends BaseDataSourceDecorator {

    public UnitilsDataSource() {
        super(getDatabaseModule().getDataSource());
    }

    private static DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }

}
