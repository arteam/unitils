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

package org.unitils.io;

import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.reader.ReadingStrategy;

import java.util.*;

import static org.unitils.util.ReflectionUtils.createInstanceOfType;

public class FileContentTestListenerFactory {

    protected static final String DEFAULT_CONVERSION_STRATEGY_KEY = "IoModule.conversion.default";
    protected static final String CUSTOM_CONVERSION_STRATEGY_KEY = "IoModule.conversion.custom";
    protected static final String READER_STRATEGY_KEY = "IoModule.reader.default";
    protected static final String DEFAULT_FILE_ENCODING = "IoModule.encoding.default";

    public static FileContentTestListener createFileContentTestListener(Properties properties) {
        FileContentTestListener result = new FileContentTestListener();
        HashMap<Object, ConversionStrategy<?>> conversionStrategiesMap = new HashMap<Object, ConversionStrategy<?>>();

        List<ConversionStrategy<?>> strategies = new LinkedList<ConversionStrategy<?>>();
        strategies.addAll(resolveConversationStrategies(properties, DEFAULT_CONVERSION_STRATEGY_KEY));
        strategies.addAll(resolveConversationStrategies(properties, CUSTOM_CONVERSION_STRATEGY_KEY));

        result.setConversionStrategiesList(strategies);
        result.setDefaultEncoding(properties.getProperty(DEFAULT_FILE_ENCODING));
        result.setDefaultReadingStrategy(resolveReadingStrategy(properties));
        return result;
    }

    private static ReadingStrategy resolveReadingStrategy(Properties properties) {
        String className = properties.getProperty(READER_STRATEGY_KEY);
        return createInstanceOfType(className.trim(), false);
    }


    protected static List<ConversionStrategy<?>> resolveConversationStrategies(Properties properties, String key) {
        String conversionStrategiesString = properties.getProperty(key);

        String[] split = conversionStrategiesString.split(",");

        List<ConversionStrategy<?>> result = new ArrayList<ConversionStrategy<?>>(split.length);

        for (String className : split) {
            if (className != null && !className.trim().isEmpty()) {
                ConversionStrategy<?> conversionStrategy = createInstanceOfType(className.trim(), false);
                result.add(conversionStrategy);
            }
        }
        return result;
    }
}
