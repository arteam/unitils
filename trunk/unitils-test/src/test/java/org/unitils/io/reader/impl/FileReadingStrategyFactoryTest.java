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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.io.reader.FileResolvingStrategyFactory;

import java.util.Properties;

import static org.junit.Assert.assertTrue;
import static org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory.PATH_PREFIX_PROPERTY;
import static org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory.PREFIX_WITH_PACKAGE_NAME_PROPERTY;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class FileReadingStrategyFactoryTest {

    /* Tested object */
    private FileReadingStrategyFactory fileReadingStrategyFactory = new FileReadingStrategyFactory();

    private Properties properties;


    @Before
    public void initialize() {
        properties = new Properties();
        properties.setProperty(FileResolvingStrategyFactory.class.getName() + ".implClassName", DefaultFileResolvingStrategyFactory.class.getName());
        properties.setProperty(PREFIX_WITH_PACKAGE_NAME_PROPERTY, "true");
        properties.setProperty(PATH_PREFIX_PROPERTY, "");
        properties.setProperty(PREFIX_WITH_PACKAGE_NAME_PROPERTY, "true");
    }


    @Test
    public void defaultSetup() {
        FileReadingStrategy fileReadingStrategy = (FileReadingStrategy) fileReadingStrategyFactory.createReadingStrategy(properties);
        assertTrue(fileReadingStrategy.fileResolvingStrategy instanceof DefaultFileResolvingStrategy);
    }

    @Test(expected = UnitilsException.class)
    public void noFileResolvingStrategyFactory() {
        properties.remove(FileResolvingStrategyFactory.class.getName() + ".implClassName");
        fileReadingStrategyFactory.createReadingStrategy(properties);
    }

    @Test(expected = UnitilsException.class)
    public void emptyReadingStrategyFactory() {
        properties.setProperty(FileResolvingStrategyFactory.class.getName() + ".implClassName", "");
        fileReadingStrategyFactory.createReadingStrategy(properties);
    }

    @Test(expected = UnitilsException.class)
    public void invalidReadingStrategyFactory() {
        properties.setProperty(FileResolvingStrategyFactory.class.getName() + ".implClassName", "xxx");
        fileReadingStrategyFactory.createReadingStrategy(properties);
    }

}
