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
 * 
 * 
 * Example:
 * 
 * <pre>
 * &#064;FileContent(location = &quot;be/smals/file.txt&quot;)
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

	private FileContentTestListener testListener;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileContentTestListener.class);

	public TestListener getTestListener() {
		return testListener;
	}

	public void init(Properties properties) {
		testListener = initFileContentListener(properties);

	}

	private FileContentTestListener initFileContentListener(
			Properties properties) {
		return FileContentTestListenerFactory
				.createFileContentTestListener(properties);

	}

	public void afterInit() {
		LOGGER.debug("IoModule succesfully loaded. ");
	}

	protected class FileUtilListener extends TestListener {

		private FileContentTestListener fileContentListener = new FileContentTestListener();

		@Override
		public void beforeTestSetUp(Object testObject, Method testMethod) {
			fileContentListener.beforeTestSetUp(testObject, testMethod);
		}

	}

}
