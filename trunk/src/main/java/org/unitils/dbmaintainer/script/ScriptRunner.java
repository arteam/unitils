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
package org.unitils.dbmaintainer.script;

import org.unitils.dbmaintainer.util.DatabaseAccessing;


/**
 * Runs a given database script.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface ScriptRunner extends DatabaseAccessing {


    /**
     * Executes the given script
     *
     * @param script A handle that provides access to the content of the script, not null
     */
    void execute(ScriptContentHandle scriptContentHandle);

}
