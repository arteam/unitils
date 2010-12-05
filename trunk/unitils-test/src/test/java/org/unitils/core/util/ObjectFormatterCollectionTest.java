/*
 * Copyright Unitils.org
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
package org.unitils.core.util;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;


/**
 * Tests the formatting of proxies and mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectFormatterCollectionTest extends UnitilsJUnit4 {

    private ObjectFormatter objectFormatter = new ObjectFormatter(2, 3);


    @Test
    public void list() {
        List<String> list = asList("1", "2", "3");
        String result = objectFormatter.format(list);
        assertEquals("[\"1\", \"2\", \"3\"]", result);
    }

    @Test
    public void listMaxElements() {
        List<String> list = asList("1", "2", "3", "4");
        String result = objectFormatter.format(list);
        assertEquals("[\"1\", \"2\", \"3\", ...]", result);
    }

    @Test
    public void emptyList() {
        List<String> list = new ArrayList<String>();
        String result = objectFormatter.format(list);
        assertEquals("[]", result);
    }


    @Test
    public void map() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        String result = objectFormatter.format(map);
        assertEquals("{\"1\"=\"a\", \"2\"=\"b\", \"3\"=\"c\"}", result);
    }

    @Test
    public void mapMaxElements() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "d");
        String result = objectFormatter.format(map);
        assertEquals("{\"1\"=\"a\", \"2\"=\"b\", \"3\"=\"c\", ...}", result);
    }

    @Test
    public void emptyMap() {
        Map<String, String> map = new HashMap<String, String>();
        String result = objectFormatter.format(map);
        assertEquals("{}", result);
    }

}