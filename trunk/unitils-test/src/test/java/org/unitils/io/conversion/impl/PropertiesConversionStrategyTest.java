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

package org.unitils.io.conversion.impl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class PropertiesConversionStrategyTest {

    /* Tested object */
    private PropertiesConversionStrategy conversion = new PropertiesConversionStrategy();

    private String input = "test=$µé*ù¨´ù]:~e;[=+";


    @Test
    public void validEncodingUtf8() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("utf-8"));

        Properties result = conversion.convertContent(inputStream, "utf-8");
        assertEquals("{" + input + "}", result.toString());
    }


    @Test
    public void validEncodingISO88591() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("ISO-8859-1"));

        Properties result = conversion.convertContent(inputStream, "ISO-8859-1");
        assertEquals("{" + input + "}", result.toString());
    }


    @Test(expected = UnsupportedEncodingException.class)
    public void invalidEncoding() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        conversion.convertContent(inputStream, "xxxx");
    }
}
