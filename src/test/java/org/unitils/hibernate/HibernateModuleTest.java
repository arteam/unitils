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
package org.unitils.hibernate;

import static org.easymock.EasyMock.expect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.Mock;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateSession;
import org.unitils.hibernate.annotation.HibernateTest;

/**
 * Test class for the HibernateModule
 */
@SuppressWarnings({"UnusedDeclaration"})
public class HibernateModuleTest extends UnitilsJUnit3 {

    /**
     * Tested object
     */
    private HibernateModule hibernateModule;

    @Mock
    private Configuration mockHibernateConfiguration;

    @Mock
    private SessionFactory mockHibernateSessionFactory;

    @Mock
    private org.hibernate.classic.Session mockHibernateSession;

    /**
     * Fake unit test
     */
    protected HbnTest hbnTest;

    /**
     * Test fixture. Intialize HibernateModule, that creates a mock Hibernate Configuration and uses a mock
     * database connection.
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        hibernateModule = new HibernateModule() {

            @Override
            protected Configuration createHibernateConfiguration() {
                return mockHibernateConfiguration;
            }

        };

        hbnTest = new HbnTest();
    }

    /**
     * Tests whether the isHibernateTest returns true when passing a class annotated with @HibernateTest
     */
    public void testIsHibernateTest() {

        assertTrue(hibernateModule.isHibernateTest(HbnTest.class));
    }

    /**
     * Tests the configuration of Hibernate: Verifies that the session factory is built and a session is opened, and
     * that methods on the test object that are annotated with @HibernateConfiguration are called with the correct
     * Hibernate Configuration object
     */
    public void testConfigureHibernate() {

        expect(mockHibernateConfiguration.buildSessionFactory()).andStubReturn(mockHibernateSessionFactory);
        expect(mockHibernateConfiguration.addProperties(null)).andStubReturn(mockHibernateConfiguration);
        expect(mockHibernateSessionFactory.openSession()).andStubReturn(mockHibernateSession);
        replay();

        hibernateModule.configureHibernate(hbnTest);

        assertSame(mockHibernateConfiguration, hbnTest.getConfiguration());
    }

    /**
     * Tests hibernate session injection: A Hibernate Session must be retrieved from the Hibernate SessionManager
     * and injected into the testobject's fields and methods that are annotated with @HibernateSession
     */
    public void testInjectHibernateSession() {

        expect(mockHibernateConfiguration.buildSessionFactory()).andStubReturn(mockHibernateSessionFactory);
        expect(mockHibernateConfiguration.addProperties(null)).andStubReturn(mockHibernateConfiguration);
        expect(mockHibernateSession.isOpen()).andReturn(false);
        expect(mockHibernateSessionFactory.openSession()).andStubReturn(mockHibernateSession);
        replay();

        hibernateModule.configureHibernate(hbnTest);
        hibernateModule.injectHibernateSession(hbnTest);

        assertSame(mockHibernateSession, hbnTest.getSessionMethod());
        assertSame(mockHibernateSession, hbnTest.getSessionField());
    }

    /**
     * Tests the correct closing of the active Hibernate Session
     */
    public void testCloseSession() {
        expect(mockHibernateConfiguration.buildSessionFactory()).andStubReturn(mockHibernateSessionFactory);
        expect(mockHibernateConfiguration.addProperties(null)).andStubReturn(mockHibernateConfiguration);
        expect(mockHibernateSessionFactory.openSession()).andStubReturn(mockHibernateSession);
        expect(mockHibernateSession.isOpen()).andReturn(true);
        expect(mockHibernateSession.close()).andReturn(null);
        replay();

        hibernateModule.configureHibernate(hbnTest);
        hibernateModule.getCurrentSession();
        hibernateModule.closeHibernateSession();
    }

    /**
     * Fake test class. Contains field and method annotated with @HibernateSession (session should be injected) and
     * a method annotated with @HibernateConfiguration (should be called when configuring Hibernate)
     */
    @HibernateTest
    public static class HbnTest {

        private Configuration configuration;

        @HibernateSession
        private Session sessionField;

        private Session sessionMethod;

        @HibernateConfiguration
        public void configureHibernate(Configuration configuration) {
            this.configuration = configuration;
        }

        @HibernateSession
        public void afterCreateHibernateSession(Session session) {
            this.sessionMethod = session;
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public Session getSessionField() {
            return sessionField;
        }

        public Session getSessionMethod() {
            return sessionMethod;
        }
    }

}
