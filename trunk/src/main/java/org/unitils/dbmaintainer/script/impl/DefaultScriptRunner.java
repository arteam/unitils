/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbmaintainer.script.impl;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.Reader;

import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptParser;
import org.unitils.dbmaintainer.script.ScriptRunner;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
import org.unitils.util.PropertyUtils;

/**
 * Default implementation of a script runner.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultScriptRunner extends BaseDatabaseAccessor implements ScriptRunner {

    /**
     * Executes the given script.
     * <p/>
     * All statements should be separated with a semicolon (;). The last statement will be
     * added even if it does not end with a semicolon.
     *
     * @param scriptContentHandle The script as a string, not null
     */
    public void execute(Script script) {

        Reader scriptContentReader = null;
        try {
            // get content stream
            scriptContentReader = script.getScriptContentHandle().openScriptContentReader();

            // create a parser
            ScriptParser scriptParser = createScriptParser();
            scriptParser.init(configuration, scriptContentReader);

            // Define the target database on which to execute the script
            DbSupport targetDbSupport;
            if (script.getTargetDatabaseName() == null) {
            	targetDbSupport = defaultDbSupport;
            } else {
            	targetDbSupport = dbNameDbSupportMap.get(script.getTargetDatabaseName());
            	if (targetDbSupport == null) {
            		throw new UnitilsException("Error executing script " + script.getFileName() + 
            				". No database initialized with the name " + script.getTargetDatabaseName());
            	}
            }
            
            // parse and execute the statements
            String statement;
            while ((statement = scriptParser.getNextStatement()) != null) {
                sqlHandler.executeUpdate(statement, targetDbSupport.getDataSource());
            }
        } finally {
            closeQuietly(scriptContentReader);
        }
    }


    /**
     * Creates a script parser.
     *
     * @return The parser, not null
     */
    protected ScriptParser createScriptParser() {
        String databaseDialect = PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration);
        return getInstanceOf(ScriptParser.class, configuration, databaseDialect);
    }
}
