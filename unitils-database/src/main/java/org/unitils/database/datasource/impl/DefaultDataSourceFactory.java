/*
 * Copyright Unitils.org
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
package org.unitils.database.datasource.impl;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.DatabaseInfo;
import org.unitils.database.DatabaseModule;
import org.unitils.database.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

/**
 * A data source factory that creates a commons DBCP BasicDataSource.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSourceFactory implements DataSourceFactory {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseModule.class);


    public void init(Properties configuration) {
    }

    /**
     * Returns an instance of {@link BasicDataSource}.
     *
     * @return An instance of <code>BasicDataSource</code>
     */
    public DataSource createDataSource(DatabaseInfo databaseInfo) {
        databaseInfo.validateFull();
        String driverClassName = databaseInfo.getDriverClassName();
        String url = databaseInfo.getUrl();
        String userName = databaseInfo.getUserName();
        String password = databaseInfo.getPassword();

        DataSource dataSource = createDataSource(driverClassName, url, userName, password);
        try {
            testConnection(dataSource);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to database. Driver: " + driverClassName + ", url: " + url + ", user: " + userName + ", password: <not shown>", e);
        }
        logger.info("Created data source. Driver: " + driverClassName + ", url: " + url + ", user: " + userName + ", password: <not shown>");
        return dataSource;
    }


    protected BasicDataSource createDataSource(String driverClassName, String url, String userName, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
    }

    protected void testConnection(DataSource dataSource) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();

        } finally {
            closeQuietly(connection, null, null);
        }
    }

}
