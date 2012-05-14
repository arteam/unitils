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
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitilsnew.database.core.DataSourceWrapper.RowParameterizedRowMapper;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperRowParameterizedRowMapperMapRowTest extends UnitilsJUnit4 {

    /* Tested object */
    private RowParameterizedRowMapper rowParameterizedRowMapper = new RowParameterizedRowMapper();

    private Mock<ResultSet> resultSetMock;
    private Mock<ResultSetMetaData> resultSetMetaDataMock;


    @Before
    public void initialize() throws Exception {
        resultSetMock.returns(resultSetMetaDataMock).getMetaData();
    }


    @Test
    public void mapRow() throws Exception {
        resultSetMetaDataMock.returns(3).getColumnCount();
        resultSetMock.returns("col1").getString(1);
        resultSetMock.returns("col2").getString(2);
        resultSetMock.returns("col3").getString(3);

        List<String> result = rowParameterizedRowMapper.mapRow(resultSetMock.getMock(), 0);
        assertEquals(asList("col1", "col2", "col3"), result);
    }

    @Test
    public void emptyRow() throws Exception {
        resultSetMetaDataMock.returns(0).getColumnCount();

        List<String> result = rowParameterizedRowMapper.mapRow(resultSetMock.getMock(), 0);
        assertTrue(result.isEmpty());
    }
}
