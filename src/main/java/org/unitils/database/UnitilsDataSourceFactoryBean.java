/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.database;

import org.springframework.beans.factory.FactoryBean;
import org.unitils.core.Unitils;

import javax.sql.DataSource;

/**
 * DataSource factory bean to that lets the {@link DatabaseModule} create the data source.
 * <p/>
 * For example you could specify a bean definition named dataSource that connects to the test database as follows:
 * <pre><code>
 *     &lt;bean id="dataSource" class="org.unitils.database.UnitilsDataSourceFactoryBean"/&gt;
 * </code></pre>
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceFactoryBean implements FactoryBean {


    /**
     * Gets the data source instance.
     *
     * @return The data source, not null
     */
    public Object getObject() throws Exception {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        return databaseModule.getDataSource();
    }


    /**
     * Gets the type of the instance, i.e. <code>DataSource</code>
     *
     * @return The type, not null
     */
    public Class<?> getObjectType() {
        return DataSource.class;
    }


    /**
     * @return false, it is not a singleton
     */
    public boolean isSingleton() {
        return true;
    }
}
