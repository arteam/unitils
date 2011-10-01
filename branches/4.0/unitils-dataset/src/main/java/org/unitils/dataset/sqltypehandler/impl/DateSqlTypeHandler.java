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

import org.unitils.core.UnitilsException;
import org.unitils.dataset.sqltypehandler.SqlTypeHandler;
import org.unitils.util.PropertyUtils;

import java.sql.ResultSet;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DateSqlTypeHandler implements SqlTypeHandler<Date> {

    public static final String PROPKEY_DATE_FORMAT = "dataset.dateFormat";
    public static final String PROPKEY_TIME_FORMAT = "dataset.timeFormat";
    public static final String PROPKEY_DATETIME_FORMAT = "dataset.dateTimeFormat";

    private String datePattern;
    private String timePattern;
    private String dateTimePattern;

    private DateFormat defaultDateFormat;
    private DateFormat defaultTimeFormat;
    private DateFormat defaultDateTimeFormat;

    public void init(Properties configuration) {
        datePattern = PropertyUtils.getString(PROPKEY_DATE_FORMAT, configuration);
        timePattern = PropertyUtils.getString(PROPKEY_TIME_FORMAT, configuration);
        dateTimePattern = datePattern + " " + timePattern;
        defaultDateFormat = new SimpleDateFormat(datePattern);
        defaultTimeFormat = new SimpleDateFormat(timePattern);
        defaultDateTimeFormat = new SimpleDateFormat(dateTimePattern);
    }

    public Date getValue(String valueAsString, int sqlType) throws Exception {
        Date date = parseDate(valueAsString);
        if (Types.DATE == sqlType) {
            return new java.sql.Date(date.getTime());
        } else if (Types.TIME == sqlType) {
            return new java.sql.Time(date.getTime());
        }
        return new java.sql.Timestamp(date.getTime());
    }

    public Date getResultSetValue(ResultSet resultSet, int columnIndex, int sqlType) throws Exception {
        if (Types.DATE == sqlType) {
            return resultSet.getDate(columnIndex);
        } else if (Types.TIME == sqlType) {
            return resultSet.getTime(columnIndex);
        }
        return resultSet.getTimestamp(columnIndex);
    }


    protected Date parseDate(String dateAsString) {
        try {
            return defaultDateTimeFormat.parse(dateAsString);
        } catch (ParseException e) {
            // ignore
        }
        try {
            return defaultDateFormat.parse(dateAsString);
        } catch (ParseException e) {
            // ignore
        }
        try {
            return defaultTimeFormat.parse(dateAsString);
        } catch (ParseException e) {
            // ignore
        }
        throw new UnitilsException("Unable to parse date value " + dateAsString + ". Tried following patterns: '" + dateTimePattern + "', '" + datePattern + "' and '" + timePattern + "'");
    }
}