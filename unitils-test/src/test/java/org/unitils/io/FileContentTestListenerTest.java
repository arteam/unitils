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

import org.junit.Before;
import org.junit.Test;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.conversion.impl.PropertiesConversionStrategy;
import org.unitils.io.conversion.impl.StringConversionStrategy;
import org.unitils.io.reader.impl.FileReadingStrategy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.unitils.util.ReflectionUtils.getFieldsOfType;

/**
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
public class FileContentTestListenerTest {

    private FileContentTestListener listener;

    @Before
    public void setUp() {
        listener = new FileContentTestListener();

        List<ConversionStrategy<?>> conversions = new ArrayList<ConversionStrategy<?>>();
        conversions.add(new PropertiesConversionStrategy());
        conversions.add(new StringConversionStrategy());

        listener.setConversionStrategiesList(conversions);
        listener.setDefaultEncoding("Cp1252");

        FileReadingStrategy readingStrategy = new FileReadingStrategy();
        listener.setDefaultReadingStrategy(readingStrategy);
    }

    @Test
    public void testDefaultPropertyLoad() {
        DefaultTestStub testObject = new DefaultTestStub();

        Field field = getFieldsOfType(DefaultTestStub.class, Properties.class, false).iterator().next();

        listener.handleField(testObject, field);


        Properties result = testObject.defaultProperties;
        assertNotNull(result);
        assertEquals("text file", result.get("FileContentTestListenerTest"));

    }


    @Test
    public void testDefaultStringLoad() {
        DefaultTestStub testObject = new DefaultTestStub();

        Field field = getFieldsOfType(DefaultTestStub.class, String.class, false).iterator().next();
        listener.handleField(testObject, field);

        String result = testObject.defaultString;
        assertEquals("The FileContentTestLisener txt test file", result);

    }

    @Test
    public void testHardCodedPropertyLoad() {
        HardCodeTestStub testObject = new HardCodeTestStub();

        Field field = getFieldsOfType(HardCodeTestStub.class, Properties.class, false).iterator().next();
        listener.handleField(testObject, field);

        Properties result = testObject.defaultProperties;
        assertNotNull(result);
        assertEquals("pub file", result.get("FileContentTestListenerTest"));

    }

    @Test
    public void testHardCodedStringLoad() {
        HardCodeTestStub testObject = new HardCodeTestStub();

        Field field = getFieldsOfType(HardCodeTestStub.class, String.class, false).iterator().next();
        listener.handleField(testObject, field);

        String result = testObject.defaultString;
        assertEquals("FileContentTestListenerTest=pub file", result);
    }

    @Test
    public void testDetermineConversionStrategy() throws Exception {

        Field field = getFieldsOfType(HardCodedDifferentConversionStrategyStub.class, Object.class, false).iterator().next();
        ConversionStrategy<?> result = listener.determineConversionStrategy(field);
        assertTrue(result instanceof DummyConversionStrategy);
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

    public class HardCodedDifferentConversionStrategyStub {

        @FileContent(conversionStrategy = DummyConversionStrategy.class)
        Object justSomeObject;
    }

}
