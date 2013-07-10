/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dbunit.dataset;

import org.junit.Test;
import org.unitils.dbunit.dataset.comparison.ColumnDifference;

import java.math.BigInteger;
import java.util.Date;

import static org.dbunit.dataset.datatype.DataType.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ColumnCompareTest {

    @Test
    public void equalWhenSameValue() {
        Column column = new Column("name", DATE, new Date());
        Column actualColumn = new Column("name", DATE, column.getValue());

        ColumnDifference result = column.compare(actualColumn);
        assertNull(result);
    }

    @Test
    public void equalWhenBothNull() {
        Column column = new Column("name", DATE, null);
        Column actualColumn = new Column("name", DATE, null);

        ColumnDifference result = column.compare(actualColumn);
        assertNull(result);
    }

    @Test
    public void differentWhenValueIsNullAndActualValueNot() {
        Column column = new Column("name", VARCHAR, null);
        Column actualColumn = new Column("name", VARCHAR, "value");

        ColumnDifference result = column.compare(actualColumn);
        assertSame(column, result.getColumn());
        assertSame(actualColumn, result.getActualColumn());
    }

    @Test
    public void differentWhenActualValueIsNullAndValueNot() {
        Column column = new Column("name", VARCHAR, "value");
        Column actualColumn = new Column("name", VARCHAR, null);

        ColumnDifference result = column.compare(actualColumn);
        assertSame(column, result.getColumn());
        assertSame(actualColumn, result.getActualColumn());
    }

    @Test
    public void differentWhenValueIsNotEqual() {
        Column column = new Column("name", VARCHAR, "111");
        Column actualColumn = new Column("name", VARCHAR, "222");

        ColumnDifference result = column.compare(actualColumn);
        assertSame(column, result.getColumn());
        assertSame(actualColumn, result.getActualColumn());
    }

    @Test
    public void equalWhenCastedValueIsEqual() {
        Column column = new Column("name", VARCHAR, "111");
        Column actualColumn = new Column("name", BIGINT, new BigInteger("111"));

        ColumnDifference result = column.compare(actualColumn);
        assertNull(result);
    }

    @Test
    public void differentWhenCastedValueIsNotEqual() {
        Column column = new Column("name", VARCHAR, "111");
        Column actualColumn = new Column("name", BIGINT, new BigInteger("222"));

        ColumnDifference result = column.compare(actualColumn);
        assertSame(column, result.getColumn());
        assertSame(actualColumn, result.getActualColumn());
    }
}