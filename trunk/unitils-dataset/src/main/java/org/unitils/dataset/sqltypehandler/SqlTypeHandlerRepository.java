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

import org.unitils.core.util.ConfigUtils;
import org.unitils.dataset.sqltypehandler.impl.TextSqlTypeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.sql.Types.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SqlTypeHandlerRepository {

    private Map<Integer, SqlTypeHandler<?>> sqlTypeHandlers = new HashMap<Integer, SqlTypeHandler<?>>();

    private SqlTypeHandler<?> defaultSqlTypeHandler = new TextSqlTypeHandler();


    public void init(Properties configuration) {
        addConfiguredSqlTypeHandler(BIT, "BIT", configuration);
        addConfiguredSqlTypeHandler(TINYINT, "TINYINT", configuration);
        addConfiguredSqlTypeHandler(SMALLINT, "SMALLINT", configuration);
        addConfiguredSqlTypeHandler(INTEGER, "INTEGER", configuration);
        addConfiguredSqlTypeHandler(BIGINT, "BIGINT", configuration);
        addConfiguredSqlTypeHandler(FLOAT, "FLOAT", configuration);
        addConfiguredSqlTypeHandler(REAL, "REAL", configuration);
        addConfiguredSqlTypeHandler(DOUBLE, "DOUBLE", configuration);
        addConfiguredSqlTypeHandler(NUMERIC, "NUMERIC", configuration);
        addConfiguredSqlTypeHandler(DECIMAL, "DECIMAL", configuration);
        addConfiguredSqlTypeHandler(CHAR, "CHAR", configuration);
        addConfiguredSqlTypeHandler(VARCHAR, "VARCHAR", configuration);
        addConfiguredSqlTypeHandler(LONGVARCHAR, "LONGVARCHAR", configuration);
        // todo correctly handle date formats
        addConfiguredSqlTypeHandler(DATE, "DATE", configuration);
        addConfiguredSqlTypeHandler(TIME, "TIME", configuration);
        addConfiguredSqlTypeHandler(TIMESTAMP, "TIMESTAMP", configuration);
        addConfiguredSqlTypeHandler(BINARY, "BINARY", configuration);
        addConfiguredSqlTypeHandler(VARBINARY, "VARBINARY", configuration);
        addConfiguredSqlTypeHandler(LONGVARBINARY, "LONGVARBINARY", configuration);
        addConfiguredSqlTypeHandler(NULL, "NULL", configuration);
        addConfiguredSqlTypeHandler(OTHER, "OTHER", configuration);
        addConfiguredSqlTypeHandler(JAVA_OBJECT, "JAVA_OBJECT", configuration);
        addConfiguredSqlTypeHandler(DISTINCT, "DISTINCT", configuration);
        addConfiguredSqlTypeHandler(STRUCT, "STRUCT", configuration);
        addConfiguredSqlTypeHandler(ARRAY, "ARRAY", configuration);
        addConfiguredSqlTypeHandler(BLOB, "BLOB", configuration);
        addConfiguredSqlTypeHandler(CLOB, "CLOB", configuration);
        addConfiguredSqlTypeHandler(REF, "REF", configuration);
        addConfiguredSqlTypeHandler(DATALINK, "DATALINK", configuration);
        addConfiguredSqlTypeHandler(BOOLEAN, "BOOLEAN", configuration);
    }


    public SqlTypeHandler<?> getSqlTypeHandler(int sqlType) {
        SqlTypeHandler<?> sqlTypeHandler = sqlTypeHandlers.get(sqlType);
        if (sqlTypeHandler != null) {
            return sqlTypeHandler;
        }
        return defaultSqlTypeHandler;
    }


    protected void addConfiguredSqlTypeHandler(int sqlType, String name, Properties configuration) {
        SqlTypeHandler<?> sqlTypeHandler = ConfigUtils.getInstanceOf(SqlTypeHandler.class, configuration, name);
        sqlTypeHandlers.put(sqlType, sqlTypeHandler);
    }
}
