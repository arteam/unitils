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

package org.unitils.database.core.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.core.spring.SpringTestManager;
import org.unitils.database.UnitilsDataSourceBean;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class SpringApplicationContextDataSourceProviderGetDatabaseNamesTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringApplicationContextDataSourceProvider springApplicationContextDataSourceProvider;

    private Mock<SpringTestManager> springTestManagerMock;
    private Mock<ApplicationContext> applicationContextMock;
    @Dummy
    private DataSource dataSource;

    private Map<String, UnitilsDataSourceBean> unitilsDataSourceBeans;

    @Before
    public void initialize() {
        springApplicationContextDataSourceProvider = new SpringApplicationContextDataSourceProvider(springTestManagerMock.getMock());

        springTestManagerMock.returns(applicationContextMock).getApplicationContext();

        UnitilsDataSourceBean unitilsDataSourceBean1 = new UnitilsDataSourceBean();
        unitilsDataSourceBean1.setDataSource(dataSource);
        UnitilsDataSourceBean unitilsDataSourceBean2 = new UnitilsDataSourceBean();
        unitilsDataSourceBean2.setDataSource(dataSource);

        unitilsDataSourceBeans = new HashMap<String, UnitilsDataSourceBean>();
        unitilsDataSourceBeans.put("name 1", unitilsDataSourceBean1);
        unitilsDataSourceBeans.put("name 2", unitilsDataSourceBean2);

        applicationContextMock.returns("1").getId();
        applicationContextMock.returns(unitilsDataSourceBeans).getBeansOfType(UnitilsDataSourceBean.class);
    }


    @Test
    public void getDatabaseNames() throws Exception {
        List<String> result = springApplicationContextDataSourceProvider.getDatabaseNames();

        assertEquals(asList("name 1", "name 2"), result);
    }

    @Test
    public void exceptionWhenNoUnitilsDataSourceBeansFound() throws Exception {
        unitilsDataSourceBeans.clear();
        try {
            springApplicationContextDataSourceProvider.getDatabaseNames();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get database names from application context.\n" +
                    "Reason: No beans of type UnitilsDataSourceBean found in test application context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoApplicationContextFound() {
        springTestManagerMock.onceReturns(null).getApplicationContext();
        try {
            springApplicationContextDataSourceProvider.getDatabaseNames();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get database names from application context.\n" +
                    "Reason: No test application context found.", e.getMessage());
        }

    }
}
