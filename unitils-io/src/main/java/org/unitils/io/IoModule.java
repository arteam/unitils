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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.reader.ReadingStrategy;
import org.unitils.util.ReflectionUtils;

/**
 * Will listen for the @FileContent annotation in tests. The content of the file
 * specified in the annotation will be loaded in the property. A property
 * annotation with {@link FileContent} should always be a String
 * 
 * Example:
 * 
 * <pre>
 * &#064;FileContent(classPathLocation = &quot;be/smals/file.txt&quot;)
 * private String fileContent;
 * </pre>
 * 
 * @author Jeroen Horema
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class IoModule implements Module {

	private static final String CONVERSION_STRATEGY_KEY = "org.unitils.io.conversion";
	private static final String READER_STRATEGY_KEY = "org.unitils.io.reader";

	private FileContentTestListener testListener;

	private static final Logger LOGGER = LoggerFactory.getLogger(FileContentTestListener.class);

	public TestListener getTestListener() {
		return testListener;
	}

	public void init(Properties properties) {
		testListener = new FileContentTestListener();
		HashMap<Object, ConversionStrategy<?>> conversionStrategiesMap = new HashMap<Object, ConversionStrategy<?>>();

		List<ConversionStrategy<?>> strategies = resolveConverstionStrategies(properties);

		for (ConversionStrategy<?> tmp : strategies) {
			conversionStrategiesMap.put(tmp.getDefaultEndClass(), tmp);
		}

		testListener.setConversionStrategiesMap(conversionStrategiesMap);
		testListener.setDefaultReadingStrategy(resolveReadingStrategy(properties));

		LOGGER.debug("IoModule succesfully loaded. ");
	}

	private ReadingStrategy resolveReadingStrategy(Properties properties) {
		String className = properties.getProperty(READER_STRATEGY_KEY);
		return ReflectionUtils.createInstanceOfType(className.trim(), false);
	}

	private List<ConversionStrategy<?>> resolveConverstionStrategies(Properties properties) {
		String conversionStrategiesString = properties.getProperty(CONVERSION_STRATEGY_KEY);

		String[] split = conversionStrategiesString.split(",");

		List<ConversionStrategy<?>> result = new ArrayList<ConversionStrategy<?>>(split.length);

		for (String className : split) {
			ConversionStrategy<?> conversionStrategy = ReflectionUtils.createInstanceOfType(className.trim(), false);
			result.add(conversionStrategy);
		}

		return result;
	}

	public void afterInit() {
		// Nothing todo for now.
	}

	protected class FileUtilListener extends TestListener {

		@Override
		public void beforeTestSetUp(Object testObject, Method testMethod) {
			new FileContentTestListener().beforeTestSetUp(testObject, testMethod);
		}

	}

}
