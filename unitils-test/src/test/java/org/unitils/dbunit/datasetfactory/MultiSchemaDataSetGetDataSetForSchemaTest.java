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
package org.unitils.dbunit.datasetfactory;

import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class MultiSchemaDataSetGetDataSetForSchemaTest extends UnitilsJUnit4 {

    /* Tested object */
    private MultiSchemaDataSet multiSchemaDataSet;

    @Dummy
    private IDataSet dataSet;


    @Before
    public void initialize() {
        multiSchemaDataSet = new MultiSchemaDataSet();
        multiSchemaDataSet.setDataSetForSchema("schema", dataSet);
    }


    @Test
    public void getDataSetForSchema() {
        IDataSet result = multiSchemaDataSet.getDataSetForSchema("schema");
        assertSame(dataSet, result);
    }

    @Test
    public void nullWhenNotFound() {
        IDataSet result = multiSchemaDataSet.getDataSetForSchema("xxx");
        assertNull(result);
    }
}