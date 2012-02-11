/*
 * Copyright 2010,  Unitils.org
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

package org.unitilsnew.core;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitilsnew.core.Context.Key;

/**
 * @author Tim Ducheyne
 */
public class ContextKeyEqualsTest {


    @Test
    public void equal() {
        Key key1 = new Key(StringBuffer.class, "a", "b");
        Key key2 = new Key(StringBuffer.class, "a", "b");

        assertTrue(key1.equals(key2));
        assertTrue(key2.equals(key1));
    }

    @Test
    public void same() {
        Key key = new Key(StringBuffer.class, "a", "b");

        assertTrue(key.equals(key));
    }

    @Test
    public void notEqualType() {
        Key key1 = new Key(StringBuffer.class);
        Key key2 = new Key(List.class);

        assertFalse(key1.equals(key2));
        assertFalse(key2.equals(key1));
    }

    @Test
    public void notEqualClassifiers() {
        Key key1 = new Key(StringBuffer.class, "a", "b");
        Key key2 = new Key(StringBuffer.class, "c", "d");

        assertFalse(key1.equals(key2));
        assertFalse(key2.equals(key1));
    }

    @Test
    public void notEqualToNull() {
        Key key1 = new Key(StringBuffer.class);

        assertFalse(key1.equals(null));
    }

    @Test
    public void notEqualToOtherType() {
        Key key1 = new Key(StringBuffer.class);

        assertFalse(key1.equals("xxx"));
    }

    @Test
    public void nullKeyValues() {
        Key key1 = new Key(null, null);
        Key key2 = new Key(null, null);

        assertTrue(key1.equals(key2));
    }
}
