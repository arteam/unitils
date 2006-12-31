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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;

/**
 * Ant task that drops all database tables in the current database. Invokes the implementation of {@link DBClearer}
 * that is configured in the Unitils configuration.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ClearDatabaseTask extends BaseUnitilsTask {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ClearDatabaseTask.class);

    /* Property key of the implementation class of the {@link DBClearer} */
    public static final String PROPKEY_DBCLEARER_START = "dbMaintainer.dbClearer.className";


    /**
     * Clears the database, using the implementation of <code>DBClearer</code> that is configured
     * in the Unitils configuration.
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
     * @return the implementation of <code>DBClearer</code> that is configured in the Unitils configuration.
     */
    private DBClearer createDBClearer() {
        Configuration configuration = Unitils.getInstance().getConfiguration();

        StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration, dataSource);
        return DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource, statementHandler);
    }
}
