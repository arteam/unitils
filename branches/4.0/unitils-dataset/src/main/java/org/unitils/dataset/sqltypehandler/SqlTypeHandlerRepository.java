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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SqlTypeHandlerRepository {

    protected Properties configuration;

    protected Map<Integer, SqlTypeHandler<?>> sqlTypeHandlers = new HashMap<Integer, SqlTypeHandler<?>>();


    public void init(Properties configuration) {
        this.configuration = configuration;
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
        SqlTypeHandler<?> sqlTypeHandler = ConfigUtils.getInstanceOf(SqlTypeHandler.class, configuration, "" + sqlType);
        sqlTypeHandler.init(configuration);
        return sqlTypeHandler;
    }
}
