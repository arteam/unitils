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
package org.unitils.database.transaction.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.core.spring.SpringTestManager;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class SpringApplicationContextTransactionProviderGetPlatformTransactionManagerTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringApplicationContextTransactionProvider springApplicationContextTransactionProvider;

    private Mock<SpringTestManager> springTestManagerMock;
    private Mock<DefaultTransactionProvider> defaultTransactionProviderMock;
    private Mock<ApplicationContext> applicationContextMock;
    @Dummy
    private PlatformTransactionManager platformTransactionManager1;
    @Dummy
    private PlatformTransactionManager platformTransactionManager2;
    @Dummy
    private DataSource dataSource;

    private Map<String, PlatformTransactionManager> platformTransactionManagers = new HashMap<String, PlatformTransactionManager>();


    @Before
    public void initialize() {
        springApplicationContextTransactionProvider = new SpringApplicationContextTransactionProvider(springTestManagerMock.getMock(), defaultTransactionProviderMock.getMock());

        springTestManagerMock.returns(applicationContextMock).getApplicationContext();
        applicationContextMock.returns(platformTransactionManagers).getBeansOfType(PlatformTransactionManager.class);
    }


    @Test
    public void namedPlatformTransactionManager() {
        platformTransactionManagers.put("name 1", platformTransactionManager1);
        platformTransactionManagers.put("name 2", platformTransactionManager2);

        PlatformTransactionManager result = springApplicationContextTransactionProvider.getPlatformTransactionManager("name 1", dataSource);

        assertSame(platformTransactionManager1, result);
    }

    @Test
    public void defaultWhenNameIsNull() {
        platformTransactionManagers.put("name", platformTransactionManager1);

        PlatformTransactionManager result = springApplicationContextTransactionProvider.getPlatformTransactionManager(null, dataSource);

        assertSame(platformTransactionManager1, result);
    }

    @Test
    public void defaultWhenNameIsBlank() {
        platformTransactionManagers.put("name", platformTransactionManager1);

        PlatformTransactionManager result = springApplicationContextTransactionProvider.getPlatformTransactionManager("", dataSource);

        assertSame(platformTransactionManager1, result);
    }

    @Test
    public void fallbackToDefaultTransactionProviderWhenNoTransactionManagersFoundInApplicationContext() {
        defaultTransactionProviderMock.returns(platformTransactionManager2).getPlatformTransactionManager("name", dataSource);

        PlatformTransactionManager result = springApplicationContextTransactionProvider.getPlatformTransactionManager("name", dataSource);

        assertSame(platformTransactionManager2, result);
    }

    @Test
    public void exceptionWhenDefaultButMoreThanOneTransactionManagerFound() {
        platformTransactionManagers.put("name 1", platformTransactionManager1);
        platformTransactionManagers.put("name 2", platformTransactionManager2);
        try {
            springApplicationContextTransactionProvider.getPlatformTransactionManager(null, dataSource);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get default platform transaction manager from application context. More than one bean of type PlatformTransactionManager found in application context. Please specify the id of the transaction manager explicitly.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTransactionManagerNotFound() {
        platformTransactionManagers.put("name", platformTransactionManager1);
        try {
            springApplicationContextTransactionProvider.getPlatformTransactionManager("xxx", dataSource);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get platform transaction manager from application context. No bean of type PlatformTransactionManager with id 'xxx' found in application context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoApplicationContext() {
        springTestManagerMock.onceReturns(null).getApplicationContext();
        try {
            springApplicationContextTransactionProvider.getPlatformTransactionManager(null, dataSource);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get platform transaction manager from application context. No test application context found.", e.getMessage());
        }
    }
}
