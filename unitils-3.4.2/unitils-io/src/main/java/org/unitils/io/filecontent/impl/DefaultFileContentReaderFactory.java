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

import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.filecontent.FileContentReader;
import org.unitils.io.filecontent.FileContentReaderFactory;
import org.unitils.io.reader.ReadingStrategy;
import org.unitils.io.reader.ReadingStrategyFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.util.PropertyUtils.getString;
import static org.unitils.util.PropertyUtils.getStringList;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class DefaultFileContentReaderFactory implements FileContentReaderFactory {

    public static final String DEFAULT_CONVERSION_STRATEGY_KEY = "IOModule.conversion.default";
    public static final String CUSTOM_CONVERSION_STRATEGY_KEY = "IOModule.conversion.custom";
    public static final String DEFAULT_FILE_ENCODING = "IOModule.encoding.default";


    public FileContentReader createFileContentReader(Properties configuration) {
        ReadingStrategy readingStrategy = createReadingStrategy(configuration);
        List<ConversionStrategy<?>> conversionStrategies = createConversionStrategies(configuration);
        String defaultEncoding = getString(DEFAULT_FILE_ENCODING, configuration);

        return new DefaultFileContentReader(readingStrategy, conversionStrategies, defaultEncoding);
    }


    protected ReadingStrategy createReadingStrategy(Properties configuration) {
        ReadingStrategyFactory readingStrategyFactory = getInstanceOf(ReadingStrategyFactory.class, configuration);
        return readingStrategyFactory.createReadingStrategy(configuration);
    }

    protected List<ConversionStrategy<?>> createConversionStrategies(Properties configuration) {
        List<ConversionStrategy<?>> conversionStrategies = new LinkedList<ConversionStrategy<?>>();
        conversionStrategies.addAll(createConversionStrategies(configuration, CUSTOM_CONVERSION_STRATEGY_KEY));
        conversionStrategies.addAll(createConversionStrategies(configuration, DEFAULT_CONVERSION_STRATEGY_KEY));
        return conversionStrategies;
    }

    protected List<ConversionStrategy<?>> createConversionStrategies(Properties configuration, String propertyName) {
        List<String> conversionStrategyClassNames = getStringList(propertyName, configuration);
        List<ConversionStrategy<?>> conversionStrategies = new ArrayList<ConversionStrategy<?>>(conversionStrategyClassNames.size());

        for (String conversionStrategyClassName : conversionStrategyClassNames) {
            ConversionStrategy<?> conversionStrategy = createInstanceOfType(conversionStrategyClassName, false);
            conversionStrategies.add(conversionStrategy);
        }
        return conversionStrategies;
    }
}
