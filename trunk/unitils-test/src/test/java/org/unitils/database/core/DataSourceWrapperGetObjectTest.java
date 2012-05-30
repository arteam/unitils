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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitilsnew.UnitilsJUnit4;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperGetObjectTest extends UnitilsJUnit4 {

    /* Tested object */
    private PartialMock<DataSourceWrapper> dataSourceWrapper;

    private Mock<SimpleJdbcTemplate> simpleJdbcTemplateMock;


    @Before
    public void initialize() {
        dataSourceWrapper.returns(simpleJdbcTemplateMock).getSimpleJdbcTemplate();
    }


    @Test
    public void getObject() throws Exception {
        simpleJdbcTemplateMock.returns(new BigDecimal(1)).queryForObject("query", BigDecimal.class, "arg");

        BigDecimal result = dataSourceWrapper.getMock().getObject("query", BigDecimal.class, "arg");
        assertEquals(new BigDecimal(1), result);
    }

    @Test
    public void exceptionWhenFailure() throws Exception {
        simpleJdbcTemplateMock.raises(new NullPointerException("message")).queryForObject("query", BigDecimal.class, "arg");
        try {
            dataSourceWrapper.getMock().getObject("query", BigDecimal.class, "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'query'.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoValueFound() throws Exception {
        simpleJdbcTemplateMock.raises(new EmptyResultDataAccessException("message", 1)).queryForObject("query", BigDecimal.class, "arg");
        try {
            dataSourceWrapper.getMock().getObject("query", BigDecimal.class, "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Statement did not produce any results: 'query'.\n" +
                    "Reason: EmptyResultDataAccessException: message", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenMoreThanOneValueFound() throws Exception {
        simpleJdbcTemplateMock.raises(new IncorrectResultSizeDataAccessException("message", 1)).queryForObject("query", BigDecimal.class, "arg");
        try {
            dataSourceWrapper.getMock().getObject("query", BigDecimal.class, "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Statement produced more than 1 result: 'query'.\n" +
                    "Reason: IncorrectResultSizeDataAccessException: message", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullType() throws Exception {
        try {
            dataSourceWrapper.getMock().getObject("query", null, "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Type cannot be null.", e.getMessage());
        }
    }
}
