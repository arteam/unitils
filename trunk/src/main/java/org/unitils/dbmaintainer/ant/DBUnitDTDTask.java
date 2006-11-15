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
import org.apache.tools.ant.BuildException;
import org.unitils.core.Unitils;
import org.unitils.dbmaintainer.dtd.DtdGenerator;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;

/**
 * Ant task that generates a DTD from the unit test database. Invokes the implementation of {@link DtdGenerator}
 * that is configured in the Unitils configuration.
 */
public class DBUnitDTDTask extends BaseUnitilsTask {

    /* Property key of the impelementation class of {@link DtdGenerator} */
    private static final String PROPKEY_DTDGENERATOR_CLASSNAME = "dbMaintainer.database.dtdGenerator.className";

    /**
     * Generates a DTD from the unit test database
     *
     * @throws BuildException
     */
    public void doExecute() throws BuildException {

        Configuration configuration = Unitils.getInstance().getConfiguration();
        StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration,
                dataSource);
        DtdGenerator dtdGenerator = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DtdGenerator.class,
                configuration, dataSource, statementHandler);

        dtdGenerator.generateDtd();
    }

}
