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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.unitils.util.ReflectionUtils.createInstanceOfType;

public class FileContentTestListenerFactory {

    private static final String CONVERSION_STRATEGY_KEY = "org.unitils.io.conversion";
    private static final String READER_STRATEGY_KEY = "org.unitils.io.reader";

    public static FileContentTestListener createFileContentTestListener(Properties properties) {
        FileContentTestListener result = new FileContentTestListener();
        HashMap<Object, ConversionStrategy<?>> conversionStrategiesMap = new HashMap<Object, ConversionStrategy<?>>();

        List<ConversionStrategy<?>> strategies = resolveConversationStrategies(properties);
        for (ConversionStrategy<?> tmp : strategies) {
            conversionStrategiesMap.put(tmp.getDefaultEndClass(), tmp);
        }

        result.setConversionStrategiesMap(conversionStrategiesMap);
        result.setDefaultReadingStrategy(resolveReadingStrategy(properties));
        return result;
    }

    private static ReadingStrategy resolveReadingStrategy(Properties properties) {
        String className = properties.getProperty(READER_STRATEGY_KEY);
        return createInstanceOfType(className.trim(), false);
    }

    private static List<ConversionStrategy<?>> resolveConversationStrategies(Properties properties) {
        String conversionStrategiesString = properties.getProperty(CONVERSION_STRATEGY_KEY);

        String[] split = conversionStrategiesString.split(",");

        List<ConversionStrategy<?>> result = new ArrayList<ConversionStrategy<?>>(split.length);

        for (String className : split) {
            ConversionStrategy<?> conversionStrategy = createInstanceOfType(className.trim(), false);
            result.add(conversionStrategy);
        }
        return result;
    }
}
