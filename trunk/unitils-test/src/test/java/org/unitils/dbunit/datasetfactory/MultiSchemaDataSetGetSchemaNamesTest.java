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

import org.junit.Test;

import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class MultiSchemaDataSetGetSchemaNamesTest {

    /* Tested object */
    private MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();


    @Test
    public void setDataSetForSchema() {
        multiSchemaDataSet.setDataSetForSchema("schema1", null);
        multiSchemaDataSet.setDataSetForSchema("schema2", null);

        Set<String> result = multiSchemaDataSet.getSchemaNames();
        assertLenientEquals(asList("schema1", "schema2"), result);
    }

    @Test
    public void emptyWhenNoneFound() {
        Set<String> result = multiSchemaDataSet.getSchemaNames();
        assertTrue(result.isEmpty());
    }
}