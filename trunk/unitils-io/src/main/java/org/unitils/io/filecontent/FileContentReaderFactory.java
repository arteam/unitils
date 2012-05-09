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

package org.unitils.io.filecontent;

import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.filecontent.impl.DefaultFileContentReader;
import org.unitils.io.reader.ReadingStrategy;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.config.Configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class FileContentReaderFactory implements Factory<FileContentReader> {

    public static final String DEFAULT_CONVERSION_STRATEGY_KEY = "IOModule.conversion.default";

    public static final String CUSTOM_CONVERSION_STRATEGY_KEY = "IOModule.conversion.custom";

    public static final String DEFAULT_FILE_ENCODING = "IOModule.encoding.default";


    protected List<ConversionStrategy<?>> conversionStrategies;

    protected String defaultEncoding;

    protected ReadingStrategy readingStrategy;

    protected Configuration config;

    public FileContentReaderFactory(Configuration config, @Property(DEFAULT_FILE_ENCODING) String defaultEncoding, ReadingStrategy readingStrategy) {
        this.config = config;
        this.defaultEncoding = defaultEncoding;
        this.readingStrategy = readingStrategy;
    }

    public FileContentReader create() {
        conversionStrategies = createConversionStrategies(config);


        return new DefaultFileContentReader(readingStrategy, conversionStrategies, defaultEncoding);

    }


    protected List<ConversionStrategy<?>> createConversionStrategies(Configuration configuration) {
        List<ConversionStrategy<?>> conversionStrategies = new LinkedList<ConversionStrategy<?>>();
        conversionStrategies.addAll(createConversionStrategies(configuration.getOptionalStringList(CUSTOM_CONVERSION_STRATEGY_KEY)));
        conversionStrategies.addAll(createConversionStrategies(configuration.getStringList(DEFAULT_CONVERSION_STRATEGY_KEY)));
        return conversionStrategies;
    }

    protected List<ConversionStrategy<?>> createConversionStrategies(List<String> conversionStrategyClassNames) {
        if (conversionStrategyClassNames == null || conversionStrategyClassNames.isEmpty()) {
            return new LinkedList<ConversionStrategy<?>>();
        }
        List<ConversionStrategy<?>> conversionStrategies = new ArrayList<ConversionStrategy<?>>(conversionStrategyClassNames.size());

        for (String conversionStrategyClassName : conversionStrategyClassNames) {
            ConversionStrategy<?> conversionStrategy = createInstanceOfType(conversionStrategyClassName, false);
            conversionStrategies.add(conversionStrategy);
        }
        return conversionStrategies;
    }
}
