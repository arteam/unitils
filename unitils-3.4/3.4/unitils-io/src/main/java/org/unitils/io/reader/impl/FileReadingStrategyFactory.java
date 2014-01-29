/*
 * Copyright 2011,  Unitils.org
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

package org.unitils.io.reader.impl;

import org.unitils.io.reader.FileResolvingStrategy;
import org.unitils.io.reader.FileResolvingStrategyFactory;
import org.unitils.io.reader.ReadingStrategy;
import org.unitils.io.reader.ReadingStrategyFactory;

import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class FileReadingStrategyFactory implements ReadingStrategyFactory {


    public ReadingStrategy createReadingStrategy(Properties configuration) {
        FileResolvingStrategyFactory fileResolvingStrategyFactory = getInstanceOf(FileResolvingStrategyFactory.class, configuration);
        FileResolvingStrategy fileResolvingStrategy = fileResolvingStrategyFactory.createFileResolvingStrategy(configuration);
        return new FileReadingStrategy(fileResolvingStrategy);
    }
}
