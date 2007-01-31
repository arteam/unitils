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
package org.unitils.dbmaintainer.script;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of {@link ScriptRunner} that runs an SQL script. All statements are passed on to
 * a {@link StatementHandler}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLScriptRunner extends BaseScriptRunner implements ScriptRunner {

    protected List<String> parseStatements(String script) {
        return dbSupport.parseStatements(script);
    }

}
