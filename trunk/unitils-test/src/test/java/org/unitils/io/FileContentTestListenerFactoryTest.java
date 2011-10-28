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

import junitx.framework.Assert;
import org.junit.Test;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.conversion.impl.PropertiesConversionStrategy;
import org.unitils.io.conversion.impl.StringConversionStrategy;
import org.unitils.io.reader.impl.FileReadingStrategy;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author Jeroen Horemans
 * @since 3.3
 */
public class FileContentTestListenerFactoryTest {


    public static final String KEY = "IoModule.FileContentTestListenerfactoryTest";

    @Test
    public void testCreateFileContentTestListener_custom_setup() {
        Properties properties = new Properties();
        properties.setProperty(KEY, "org.unitils.io.conversion.impl.PropertiesConversionStrategy,org.unitils.io.conversion.impl.StringConversionStrategy");


        List result = FileContentTestListenerFactory.resolveConversationStrategies(properties, "IoModule.FileContentTestListenerfactoryTest");

        List<ConversionStrategy<?>> expected = new LinkedList<ConversionStrategy<?>>();
        expected.add(new PropertiesConversionStrategy());
        expected.add(new StringConversionStrategy());

        ReflectionAssert.assertReflectionEquals(expected, result);
    }

    @Test
    public void testCreateFileContentTestListener_Empty_Input() {
        Properties properties = new Properties();

        properties.setProperty(FileContentTestListenerFactory.DEFAULT_CONVERSION_STRATEGY_KEY, "");
        properties.setProperty(FileContentTestListenerFactory.READER_STRATEGY_KEY, FileReadingStrategy.class.getName());
        properties.setProperty(FileContentTestListenerFactory.CUSTOM_CONVERSION_STRATEGY_KEY, "");


        FileContentTestListener result = FileContentTestListenerFactory.createFileContentTestListener(properties);
        Assert.assertTrue(result.getConversionStrategiesList().isEmpty());
    }


}
