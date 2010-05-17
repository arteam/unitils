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
package org.unitils.dataset.sqltypehandler;

import org.unitils.dataset.sqltypehandler.impl.BooleanSqlTypeHandler;
import org.unitils.dataset.sqltypehandler.impl.DateSqlTypeHandler;
import org.unitils.dataset.sqltypehandler.impl.NumberSqlTypeHandler;
import org.unitils.dataset.sqltypehandler.impl.TextSqlTypeHandler;

import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SqlTypeHandlerRepository {

    private Map<Integer, SqlTypeHandler<?>> sqlTypeHandlers = new HashMap<Integer, SqlTypeHandler<?>>();

    private SqlTypeHandler<?> defaultSqlTypeHandler = new TextSqlTypeHandler();


    public SqlTypeHandlerRepository() {
        sqlTypeHandlers.put(BIT, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(TINYINT, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(SMALLINT, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(INTEGER, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(BIGINT, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(FLOAT, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(REAL, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(DOUBLE, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(NUMERIC, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(DECIMAL, new NumberSqlTypeHandler());
        sqlTypeHandlers.put(CHAR, new TextSqlTypeHandler());
        sqlTypeHandlers.put(VARCHAR, new TextSqlTypeHandler());
        sqlTypeHandlers.put(LONGVARCHAR, new TextSqlTypeHandler());
        // todo correctly handle date formats
        sqlTypeHandlers.put(DATE, new DateSqlTypeHandler());
        sqlTypeHandlers.put(TIME, new DateSqlTypeHandler());
        sqlTypeHandlers.put(TIMESTAMP, new DateSqlTypeHandler());
        sqlTypeHandlers.put(BINARY, new TextSqlTypeHandler());
        sqlTypeHandlers.put(VARBINARY, new TextSqlTypeHandler());
        sqlTypeHandlers.put(LONGVARBINARY, new TextSqlTypeHandler());
        sqlTypeHandlers.put(NULL, new TextSqlTypeHandler());
        sqlTypeHandlers.put(OTHER, new TextSqlTypeHandler());
        sqlTypeHandlers.put(JAVA_OBJECT, new TextSqlTypeHandler());
        sqlTypeHandlers.put(DISTINCT, new TextSqlTypeHandler());
        sqlTypeHandlers.put(STRUCT, new TextSqlTypeHandler());
        sqlTypeHandlers.put(ARRAY, new TextSqlTypeHandler());
        sqlTypeHandlers.put(BLOB, new TextSqlTypeHandler());
        sqlTypeHandlers.put(CLOB, new TextSqlTypeHandler());
        sqlTypeHandlers.put(REF, new TextSqlTypeHandler());
        sqlTypeHandlers.put(DATALINK, new TextSqlTypeHandler());
        sqlTypeHandlers.put(BOOLEAN, new BooleanSqlTypeHandler());
    }

    public SqlTypeHandler<?> getSqlTypeHandler(int sqlType) {
        SqlTypeHandler<?> sqlTypeHandler = sqlTypeHandlers.get(sqlType);
        if (sqlTypeHandler != null) {
            return sqlTypeHandler;
        }
        return defaultSqlTypeHandler;
    }
}
