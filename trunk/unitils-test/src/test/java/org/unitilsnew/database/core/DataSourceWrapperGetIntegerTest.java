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

package org.unitilsnew.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class DataSourceWrapperGetIntegerTest extends UnitilsJUnit4 {

    /* Tested object */
    private PartialMock<DataSourceWrapper> dataSourceWrapper;

    private Mock<SimpleJdbcTemplate> simpleJdbcTemplateMock;


    @Before
    public void initialize() {
        dataSourceWrapper.returns(simpleJdbcTemplateMock).getSimpleJdbcTemplate();
    }


    @Test
    public void getInteger() throws Exception {
        simpleJdbcTemplateMock.returns(5).queryForObject("query", Integer.class, "arg");

        int result = dataSourceWrapper.getMock().getInteger("query", "arg");
        assertEquals(5, result);
    }

    @Test
    public void exceptionWhenFailure() throws Exception {
        simpleJdbcTemplateMock.raises(new NullPointerException("message")).queryForObject("query", Integer.class, "arg");
        try {
            dataSourceWrapper.getMock().getInteger("query", "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'query'. Reason:\n" +
                    "message", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoValueFound() throws Exception {
        simpleJdbcTemplateMock.raises(new EmptyResultDataAccessException("message", 1)).queryForObject("query", Integer.class, "arg");
        try {
            dataSourceWrapper.getMock().getInteger("query", "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Statement did not produce any results: 'query'. Reason:\n" +
                    "message", e.getMessage());
        }
    }
}
