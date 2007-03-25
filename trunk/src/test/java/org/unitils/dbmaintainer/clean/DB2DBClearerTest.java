package org.unitils.dbmaintainer.clean;

import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.DBMaintainer;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.unitils.util.PropertyUtils.getString;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DBClearer test for a hsqldb database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DB2DBClearerTest extends DBClearerTest {


    protected void createTestTrigger(String tableName, String triggerName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create trigger " + triggerName + " before insert on " + tableName + " FOR EACH ROW begin atomic end");
        } finally {
            closeQuietly(conn, st, null);
        }
    }


    /**
     * Verifies wether the db2 dialect is activated
     *
     * @return True if the db2 dialect is activated, false otherwise
     */
    protected boolean isTestedDialectActivated() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        return "db2".equals(getString(DBMaintainer.PROPKEY_DATABASE_DIALECT, configuration));
    }
}
