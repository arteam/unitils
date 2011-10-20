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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.conversion.PropertiesConversionStrategy;
import org.unitils.io.conversion.StringConversionStrategy;
import org.unitils.io.reader.FileReadingStrategy;
import org.unitils.util.ReflectionUtils;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
public class FileContentTestListenerTest {
	FileContentTestListener listener;

	@Before
	public void setUp() {
		listener = new FileContentTestListener();

		HashMap<Object, ConversionStrategy<?>> conversions = new HashMap<Object, ConversionStrategy<?>>();
		conversions.put(Properties.class, new PropertiesConversionStrategy());
		conversions.put(String.class, new StringConversionStrategy());

		listener.setConversionStrategiesMap(conversions);

		FileReadingStrategy readingStrategy = new FileReadingStrategy();
		listener.setDefaultReadingStrategy(readingStrategy);

	}

	@Test
	public void testDefaultPropertyLoad() {
		DefaultTestStub testObject = new DefaultTestStub();

		Field field = ReflectionUtils
				.getFieldsOfType(DefaultTestStub.class, Properties.class, false)
				.iterator().next();

		listener.handleField(testObject, field);

		Properties result = testObject.defaultProperties;
		Assert.assertNotNull(result);
		Assert.assertEquals("text file",
				result.get("FileContentTestListenerTest"));

	}

	@Test
	public void testDefaultStringLoad() {
		DefaultTestStub testObject = new DefaultTestStub();

		Field field = ReflectionUtils
				.getFieldsOfType(DefaultTestStub.class, String.class, false)
				.iterator().next();

		listener.handleField(testObject, field);

		String result = testObject.defaultString;
		Assert.assertEquals("The FileContentTestLisener txt test file", result);

	}

	@Test
	public void testHardCodedPropertyLoad() {
		HardCodeTestStub testObject = new HardCodeTestStub();

		Field field = ReflectionUtils
				.getFieldsOfType(HardCodeTestStub.class, Properties.class,
						false).iterator().next();

		listener.handleField(testObject, field);

		Properties result = testObject.defaultProperties;
		Assert.assertNotNull(result);
		Assert.assertEquals("pub file",
				result.get("FileContentTestListenerTest"));

	}

	@Test
	public void testHardCodedStringLoad() {
		HardCodeTestStub testObject = new HardCodeTestStub();

		Field field = ReflectionUtils
				.getFieldsOfType(HardCodeTestStub.class, String.class, false)
				.iterator().next();

		listener.handleField(testObject, field);

		String result = testObject.defaultString;
		Assert.assertEquals("FileContentTestListenerTest=pub file", result);

	}

	@Test
	public void testDetermineConversionStrategy() throws Exception {

		Field field = ReflectionUtils
				.getFieldsOfType(HardCodedDifferentConversionStrategyStub.class, Object.class, false)
				.iterator().next();
		ConversionStrategy<?> result = listener.determineConversionStrategy(field);
		Assert.assertTrue(result instanceof DummyConversionStrategy);
		
	}

	private class DefaultTestStub {
		@FileContent
		Properties defaultProperties;
		@FileContent
		String defaultString;

	}

	private class HardCodeTestStub {
		@FileContent(location = "org/unitils/io/hardcodefile.pub")
		Properties defaultProperties;
		@FileContent(location = "org/unitils/io/hardcodefile.pub")
		String defaultString;

	}

	public class HardCodedDifferentConversionStrategyStub{
		
		@FileContent(conversionStrategy=DummyConversionStrategy.class)
		Object justSomeObject;
	}
	
}
