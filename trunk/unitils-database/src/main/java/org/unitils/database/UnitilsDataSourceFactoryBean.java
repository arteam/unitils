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
package org.unitils.database;

import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

/**
 * Spring <code>FactoryBean</code> that provides access to the datasource configured in unitils.
 * <p/>
 * For example, you can define a bean in spring named 'dataSource' that connects to the default test database as follows:
 * <pre><code>
 *     &lt;bean id="dataSource" class="org.unitils.database.UnitilsDataSourceFactoryBean"/&gt;
 * </code></pre>
 * or
 * <pre><code>
 *     &lt;bean id="dataSource" class="org.unitils.database.UnitilsDataSourceFactoryBean"&gt;
 *          &lt;property name"databaseName" value="someDatabase"/&gt;
 *     &lt;bean&gt;
 * </code></pre>
 * if you want to fetch the data source of the configured database with name 'someDatabase'
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceFactoryBean implements FactoryBean {

    private String databaseName;

    /**
     * Gets the data source instance.
     *
     * @return The data source, not null
     */
    public Object getObject() throws Exception {
        return DatabaseUnitils.getDataSource(databaseName);
    }

    /**
     * Gets the type of the object provided by this <code>FactoryBean</code>, i.e. <code>DataSource</code>
     *
     * @return The type, not null
     */
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    /**
     * @return true, this is a singleton
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * @param databaseName The database name for which the data source should be retrieved, null for the default database
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
