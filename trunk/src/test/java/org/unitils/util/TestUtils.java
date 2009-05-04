package org.unitils.util;

import org.dbmaintain.dbsupport.DbSupport;
import org.dbmaintain.dbsupport.DbMaintainDataSource;
import org.dbmaintain.dbsupport.impl.HsqldbDbSupport;
import org.dbmaintain.dbsupport.impl.DefaultSQLHandler;
import static org.dbmaintain.util.CollectionUtils.asSet;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 */
public class TestUtils {

    /**
     * Private constructor to prevent instantiation
     */
    private TestUtils() {
    }

    public static DbSupport getDbSupport() {
        return getDbSupport("PUBLIC");
    }


    public static DbSupport getDbSupport(String... schemaNames) {
        DataSource dataSource = getDataSource();
        return new HsqldbDbSupport(null, dataSource, schemaNames[0], asSet(schemaNames), new DefaultSQLHandler(), null, null);
    }


    protected static DataSource getDataSource() {
        return DbMaintainDataSource.createDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils", "sa", "");
    }
}
