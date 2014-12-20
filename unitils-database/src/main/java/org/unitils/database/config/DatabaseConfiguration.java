/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.database.config;

import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class DatabaseConfiguration {

    protected String databaseName;
    protected String dialect;
    protected String driverClassName;
    protected String url;
    protected String userName;
    protected String password;
    protected String defaultSchemaName;
    protected List<String> schemaNames;
    protected boolean updateDisabled;
    protected boolean defaultDatabase;


    public DatabaseConfiguration(String databaseName, String dialect, String driverClassName, String url, String userName, String password, String defaultSchemaName, List<String> schemaNames, boolean updateDisabled, boolean defaultDatabase) {
        this.databaseName = databaseName;
        this.dialect = dialect;
        this.driverClassName = driverClassName;
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.defaultSchemaName = defaultSchemaName;
        this.schemaNames = schemaNames;
        this.updateDisabled = updateDisabled;
        this.defaultDatabase = defaultDatabase;
    }


    public String getDatabaseName() {
        return databaseName;
    }

    public String getDialect() {
        return dialect;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDefaultSchemaName() {
        return defaultSchemaName;
    }

    public List<String> getSchemaNames() {
        return schemaNames;
    }

    public boolean isUpdateDisabled() {
        return updateDisabled;
    }

    public boolean isDefaultDatabase() {
        return defaultDatabase;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (databaseName != null) {
            stringBuilder.append("database name: '");
            stringBuilder.append(databaseName);
            stringBuilder.append("', ");
        }
        stringBuilder.append("driver class name: ");
        if (driverClassName == null) {
            stringBuilder.append("<null>");
        } else {
            stringBuilder.append("'");
            stringBuilder.append(driverClassName);
            stringBuilder.append("'");
        }
        stringBuilder.append(", url: ");
        if (url == null) {
            stringBuilder.append("<null>");
        } else {
            stringBuilder.append("'");
            stringBuilder.append(url);
            stringBuilder.append("'");
        }
        stringBuilder.append(", user name: ");
        if (userName == null) {
            stringBuilder.append("<null>");
        } else {
            stringBuilder.append("'");
            stringBuilder.append(userName);
            stringBuilder.append("'");
        }
        stringBuilder.append(", password: <not shown>, default schema name: ");
        if (defaultSchemaName == null) {
            stringBuilder.append("<null>");
        } else {
            stringBuilder.append("'");
            stringBuilder.append(defaultSchemaName);
            stringBuilder.append("'");
        }
        stringBuilder.append(", schema names: ");
        if (schemaNames == null) {
            stringBuilder.append("<null>");
        } else {
            stringBuilder.append(schemaNames);
        }
        return stringBuilder.toString();
    }
}
