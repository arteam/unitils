/*
 * Copyright 2012,  Unitils.org
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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperGetTableCountTest extends UnitilsJUnit4 {

    /* Tested object */
    private PartialMock<DataSourceWrapper> dataSourceWrapper;

    private Mock<SimpleJdbcTemplate> simpleJdbcTemplateMock;


    @Before
    public void initialize() {
        dataSourceWrapper.returns(simpleJdbcTemplateMock).getSimpleJdbcTemplate();
    }


    @Test
    public void getTableCount() throws Exception {
        simpleJdbcTemplateMock.returns(5L).queryForObject("select count(1) from my_table", Long.class);

        long result = dataSourceWrapper.getMock().getTableCount("my_table");
        assertEquals(5, result);
    }

    @Test
    public void exceptionWhenEmptyTableName() throws Exception {
        try {
            dataSourceWrapper.getMock().getTableCount("");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get table count. Table name is null or empty.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullTableName() throws Exception {
        try {
            dataSourceWrapper.getMock().getTableCount("");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get table count. Table name is null or empty.", e.getMessage());
        }
    }
}
