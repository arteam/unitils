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

import static org.apache.commons.dbutils.DbUtils.closeQuietly;
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
public class HsqldbDBClearerTest extends DBClearerTest {


    //todo javadoc
    protected void createTestTrigger(String tableName, String triggerName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create trigger " + triggerName + " before insert on " + tableName + " call "
                    + "\"org.unitils.dbmaintainer.clear.HsqldbTestTrigger\"");
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
        return "hsqldb".equals(getString(PROPKEY_DATABASE_DIALECT, configuration));
    }
}
