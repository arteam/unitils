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
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

/**
 * Ant task that updates the database to the latest version. Invokes the {@link DBMaintainer} as it is configured in the
 * Unitils configuration.
 */
public class UpdateDatabaseTask extends BaseUnitilsTask {

    /* Logger for this class */
    private static final Logger logger = Logger.getLogger(UpdateDatabaseTask.class);

    /**
     * Updates the database to the latest version. Invokes {@link DBMaintainer} as it is configured in the Unitils
     * configuration.
     *
     * @throws BuildException
     */
    public void doExecute() throws BuildException {
        Configuration configuration = Unitils.getInstance().getConfiguration();
        DBMaintainer dbMaintainer = new DBMaintainer(configuration, dataSource);
        try {
            dbMaintainer.updateDatabase();
        } catch (StatementHandlerException e) {
            logger.error(e);
            throw new BuildException("Error updateing database", e);
        }
    }

}
