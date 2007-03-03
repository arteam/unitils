package org.unitils.dbmaintainer.clean;

import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.dbmaintainer.DBMaintainer.PROPKEY_DATABASE_DIALECT;
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
public class MySqlDBClearerTest extends DBClearerTest {


    //todo javadoc
    protected void createTestTrigger(String tableName, String triggerName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create trigger " + triggerName + " before insert on " + tableName + " FOR EACH ROW begin end");

        } finally {
            closeQuietly(conn, st, null);
        }
    }


    /**
     * Verifies wether the hsqldb dialect is activated
     *
     * @return True if the hsqldb dialect is activated, false otherwise
     */
    protected boolean isTestedDialectActivated() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        return "mysql".equals(getString(PROPKEY_DATABASE_DIALECT, configuration));
    }
}
