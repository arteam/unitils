package org.unitils.dbmaintainer.clean;

import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.dbsupport.TestSQLUtils.dropTestTables;
import static org.unitils.core.dbsupport.TestSQLUtils.dropTestViews;
import static org.unitils.core.dbsupport.TestSQLUtils.dropTestTriggers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBClearer test for a hsqldb database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MySqlDBClearerTest extends DBClearerTest {


    /**
     * Creates a new clearer test
     */
    public MySqlDBClearerTest() {
        super("mysql");
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    protected void createTestDatabase() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table `Test_CASE_Table` (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view `Test_CASE_View` as select col1 from `Test_CASE_Table`", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
        executeUpdate("create trigger `Test_CASE_Trigger` after insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    protected void cleanupTestDatabase() throws Exception {
        dropTestTables(dbSupport, "test_table", "`Test_CASE_Table`");
        dropTestViews(dbSupport, "test_view", "`Test_CASE_View`");
        dropTestTriggers(dbSupport, "test_trigger", "`Test_CASE_Trigger`");
    }

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

}
