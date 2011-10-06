/*
 * Copyright 2008,  Unitils.org
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

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.reader.ReadingStrategy;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class FileContentTestListener extends TestListener {

	private ReadingStrategy defaultReadingStrategy;

	private HashMap<Object, ConversionStrategy<?>> conversionStrategiesMap;

	@Override
	public void beforeTestSetUp(Object testObject, Method testMethod) {
		executeActions(testObject, testMethod);
	}

	/**
	 * @param testObject
	 * @param testMethod
	 */
	public void executeActions(Object testObject, Method testMethod) {

		Set<Field> fieldsAnnotatedWith = AnnotationUtils.getFieldsAnnotatedWith(testObject.getClass(), FileContent.class);

		for (Field field : fieldsAnnotatedWith) {
			handleField(testObject, field);
		}

	}

	protected void handleField(Object testObject, Field field) {
		ReadingStrategy readingStrategy = determineReadingStrategy(field);
		ConversionStrategy<?> conversionStrategy = determineConversionStrategy(field);
		String encoding = determineEncoding(field);

		try {
			InputStream inputStream = readingStrategy.handleFile(field, testObject, conversionStrategy.getDefaultPostFix());

			Object result = conversionStrategy.readContent(inputStream, encoding);

			ReflectionUtils.setFieldValue(testObject, field, result);
		} catch (Exception e) {
			throw new UnitilsException("Error reading file for  " + field.getName(), e);
		}
	}

	private String determineEncoding(Field field) {
		// TODO encoding should be found according to the known hierarchy.
		return "UTF-8";
	}

	private ConversionStrategy<?> determineConversionStrategy(Field field) {

		ConversionStrategy<?> strategy = conversionStrategiesMap.get(field.getType());
		return strategy;
	}

	private ReadingStrategy determineReadingStrategy(Field field) {
		return defaultReadingStrategy;
	}

	public void setDefaultReadingStrategy(ReadingStrategy defaultReadingStrategy) {
		this.defaultReadingStrategy = defaultReadingStrategy;
	}

	public void setConversionStrategiesMap(HashMap<Object, ConversionStrategy<?>> conversionStrategiesMap) {
		this.conversionStrategiesMap = conversionStrategiesMap;
	}

}
