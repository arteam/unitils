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

import static org.junit.Assert.assertEquals;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class StringConversionStrategyTest {

    /* Tested object */
    private StringConversionStrategy conversion = new StringConversionStrategy();

    private String input = "€é*ù¨´ù]:~e;[=+";


    @Test
    public void validEncoding() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes("utf-8"));

        String result = conversion.convertContent(inputStream, "utf-8");
        assertEquals(input, result);
    }

    @Test(expected = UnsupportedEncodingException.class)
    public void invalidEncoding() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        conversion.convertContent(inputStream, "xxxx");
    }
}
