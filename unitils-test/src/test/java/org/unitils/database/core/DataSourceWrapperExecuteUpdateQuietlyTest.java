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
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperExecuteUpdateQuietlyTest extends UnitilsJUnit4 {

    /* Tested object */
    private PartialMock<DataSourceWrapper> dataSourceWrapper;

    private Mock<SimpleJdbcTemplate> simpleJdbcTemplateMock;


    @Before
    public void initialize() {
        dataSourceWrapper.returns(simpleJdbcTemplateMock).getSimpleJdbcTemplate();
    }


    @Test
    public void executeUpdateQuietly() throws Exception {
        simpleJdbcTemplateMock.returns(5).update("sql", "arg");

        int result = dataSourceWrapper.getMock().executeUpdateQuietly("sql", "arg");

        simpleJdbcTemplateMock.assertInvoked().update("sql", "arg");
        assertEquals(5, result);
    }

    @Test
    public void ignoredWhenFailure() throws Exception {
        simpleJdbcTemplateMock.raises(new NullPointerException("message")).update("sql", "arg");

        int result = dataSourceWrapper.getMock().executeUpdateQuietly("sql", "arg");
        assertEquals(-1, result);
    }
}
