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
package org.unitils.dataset;

import org.junit.Test;

import static org.unitils.dataset.DataSetAssert.assertInlineDataSet;
import static org.unitils.dataset.DataSetLoader.cleanInsertDataSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineAssertDataSetTest extends OneDbDataSetTestBase {

    @Test
    public void successfulAssert() throws Exception {
        cleanInsertDataSet("TEST col1=value1", "TEST col1=value2");
        assertInlineDataSet("TEST col1=value1", "TEST col1=value2");
    }

    @Test(expected = AssertionError.class)
    public void failingAssert() throws Exception {
        cleanInsertDataSet("TEST col1=value1");
        assertInlineDataSet("TEST col1=value1", "TEST col1=value2");
    }

    @Test
    public void assertNotExists() throws Exception {
        cleanInsertDataSet("TEST col1=value1");
        assertInlineDataSet("!TEST col1=value2");
    }

    @Test(expected = AssertionError.class)
    public void assertNotExists_failed() throws Exception {
        cleanInsertDataSet("TEST col1=value1");
        assertInlineDataSet("!TEST col1=value1");
    }

    @Test
    public void assertNoRecordsFound() throws Exception {
        assertInlineDataSet("TEST");
    }

    @Test(expected = AssertionError.class)
    public void assertNoRecordsFound_failed() throws Exception {
        cleanInsertDataSet("TEST col1=value1");
        assertInlineDataSet("TEST");
    }

}