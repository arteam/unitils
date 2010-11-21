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
package org.unitils.dataset.sqltypehandler.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import static java.sql.Types.TIMESTAMP;
import static java.util.Calendar.*;
import static org.junit.Assert.assertEquals;
import static org.unitils.dataset.sqltypehandler.impl.DateSqlTypeHandler.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DateSqlTypeHandlerGetValueTest {

    private DateSqlTypeHandler dateSqlTypeHandler = new DateSqlTypeHandler();

    @Before
    public void initialize() {
        Properties configuration = new Properties();
        configuration.put(PROPKEY_DATE_FORMAT, "yyyy-MM-dd");
        configuration.put(PROPKEY_TIME_FORMAT, "HH:mm:ss");
        configuration.put(PROPKEY_DATETIME_FORMAT, "yyyy-MM-dd HH:mm:ss");
        dateSqlTypeHandler.init(configuration);
    }

    @Test
    public void dateTime() throws Exception {
        Date date = dateSqlTypeHandler.getValue("2010-11-10 1:2:14", TIMESTAMP);
        assertDate(2010, 10, 10, 1, 2, 14, date);
    }

    @Test
    public void date() throws Exception {
        Date date = dateSqlTypeHandler.getValue("2010-11-10", 0);
        assertDate(2010, 10, 10, 0, 0, 0, date);
    }

    @Test
    public void time() throws Exception {
        Date date = dateSqlTypeHandler.getValue("1:2:14", 0);
        assertDate(1970, 0, 1, 1, 2, 14, date);
    }

    @Test
    public void invalidString() throws Exception {
        try {
            dateSqlTypeHandler.getValue("xxx", TIMESTAMP);
        } catch (UnitilsException e) {
            assertEquals("Unable to parse date value. Tried following patterns: 'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' and 'HH:mm:ss'", e.getMessage());
        }
    }


    private void assertDate(int year, int month, int day, int hour, int minutes, int seconds, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        assertEquals(year, calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day, calendar.get(DAY_OF_MONTH));
        assertEquals(hour, calendar.get(HOUR_OF_DAY));
        assertEquals(minutes, calendar.get(MINUTE));
        assertEquals(seconds, calendar.get(SECOND));
        assertEquals(0, calendar.get(MILLISECOND));
    }

}
