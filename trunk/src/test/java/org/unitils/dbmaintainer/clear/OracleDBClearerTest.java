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
package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DbClearer test for an Oracle database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OracleDBClearerTest extends DBClearerTest {


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


    protected boolean isTestedDialectActivated() {
        Configuration config = new ConfigurationLoader().loadConfiguration();
        return "oracle".equals(config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));
    }
}
