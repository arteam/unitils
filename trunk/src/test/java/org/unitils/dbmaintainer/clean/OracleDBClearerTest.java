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

import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.dbmaintainer.DBMaintainer.PROPKEY_DATABASE_DIALECT;
import static org.unitils.util.PropertyUtils.getString;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * DbClearer test for an Oracle database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OracleDBClearerTest extends DBClearerTest {


    //todo javadoc
    protected void createTestTrigger(String tableName, String triggerName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create or replace trigger " + triggerName + " before insert on " + tableName + " begin " +
                    "select 1 from dual end " + triggerName);

        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }


    //todo javadoc
    protected boolean isTestedDialectActivated() {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        return "oracle".equals(getString(PROPKEY_DATABASE_DIALECT, configuration));
    }
}
