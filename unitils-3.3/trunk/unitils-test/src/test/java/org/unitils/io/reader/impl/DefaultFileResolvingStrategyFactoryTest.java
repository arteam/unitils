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

import java.util.Properties;

import static org.junit.Assert.*;
import static org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory.PATH_PREFIX_PROPERTY;
import static org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory.PREFIX_WITH_PACKAGE_NAME_PROPERTY;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class DefaultFileResolvingStrategyFactoryTest {

    /* Tested object */
    private DefaultFileResolvingStrategyFactory defaultFileResolvingStrategyFactory = new DefaultFileResolvingStrategyFactory();

    private Properties properties;


    @Before
    public void initialize() {
        properties = new Properties();
        properties.setProperty(PATH_PREFIX_PROPERTY, "prefix");
        properties.setProperty(PREFIX_WITH_PACKAGE_NAME_PROPERTY, "true");
    }


    @Test
    public void defaultSetup() {
        DefaultFileResolvingStrategy fileResolvingStrategy = (DefaultFileResolvingStrategy) defaultFileResolvingStrategyFactory.createFileResolvingStrategy(properties);

        assertEquals("prefix", fileResolvingStrategy.fileResolver.getPathPrefix());
        assertTrue(fileResolvingStrategy.fileResolver.isPrefixWithPackageName());
    }

    @Test
    public void emptyPathPrefix() {
        properties.setProperty(PATH_PREFIX_PROPERTY, "");

        DefaultFileResolvingStrategy fileResolvingStrategy = (DefaultFileResolvingStrategy) defaultFileResolvingStrategyFactory.createFileResolvingStrategy(properties);
        assertNull(fileResolvingStrategy.fileResolver.getPathPrefix());
    }

    @Test
    public void noPathPrefix() {
        properties.remove(PATH_PREFIX_PROPERTY);

        DefaultFileResolvingStrategy fileResolvingStrategy = (DefaultFileResolvingStrategy) defaultFileResolvingStrategyFactory.createFileResolvingStrategy(properties);
        assertNull(fileResolvingStrategy.fileResolver.getPathPrefix());
    }

    @Test(expected = UnitilsException.class)
    public void noPrefixWithPackageName() {
        properties.remove(PREFIX_WITH_PACKAGE_NAME_PROPERTY);
        defaultFileResolvingStrategyFactory.createFileResolvingStrategy(properties);
    }

    @Test(expected = UnitilsException.class)
    public void emptyPrefixWithPackageName() {
        properties.setProperty(PREFIX_WITH_PACKAGE_NAME_PROPERTY, "");
        defaultFileResolvingStrategyFactory.createFileResolvingStrategy(properties);
    }

    @Test(expected = UnitilsException.class)
    public void invalidPrefixWithPackageName() {
        properties.setProperty(PREFIX_WITH_PACKAGE_NAME_PROPERTY, "xxx");
        defaultFileResolvingStrategyFactory.createFileResolvingStrategy(properties);
    }
}
