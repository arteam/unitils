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

    protected Properties configuration;
    private SqlTypeHandler<?> defaultSqlTypeHandler;

    private Map<Integer, SqlTypeHandler<?>> sqlTypeHandlers = new HashMap<Integer, SqlTypeHandler<?>>();


    public void init(Properties configuration) {
        this.configuration = configuration;
        this.defaultSqlTypeHandler = new TextSqlTypeHandler();
    }


    public SqlTypeHandler<?> getSqlTypeHandler(int sqlType) {
        SqlTypeHandler<?> sqlTypeHandler = sqlTypeHandlers.get(sqlType);
        if (sqlTypeHandler == null) {
            sqlTypeHandler = createConfiguredSqlTypeHandler(sqlType);
            sqlTypeHandlers.put(sqlType, sqlTypeHandler);
        }
        return sqlTypeHandler;
    }


    protected SqlTypeHandler<?> createConfiguredSqlTypeHandler(int sqlType) {
        switch (sqlType) {
            case BIT:
                return createConfiguredSqlTypeHandler("BIT");
            case TINYINT:
                return createConfiguredSqlTypeHandler("TINYINT");
            case SMALLINT:
                return createConfiguredSqlTypeHandler("SMALLINT");
            case INTEGER:
                return createConfiguredSqlTypeHandler("INTEGER");
            case BIGINT:
                return createConfiguredSqlTypeHandler("BIGINT");
            case FLOAT:
                return createConfiguredSqlTypeHandler("FLOAT");
            case REAL:
                return createConfiguredSqlTypeHandler("REAL");
            case DOUBLE:
                return createConfiguredSqlTypeHandler("DOUBLE");
            case NUMERIC:
                return createConfiguredSqlTypeHandler("NUMERIC");
            case DECIMAL:
                return createConfiguredSqlTypeHandler("DECIMAL");
            case CHAR:
                return createConfiguredSqlTypeHandler("CHAR");
            case VARCHAR:
                return createConfiguredSqlTypeHandler("VARCHAR");
            case LONGVARCHAR:
                return createConfiguredSqlTypeHandler("LONGVARCHAR");
            // todo correctly handle date formats
            case DATE:
                return createConfiguredSqlTypeHandler("DATE");
            case TIME:
                return createConfiguredSqlTypeHandler("TIME");
            case TIMESTAMP:
                return createConfiguredSqlTypeHandler("TIMESTAMP");
            case BINARY:
                return createConfiguredSqlTypeHandler("BINARY");
            case VARBINARY:
                return createConfiguredSqlTypeHandler("VARBINARY");
            case LONGVARBINARY:
                return createConfiguredSqlTypeHandler("LONGVARBINARY");
            case NULL:
                return createConfiguredSqlTypeHandler("NULL");
            case OTHER:
                return createConfiguredSqlTypeHandler("OTHER");
            case JAVA_OBJECT:
                return createConfiguredSqlTypeHandler("JAVA_OBJECT");
            case DISTINCT:
                return createConfiguredSqlTypeHandler("DISTINCT");
            case STRUCT:
                return createConfiguredSqlTypeHandler("STRUCT");
            case ARRAY:
                return createConfiguredSqlTypeHandler("ARRAY");
            case BLOB:
                return createConfiguredSqlTypeHandler("BLOB");
            case CLOB:
                return createConfiguredSqlTypeHandler("CLOB");
            case REF:
                return createConfiguredSqlTypeHandler("REF");
            case DATALINK:
                return createConfiguredSqlTypeHandler("DATALINK");
            case BOOLEAN:
                return createConfiguredSqlTypeHandler("BOOLEAN");
            case ROWID:
                return createConfiguredSqlTypeHandler("ROWID");
            case NCHAR:
                return createConfiguredSqlTypeHandler("NCHAR");
            case NVARCHAR:
                return createConfiguredSqlTypeHandler("NVARCHAR");
            case LONGNVARCHAR:
                return createConfiguredSqlTypeHandler("LONGNVARCHAR");
            case NCLOB:
                return createConfiguredSqlTypeHandler("NCLOB");
            case SQLXML:
                return createConfiguredSqlTypeHandler("SQLXML");
        }
        return defaultSqlTypeHandler;

    }

    protected SqlTypeHandler<?> createConfiguredSqlTypeHandler(String name) {
        SqlTypeHandler<?> sqlTypeHandler = ConfigUtils.getInstanceOf(SqlTypeHandler.class, configuration, name);
        sqlTypeHandler.init(configuration);
        return sqlTypeHandler;
    }
}
