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
package org.unitils.dbmaintainer.ant;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.util.ReflectionUtils;

/**
 * Ant task that drops all database tables in the current database. Invokes the implementation of {@link DBClearer}
 * that is configured in the Unitils configuration.
 */
public class ClearDatabaseTask extends BaseUnitilsTask {

    /* Logger for this class */
    private static final Logger logger = Logger.getLogger(ClearDatabaseTask.class);

    /* Property key of the implementation class of the {@link DBClearer} */
    public static final String PROPKEY_DBCLEARER_START = "dbMaintainer.dbClearer.className";

    /* Property key of the SQL dialect of the underlying DBMS implementation */
    private static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /**
     * Clears the database, using the implementation of <code>DBClearer</code> that is configured in the Unitils
     * configuration.
     *
     * @throws BuildException
     */
    public void doExecute() throws BuildException {
        try {
            DBClearer dbClearer = createDBClearer();
            dbClearer.clearDatabase();
        } catch (StatementHandlerException e) {
            logger.error(e);
            throw new BuildException("Error while clearing database", e);
        }
    }

    /**
     * @return the implementation of <code>DBClearer</code> that is configured in the Unitils
     *         configuration.
     */
    private DBClearer createDBClearer() {
        Configuration configuration = Unitils.getInstance().getConfiguration();

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        DBClearer dbClearer = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DBCLEARER_START + configuration.getString(PROPKEY_DATABASE_DIALECT)));
        dbClearer.init(configuration, dataSource, statementHandler);
        return dbClearer;
    }
}
