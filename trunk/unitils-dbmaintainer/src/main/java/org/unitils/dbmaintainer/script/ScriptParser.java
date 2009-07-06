/*
 * Copyright 2008,  Unitils.org
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

import java.io.Reader;
import java.util.Properties;

/**
 * An interface for a parser that can parse statements out of a script.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface ScriptParser {


    /**
     * Initializes the parser with the given configuration settings.
     *
     * @param configuration The config, not null
     * @param scriptReader  The script stream, not null
     */
    void init(Properties configuration, Reader scriptReader);


    /**
     * Gets the next statement out of the given script stream.
     *
     * @return the statements, null if no more statements
     */
    String getNextStatement();

}
