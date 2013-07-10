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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class DbUnitDataSetGetDbUnitTableTest {

    /* Tested object */
    private DbUnitDataSet dbUnitDataSet;

    private DbUnitTable table;


    @Before
    public void initialize() {
        dbUnitDataSet = new DbUnitDataSet();

        table = new DbUnitTable("table");
        dbUnitDataSet.addTable(table);
    }


    @Test
    public void getDbUnitTable() {
        DbUnitTable result = dbUnitDataSet.getDbUnitTable("table");
        assertSame(table, result);
    }

    @Test
    public void nullWhenTableNotKnown() {
        DbUnitTable result = dbUnitDataSet.getDbUnitTable("xxx");
        assertNull(result);
    }
}
