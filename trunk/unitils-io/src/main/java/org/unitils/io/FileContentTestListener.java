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

import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.reader.ReadingStrategy;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.setFieldValue;

/**
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
public class FileContentTestListener extends TestListener {

    private ReadingStrategy defaultReadingStrategy;

    private List<ConversionStrategy<?>> conversionStrategiesList;

    private String defaultEncoding;

    @Override
    public void beforeTestSetUp(Object testObject, Method testMethod) {
        executeActions(testObject, testMethod);
    }

    public void executeActions(Object testObject, Method testMethod) {
        Set<Field> fieldsAnnotatedWith = getFieldsAnnotatedWith(testObject.getClass(), FileContent.class);

        for (Field field : fieldsAnnotatedWith) {
            handleField(testObject, field);
        }
    }

    protected void handleField(Object testObject, Field field) {
        ReadingStrategy readingStrategy = determineReadingStrategy(field);
        ConversionStrategy<?> conversionStrategy = determineConversionStrategy(field);
        String encoding = determineEncoding(field);
        try {
            InputStream inputStream = readingStrategy.handleFile(field, testObject, conversionStrategy.getFileExtension());
            Object result = conversionStrategy.readContent(inputStream, encoding);
            setFieldValue(testObject, field, result);

        } catch (Exception e) {
            throw new UnitilsException("Error reading file for  " + field.getName(), e);
        }
    }

    private String determineEncoding(Field field) {
        FileContent annotation = field.getAnnotation(FileContent.class);
        if (annotation.encoding() != null && !annotation.encoding().isEmpty()) {
            return annotation.encoding();
        }

        return defaultEncoding;
    }

    protected ConversionStrategy<?> determineConversionStrategy(Field field) {
        FileContent annotation = field.getAnnotation(FileContent.class);
        if (!annotation.conversionStrategy().isInterface()) {
            return (ConversionStrategy<?>) createInstanceOfType(annotation.conversionStrategy(), true);
        }
        for (ConversionStrategy tmp : conversionStrategiesList) {
            if (tmp.getDefaultEndClass().equals(field.getType())) {
                return tmp;
            }
        }
        throw new UnitilsException("Unable to determinate conversion strategy for field " + field.getType());
    }

    private ReadingStrategy determineReadingStrategy(Field field) {
        return defaultReadingStrategy;
    }

    public void setDefaultReadingStrategy(ReadingStrategy defaultReadingStrategy) {
        this.defaultReadingStrategy = defaultReadingStrategy;
    }

    public void setConversionStrategiesList(List<ConversionStrategy<?>> conversionStrategiesList) {
        this.conversionStrategiesList = conversionStrategiesList;
    }

    protected List<ConversionStrategy<?>> getConversionStrategiesList() {
        return conversionStrategiesList;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }
}
