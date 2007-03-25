/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.clean;

import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.DBMaintainer;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.util.PropertyUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DbClearer test for an PostgreSql database
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PostgreSqlDBClearerTest extends DBClearerTest {


    /**
     * Creates a trigger for the test.
     *
     * @param tableName   The table for the trigger, not null
     * @param triggerName The name of the trigger, not null
     */
    protected void createTestTrigger(String tableName, String triggerName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            try {
                st.execute("create language plpgsql");
            } catch (Exception e) {
                // ignore language already exists
            }
            st.execute("create or replace function test() returns trigger as $$ declare begin end; $$ language plpgsql");
            st.execute("create trigger " + triggerName + " before insert on " + tableName + " FOR EACH ROW EXECUTE PROCEDURE test()");

        } finally {
            closeQuietly(conn, st, null);
        }
    }


    /**
     * Verifies wether the postgresql dialect is activated
     *
     * @return True if the db2 postgresql is activated, false otherwise
     */
    protected boolean isTestedDialectActivated() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        return "postgresql".equals(PropertyUtils.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT, configuration));
    }
}
