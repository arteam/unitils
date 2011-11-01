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


package org.unitils.io.filecontent.impl;

/**
 * @author Jeroen Horemans
 * @since 3.3
 */
public class DefaultFileContentReaderFactoryTest {


//    /* Tested object */
//    private DefaultFileContentReaderFactory defaultFileContentReaderFactory = new DefaultFileContentReaderFactory();
//
//    private Properties properties;
//
//    @Before
//    public void initialize() {
//        properties = new Properties();
//        properties.setProperty(DEFAULT_CONVERSION_STRATEGY_KEY, "org.unitils.io.conversion.impl.PropertiesConversionStrategy, org.unitils.io.conversion.impl.StringConversionStrategy");
//    }
//
//    @Test
//    public void defaultSetup() {
//        FileContentReader fileContentReader = defaultFileContentReaderFactory.createFileContentReader(properties);
//        Assert.assertNotNull(fileContentReader);
//    }
//
//    @Test
//    public void customSetup() {
//        Properties properties = new Properties();
//        properties.setProperty(KEY, "org.unitils.io.conversion.impl.PropertiesConversionStrategy,org.unitils.io.conversion.impl.StringConversionStrategy");
//
//
//        List result = DefaultFileContentReaderFactory.createConversionStrategies(properties, "IoModule.FileContentTestListenerfactoryTest");
//
//        List<ConversionStrategy<?>> expected = new LinkedList<ConversionStrategy<?>>();
//        expected.add(new PropertiesConversionStrategy());
//        expected.add(new StringConversionStrategy());
//
//        ReflectionAssert.assertReflectionEquals(expected, result);
//    }
//
//    @Test
//    public void testCreateFileContentTestListener_Empty_Input() {
//        Properties properties = new Properties();
//
//        properties.setProperty(DEFAULT_CONVERSION_STRATEGY_KEY, "");
//        properties.setProperty(DefaultFileContentReaderFactory.READER_STRATEGY_KEY, FileReadingStrategy.class.getName());
//        properties.setProperty(DefaultFileContentReaderFactory.CUSTOM_CONVERSION_STRATEGY_KEY, "");
//
//
//        FileContentTestListener result = DefaultFileContentReaderFactory.createFileContentTestListener(properties);
//        Assert.assertTrue(result.getConversionStrategiesList().isEmpty());
//    }
//

}
