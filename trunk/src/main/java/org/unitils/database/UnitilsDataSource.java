/*
 * Copyright 2006 the original author or authors.
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

import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.core.Unitils;

import javax.sql.DataSource;

/**
 * DataSource that wraps the DataSource that is provided by the {@link DatabaseModule}, but that doesn't need any context
 * information to be initialized. It will instead lookup its context information itself when the empty constructor is called.
 * <p/>
 * This class can be useful for configuring other code to make use of the <code>DataSource</code> provided by Unitils
 * with minimal effort. For example when using Spring, you could specify a bean definition named dataSource that
 * connects to the test database as follows:
 * <pre><code>
 *     &lt;bean id="dataSource" class="org.unitils.database.UnitilsDataSource"/&gt;
 * </code></pre>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsDataSource extends TransactionAwareDataSourceProxy {


    /**
     * Creates a datasource.
     */
    public UnitilsDataSource() {
        // create data source
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        DataSource dataSource = databaseModule.createDataSource();
        setTargetDataSource(dataSource);

        // register data source
        databaseModule.setDataSource(this);
    }

}
