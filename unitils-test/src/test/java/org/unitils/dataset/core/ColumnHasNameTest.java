/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnHasNameTest {

    private List<String> emptyVariables = new ArrayList<String>();


    @Test
    public void caseInsensitive() {
        Column column = new Column("column", "value", false);
        boolean result = column.hasName("COLUMN");
        assertTrue(result);
    }

    @Test
    public void different() {
        Column column = new Column("column", "value", false);
        boolean result = column.hasName("xxxx");
        assertFalse(result);
    }

    @Test
    public void caseSensitive() {
        Column column = new Column("column", "value", true);

        boolean result = column.hasName("column");
        assertTrue(result);
    }

    @Test
    public void differentCaseSensitive() {
        Column column = new Column("column", "value", true);

        boolean result = column.hasName("COLUMN");
        assertFalse(result);
    }
}