/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.core.UnitilsException;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.filecontent.FileContentReader;
import org.unitils.io.reader.ReadingStrategy;

import java.io.InputStream;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.unitils.core.util.IOUtils.closeQuietly;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultFileContentReader implements FileContentReader {

    protected ReadingStrategy readingStrategy;
    protected List<ConversionStrategy<?>> conversionStrategies;
    protected String defaultEncoding;


    public DefaultFileContentReader(ReadingStrategy readingStrategy, List<ConversionStrategy<?>> conversionStrategies, String defaultEncoding) {
        this.readingStrategy = readingStrategy;
        this.conversionStrategies = conversionStrategies;
        this.defaultEncoding = defaultEncoding;
    }


    @SuppressWarnings({"unchecked"})
    public <T> T readFileContent(String fileName, Class<T> targetType, String encoding, Class<?> testClass) {
        ConversionStrategy<?> conversionStrategy = determineConversionStrategy(targetType);
        if (isBlank(encoding)) {
            encoding = defaultEncoding;
        }
        InputStream inputStream = null;
        try {

            if (isBlank(fileName)) {
                inputStream = readingStrategy.getDefaultInputStream(conversionStrategy.getDefaultFileExtension(), testClass);
            } else {
                inputStream = readingStrategy.getInputStream(fileName, testClass);
            }
            return (T) conversionStrategy.convertContent(inputStream, encoding);

        } catch (Exception e) {
            String name = isBlank(fileName) ? "default file name" : "file with name '" + fileName + "'";
            throw new UnitilsException("Unable to read file content for " + name + " and target type " + targetType.getSimpleName(), e);
        } finally {
            closeQuietly(inputStream);
        }
    }


    protected ConversionStrategy<?> determineConversionStrategy(Class<?> targetType) {
        for (ConversionStrategy<?> conversionStrategy : conversionStrategies) {
            if (conversionStrategy.getTargetType().isAssignableFrom(targetType)) {
                return conversionStrategy;
            }
        }
        throw new UnitilsException("Unable to determine conversion strategy for target type " + targetType);
    }

}
