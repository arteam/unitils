/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */


package org.unitils.io.filecontent.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.io.conversion.ConversionStrategy;
import org.unitils.io.reader.ReadingStrategy;
import org.unitils.mock.Mock;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @since 3.3
 */
public class DefaultFileContentReaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultFileContentReader defaultFileContentReader;

    private Mock<ReadingStrategy> readingStrategyMock;
    private Mock<ConversionStrategy<?>> stringConversionStrategyMock;
    private Mock<ConversionStrategy<?>> mapConversionStrategyMock;
    private Mock<InputStream> inputStreamMock;

    @Before
    public void initialize() throws Exception {
        List<ConversionStrategy<?>> conversionStrategies = new ArrayList<ConversionStrategy<?>>();
        conversionStrategies.add(stringConversionStrategyMock.getMock());
        conversionStrategies.add(mapConversionStrategyMock.getMock());

        defaultFileContentReader = new DefaultFileContentReader(readingStrategyMock.getMock(), conversionStrategies, "defaultEncoding");

        stringConversionStrategyMock.returns(String.class).getTargetType();
        mapConversionStrategyMock.returns(Map.class).getTargetType();
        readingStrategyMock.returns(inputStreamMock).getInputStream("fileName", DefaultFileContentReader.class);
    }


    @Test
    public void firstConversionStrategyHasTargetType() throws Exception {
        stringConversionStrategyMock.returns("resultString").convertContent(inputStreamMock.getMock(), "utf-8");

        String result = defaultFileContentReader.readFileContent("fileName", String.class, "utf-8", DefaultFileContentReader.class);
        assertEquals("resultString", result);
        inputStreamMock.assertInvoked().close();
    }

    @Test
    public void secondConversionStrategyHasTargetType() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        mapConversionStrategyMock.returns(map).convertContent(inputStreamMock.getMock(), "utf-8");

        Map result = defaultFileContentReader.readFileContent("fileName", Map.class, "utf-8", DefaultFileContentReader.class);
        assertSame(map, result);
    }

    @Test
    public void conversionStrategyHasAssignableTargetType() throws Exception {
        Properties properties = new Properties();
        mapConversionStrategyMock.returns(properties).convertContent(inputStreamMock.getMock(), "utf-8");

        Properties result = defaultFileContentReader.readFileContent("fileName", Properties.class, "utf-8", DefaultFileContentReader.class);
        assertSame(properties, result);
    }

    @Test(expected = UnitilsException.class)
    public void noConversionStrategyFoundForTargetType() throws Exception {
        defaultFileContentReader.readFileContent("fileName", Set.class, "utf-8", DefaultFileContentReader.class);
    }

    @Test
    public void useDefaultEncodingWhenEncodingIsNull() throws Exception {
        stringConversionStrategyMock.returns("resultString").convertContent(inputStreamMock.getMock(), "defaultEncoding");

        String result = defaultFileContentReader.readFileContent("fileName", String.class, null, DefaultFileContentReader.class);
        assertEquals("resultString", result);
    }

    @Test
    public void useDefaultFileNameIfFileNameIsNull() throws Exception {
        stringConversionStrategyMock.returns("extension").getDefaultFileExtension();
        readingStrategyMock.returns(inputStreamMock).getDefaultInputStream("extension", DefaultFileContentReader.class);
        stringConversionStrategyMock.returns("resultString").convertContent(inputStreamMock.getMock(), "utf-8");

        String result = defaultFileContentReader.readFileContent(null, String.class, "utf-8", DefaultFileContentReader.class);
        assertEquals("resultString", result);
    }

    @Test(expected = UnitilsException.class)
    public void exceptionDuringReading() throws Exception {
        readingStrategyMock.onceRaises(UnsupportedEncodingException.class).getInputStream("fileName", DefaultFileContentReader.class);
        defaultFileContentReader.readFileContent("fileName", String.class, "utf-8", DefaultFileContentReader.class);
    }

    @Test(expected = UnitilsException.class)
    public void exceptionDuringConversion() throws Exception {
        stringConversionStrategyMock.onceRaises(ClassCastException.class).convertContent(inputStreamMock.getMock(), "utf-8");
        defaultFileContentReader.readFileContent("fileName", String.class, "utf-8", DefaultFileContentReader.class);
    }
}
