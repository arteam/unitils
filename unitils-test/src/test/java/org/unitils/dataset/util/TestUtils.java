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
package org.unitils.dataset.util;

import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupports;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestUtils {

    public static DatabaseMetaData createDatabaseMetaData(Properties configuration, DataSource dataSource) {
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);

        DbSupport defaultDbSupport = getDefaultDbSupport(configuration, sqlHandler);
        List<DbSupport> dbSupports = getDbSupports(configuration, sqlHandler);
        return new DatabaseMetaData(defaultDbSupport, dbSupports, new SqlTypeHandlerRepository());
    }
}
