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

package org.unitilsnew.database;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class UnitilsDataSourceFactoryBeanIntegrationTest {

    /* Tested object */
    private UnitilsDataSourceFactoryBean unitilsDataSourceFactoryBean;


    @Before
    public void initialize() {
        unitilsDataSourceFactoryBean = new UnitilsDataSourceFactoryBean();
    }


    @Test
    public void databaseName() throws Exception {
        unitilsDataSourceFactoryBean.setDatabaseName("databaseName");
        String result = unitilsDataSourceFactoryBean.getDatabaseName();

        assertEquals("databaseName", result);
    }

    @Test
    public void getObjectType() throws Exception {
        Class<?> result = unitilsDataSourceFactoryBean.getObjectType();
        assertEquals(DataSource.class, result);
    }

    @Test
    public void isSingleton() throws Exception {
        boolean result = unitilsDataSourceFactoryBean.isSingleton();
        assertTrue(result);
    }

    @Test
    public void getObject() throws Exception {
        Object result = unitilsDataSourceFactoryBean.getObject();
        assertTrue(result instanceof DataSource);
    }
}
