/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.mock.argumentmatcher;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.core.proxy.Argument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class CaptureGetValueTest {

    private Capture<String> capture;


    @Before
    public void initialize() {
        capture = new Capture<String>(String.class);
    }


    @Test
    public void getValue() {
        Argument<String> argument = new Argument<String>("value", "cloned value", String.class);
        capture.setArgument(argument);

        String result = capture.getValue();
        assertEquals("value", result);
    }

    @Test
    public void nullValue() {
        Argument<String> argument = new Argument<String>(null, null, String.class);
        capture.setArgument(argument);

        String result = capture.getValue();
        assertNull(result);
    }

    @Test
    public void nullWhenNoArgumentSet() {
        String result = capture.getValue();
        assertNull(result);
    }
}
