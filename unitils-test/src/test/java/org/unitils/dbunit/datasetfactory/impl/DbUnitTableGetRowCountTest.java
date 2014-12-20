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
package org.unitils.dbunit.datasetfactory.impl;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class DbUnitTableGetRowCountTest {

    /* Tested object */
    private DbUnitTable dbUnitTable;


    @Before
    public void initialize() {
        dbUnitTable = new DbUnitTable("table");
    }


    @Test
    public void getRowCount() {
        dbUnitTable.addRow(asList("111", "222"));
        dbUnitTable.addRow(asList("333"));

        int result = dbUnitTable.getRowCount();
        assertEquals(2, result);
    }

    @Test
    public void zeroWhenNoRows() {
        int result = dbUnitTable.getRowCount();
        assertEquals(0, result);
    }
}
