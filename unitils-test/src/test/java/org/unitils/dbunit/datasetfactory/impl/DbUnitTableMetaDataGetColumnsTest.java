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

import org.dbunit.dataset.Column;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class DbUnitTableMetaDataGetColumnsTest {

    /* Tested object */
    private DbUnitTableMetaData dbUnitTableMetaData;

    private Column column1;
    private Column column2;


    @Before
    public void initialize() {
        dbUnitTableMetaData = new DbUnitTableMetaData("table");

        column1 = new Column("column1", VARCHAR);
        column2 = new Column("column2", VARCHAR);
    }


    @Test
    public void getColumns() throws Exception {
        dbUnitTableMetaData.addColumn(column1);
        dbUnitTableMetaData.addColumn(column2);

        Column[] result = dbUnitTableMetaData.getColumns();
        assertReflectionEquals(asList(column1, column2), result);
    }

    @Test
    public void emptyWhenNoColumns() throws Exception {
        Column[] result = dbUnitTableMetaData.getColumns();
        assertEquals(0, result.length);
    }
}
