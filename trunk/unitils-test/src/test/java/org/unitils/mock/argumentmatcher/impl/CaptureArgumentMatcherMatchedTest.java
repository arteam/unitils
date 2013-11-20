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
package org.unitils.mock.argumentmatcher.impl;

import org.junit.Test;
import org.unitils.mock.argumentmatcher.Capture;
import org.unitils.mock.core.proxy.Argument;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class CaptureArgumentMatcherMatchedTest {

    private CaptureArgumentMatcher<String> captureArgumentMatcher;


    @Test
    public void matched() {
        Argument<String> argument = new Argument<String>("value", "cloned value", String.class);
        Capture<String> capture = new Capture<String>(String.class);
        captureArgumentMatcher = new CaptureArgumentMatcher<String>(capture);

        captureArgumentMatcher.matched(argument);
        assertEquals("value", capture.getValue());
        assertEquals("cloned value", capture.getValueAtInvocationTime());
        assertEquals(String.class, capture.getType());
    }
}
