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
package org.unitils.database;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Spring bean for configuring a data source for unitils directly in an application context (without properties).
 * The spring bean references the data source that should be used and defines the schema names that this data source
 * applies to.
 * <p/>
 * For example
 * <pre><code>
 *     &lt;bean class="org.unitils.database.UnitilsDataSource"/&gt;
 *          &lt;property name"dataSource" ref="yourDataSource"/&gt;
 *          &lt;property name"schemaNames" ref="public"/&gt;
 *     &lt;bean&gt;
 * </code></pre>
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceBean {

    protected String beanName;
    /* The data source that should be used, not null */
    protected DataSource dataSource;
    /* All schemas used by the data source. The first schema name is the default one */
    protected List<String> schemaNames = new ArrayList<String>(3);
    /* The underlying DBMS implementation. E.g. 'oracle', 'db2', 'mysql', 'hsqldb', 'postgresql', 'derby' and 'mssql'. Optional, null if not defined */
    protected String dialect;
    protected boolean updateEnabled;
    protected boolean defaultDatabase;


    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource The data source that should be used, not null
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<String> getSchemaNames() {
        return schemaNames;
    }

    /**
     * The database schema that's used by the data source.
     * A schema name is case sensitive if it's surrounded by database identifier quotes (e.g. " for oracle)
     *
     * @param schemaName The schema name, not null
     */
    public void setSchemaName(String schemaName) {
        setSchemaNames(asList(schemaName));
    }

    /**
     * A set of all schemas used by the data source. The first schema name is the default one, if no schema name is
     * specified in for example a data set, this default one is used.
     * A schema name is case sensitive if it's surrounded by database identifier quotes (e.g. " for oracle)
     *
     * @param schemaNames The schema names, not null
     */
    public void setSchemaNames(List<String> schemaNames) {
        this.schemaNames = schemaNames;
    }


    public String getDialect() {
        return dialect;
    }

    /**
     * The underlying DBMS implementation.
     * E.g. 'oracle', 'db2', 'mysql', 'hsqldb', 'postgresql', 'derby' and 'mssql'.
     * Optional, null if not defined. If the dialect is set, you can define extra database configuration in the properties
     * E.g. the stored identifier case.
     *
     * @param dialect The dialect, null if not configured.
     */
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }


    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    public void setUpdateEnabled(boolean updateEnabled) {
        this.updateEnabled = updateEnabled;
    }


    public boolean isDefaultDatabase() {
        return defaultDatabase;
    }

    public void setDefaultDatabase(boolean defaultDatabase) {
        this.defaultDatabase = defaultDatabase;
    }
}
