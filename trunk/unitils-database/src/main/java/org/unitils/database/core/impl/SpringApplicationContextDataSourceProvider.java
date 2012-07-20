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
package org.unitils.database.core.impl;

import org.springframework.context.ApplicationContext;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSourceBean;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.core.DataSourceProvider;
import org.unitils.database.core.DataSourceWrapper;
import org.unitilsnew.core.spring.SpringTestManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class SpringApplicationContextDataSourceProvider implements DataSourceProvider {

    protected SpringTestManager springTestManager;

    protected String currentApplicationContextId;
    protected Map<String, DataSourceWrapper> currentDataSourceWrappers = new HashMap<String, DataSourceWrapper>(3);


    public SpringApplicationContextDataSourceProvider(SpringTestManager springTestManager) {
        this.springTestManager = springTestManager;
    }


    public List<String> getDatabaseNames() {
        try {
            Map<String, DataSourceWrapper> dataSourceWrappers = getDataSourceWrappers();
            return new ArrayList<String>(dataSourceWrappers.keySet());

        } catch (Exception e) {
            throw new UnitilsException("Unable to get database names from application context.", e);
        }
    }

    public DataSourceWrapper getDataSourceWrapper(String databaseName) {
        try {
            Map<String, DataSourceWrapper> dataSourceWrappers = getDataSourceWrappers();
            if (isBlank(databaseName)) {
                return getDefaultDataSourceWrapper(dataSourceWrappers);
            }
            DataSourceWrapper dataSourceWrapper = dataSourceWrappers.get(databaseName);
            if (dataSourceWrapper == null) {
                throw new UnitilsException("No bean with id '" + databaseName + "' of type UnitilsDataSourceBean found in test application context.");
            }
            return dataSourceWrapper;

        } catch (Exception e) {
            String message = "Unable to get ";
            if (isBlank(databaseName)) {
                message += "default data source";
            } else {
                message += "data source for database name '" + databaseName + "'";
            }
            message += " from application context.";
            throw new UnitilsException(message, e);
        }
    }


    @SuppressWarnings("unchecked")
    protected Map<String, DataSourceWrapper> getDataSourceWrappers() {
        ApplicationContext applicationContext = springTestManager.getApplicationContext();
        if (applicationContext == null) {
            throw new UnitilsException("No test application context found.");
        }
        String applicationContextId = applicationContext.getId();
        if (!applicationContextId.equals(currentApplicationContextId)) {
            currentApplicationContextId = applicationContextId;
            currentDataSourceWrappers = createDataSourceWrappers(applicationContext);
        }
        return currentDataSourceWrappers;
    }

    protected DataSourceWrapper getDefaultDataSourceWrapper(Map<String, DataSourceWrapper> dataSourceWrappers) {
        if (dataSourceWrappers.size() == 1) {
            return dataSourceWrappers.values().iterator().next();
        }
        DataSourceWrapper defaultDataSourceWrapper = null;
        for (DataSourceWrapper dataSourceWrapper : dataSourceWrappers.values()) {
            if (dataSourceWrapper.getDatabaseConfiguration().isDefaultDatabase()) {
                if (defaultDataSourceWrapper != null) {
                    throw new UnitilsException("Unable to determine default database. More than one bean of type UnitilsDataSourceBean found in test application context that is marked as default database. Only one of these beans can have the defaultDatabase property set to true.");
                }
                defaultDataSourceWrapper = dataSourceWrapper;
            }
        }
        if (defaultDataSourceWrapper == null) {
            throw new UnitilsException("Unable to determine default database. More than one bean of type UnitilsDataSourceBean found in test application context. Please mark one of these beans as default database by setting its defaultDatabase property to true.");
        }
        return defaultDataSourceWrapper;
    }


    @SuppressWarnings("unchecked")
    protected Map<String, DataSourceWrapper> createDataSourceWrappers(ApplicationContext applicationContext) {
        Map<String, DataSourceWrapper> dataSourceWrappers = new HashMap<String, DataSourceWrapper>(3);

        Map<String, UnitilsDataSourceBean> unitilsDataSourceBeans = applicationContext.getBeansOfType(UnitilsDataSourceBean.class);
        if (unitilsDataSourceBeans.isEmpty()) {
            throw new UnitilsException("No beans of type UnitilsDataSourceBean found in test application context.");
        }
        for (Map.Entry<String, UnitilsDataSourceBean> entry : unitilsDataSourceBeans.entrySet()) {
            String databaseName = entry.getKey();
            UnitilsDataSourceBean unitilsDataSourceBean = entry.getValue();

            boolean defaultDatabase = unitilsDataSourceBean.isDefaultDatabase();
            if (unitilsDataSourceBeans.size() == 1) {
                defaultDatabase = true;
            }
            DataSourceWrapper dataSourceWrapper = createDataSourceWrapper(databaseName, defaultDatabase, unitilsDataSourceBean);
            dataSourceWrappers.put(databaseName, dataSourceWrapper);
        }
        return dataSourceWrappers;
    }


    protected DataSourceWrapper createDataSourceWrapper(String databaseName, boolean defaultDatabase, UnitilsDataSourceBean unitilsDataSourceBean) {
        DataSource dataSource = unitilsDataSourceBean.getDataSource();
        if (dataSource == null) {
            throw new UnitilsException("No dataSource configured for UnitilsDataSourceBean.");
        }
        DatabaseConfiguration databaseConfiguration = createDatabaseConfiguration(databaseName, defaultDatabase, unitilsDataSourceBean);
        return new DataSourceWrapper(dataSource, databaseConfiguration);
    }

    protected DatabaseConfiguration createDatabaseConfiguration(String databaseName, boolean defaultDatabase, UnitilsDataSourceBean unitilsDataSourceBean) {
        List<String> schemaNames = unitilsDataSourceBean.getSchemaNames();
        String dialect = unitilsDataSourceBean.getDialect();
        boolean updateDisabled = unitilsDataSourceBean.isUpdateEnabled();

        String defaultSchemaName = null;
        if (!schemaNames.isEmpty()) {
            defaultSchemaName = schemaNames.get(0);
        }
        return new DatabaseConfiguration(databaseName, dialect, null, null, null, null, defaultSchemaName, schemaNames, updateDisabled, defaultDatabase);
    }
}
