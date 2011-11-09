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


package org.unitils.io.filecontent.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.conversion.impl.PropertiesConversionStrategy;
import org.unitils.io.conversion.impl.StringConversionStrategy;
import org.unitils.io.reader.FileResolvingStrategyFactory;
import org.unitils.io.reader.ReadingStrategyFactory;
import org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory;
import org.unitils.io.reader.impl.FileReadingStrategy;
import org.unitils.io.reader.impl.FileReadingStrategyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.io.filecontent.impl.DefaultFileContentReaderFactory.*;
import static org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory.PATH_PREFIX_PROPERTY;
import static org.unitils.io.reader.impl.DefaultFileResolvingStrategyFactory.PREFIX_WITH_PACKAGE_NAME_PROPERTY;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @since 3.3
 */
public class DefaultFileContentReaderFactoryTest {

    /* Tested object */
    private DefaultFileContentReaderFactory defaultFileContentReaderFactory = new DefaultFileContentReaderFactory();

    private Properties properties;


    @Before
    public void initialize() {
        properties = new Properties();
        properties.setProperty(DEFAULT_CONVERSION_STRATEGY_KEY, PropertiesConversionStrategy.class.getName() + ", " + StringConversionStrategy.class.getName());
        properties.setProperty(DEFAULT_FILE_ENCODING, "ISO-8859-1");
        properties.setProperty(ReadingStrategyFactory.class.getName() + ".implClassName", FileReadingStrategyFactory.class.getName());
        properties.setProperty(FileResolvingStrategyFactory.class.getName() + ".implClassName", DefaultFileResolvingStrategyFactory.class.getName());
        properties.setProperty(PATH_PREFIX_PROPERTY, "");
        properties.setProperty(PREFIX_WITH_PACKAGE_NAME_PROPERTY, "true");
    }


    @Test
    public void defaultSetup() {
        DefaultFileContentReader fileContentReader = (DefaultFileContentReader) defaultFileContentReaderFactory.createFileContentReader(properties);

        assertEquals("ISO-8859-1", fileContentReader.defaultEncoding);
        assertEquals(2, fileContentReader.conversionStrategies.size());
        assertTrue(fileContentReader.conversionStrategies.get(0) instanceof PropertiesConversionStrategy);
        assertTrue(fileContentReader.conversionStrategies.get(1) instanceof StringConversionStrategy);
        assertTrue(fileContentReader.readingStrategy instanceof FileReadingStrategy);
    }

    @Test
    public void customConversionStrategiesAreFirstInTheList() {
        properties.setProperty(CUSTOM_CONVERSION_STRATEGY_KEY, CustomConversionStrategy.class.getName());

        DefaultFileContentReader fileContentReader = (DefaultFileContentReader) defaultFileContentReaderFactory.createFileContentReader(properties);
        assertEquals(3, fileContentReader.conversionStrategies.size());
        assertTrue(fileContentReader.conversionStrategies.get(0) instanceof CustomConversionStrategy);
        assertTrue(fileContentReader.conversionStrategies.get(1) instanceof PropertiesConversionStrategy);
        assertTrue(fileContentReader.conversionStrategies.get(2) instanceof StringConversionStrategy);
    }

    @Test(expected = UnitilsException.class)
    public void noReadingStrategyFactory() {
        properties.remove(ReadingStrategyFactory.class.getName() + ".implClassName");
        defaultFileContentReaderFactory.createFileContentReader(properties);
    }

    @Test(expected = UnitilsException.class)
    public void emptyReadingStrategyFactory() {
        properties.setProperty(ReadingStrategyFactory.class.getName() + ".implClassName", "");
        defaultFileContentReaderFactory.createFileContentReader(properties);
    }

    @Test(expected = UnitilsException.class)
    public void invalidReadingStrategyFactory() {
        properties.setProperty(ReadingStrategyFactory.class.getName() + ".implClassName", "xxx");
        defaultFileContentReaderFactory.createFileContentReader(properties);
    }

    @Test
    public void noConversionStrategies() {
        properties.remove(DEFAULT_CONVERSION_STRATEGY_KEY);

        DefaultFileContentReader fileContentReader = (DefaultFileContentReader) defaultFileContentReaderFactory.createFileContentReader(properties);
        assertEquals(0, fileContentReader.conversionStrategies.size());
    }

    @Test
    public void emptyConversionStrategies() {
        properties.setProperty(DEFAULT_CONVERSION_STRATEGY_KEY, "");

        DefaultFileContentReader fileContentReader = (DefaultFileContentReader) defaultFileContentReaderFactory.createFileContentReader(properties);
        assertEquals(0, fileContentReader.conversionStrategies.size());
    }

    @Test
    public void onlyCustomConversionStrategies() {
        properties.setProperty(DEFAULT_CONVERSION_STRATEGY_KEY, "");
        properties.setProperty(CUSTOM_CONVERSION_STRATEGY_KEY, CustomConversionStrategy.class.getName());

        DefaultFileContentReader fileContentReader = (DefaultFileContentReader) defaultFileContentReaderFactory.createFileContentReader(properties);
        assertEquals(1, fileContentReader.conversionStrategies.size());
        assertTrue(fileContentReader.conversionStrategies.get(0) instanceof CustomConversionStrategy);
    }

    @Test(expected = UnitilsException.class)
    public void invalidConversionStrategy() {
        properties.setProperty(DEFAULT_CONVERSION_STRATEGY_KEY, "xxx");
        defaultFileContentReaderFactory.createFileContentReader(properties);
    }


    public static class CustomConversionStrategy implements ConversionStrategy<String> {

        public String convertContent(InputStream inputStream, String encoding) throws IOException {
            return null;
        }

        public String getDefaultFileExtension() {
            return null;
        }

        public Class<String> getTargetType() {
            return null;
        }
    }
}
