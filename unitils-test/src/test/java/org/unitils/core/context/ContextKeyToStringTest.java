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

package org.unitils.core.context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.unitils.core.context.Context.Key;

/**
 * @author Tim Ducheyne
 */
public class ContextKeyToStringTest {


    @Test
    public void typeAndClassifiers() {
        Key key = new Key(StringBuffer.class, "a", "b");
        String result = key.toString();

        assertEquals("java.lang.StringBuffer (classifiers: [a, b])", result);
    }

    @Test
    public void nullType() {
        Key key = new Key(null, "a", "b");
        String result = key.toString();

        assertEquals("null (classifiers: [a, b])", result);
    }

    @Test
    public void nullClassifiers() {
        Key key = new Key(StringBuffer.class);
        String result = key.toString();

        assertEquals("java.lang.StringBuffer", result);
    }

    @Test
    public void nullTypeAndClassifiers() {
        Key key = new Key(null, (String[]) null);
        String result = key.toString();

        assertEquals("null", result);
    }
}
