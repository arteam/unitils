package org.unitils.hibernate;

import static org.easymock.EasyMock.expect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.LenientMock;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateSession;
import org.unitils.hibernate.annotation.HibernateTest;

import java.sql.Connection;

/**
 * Test class for the HibernateModule
 */
@SuppressWarnings({"UnusedDeclaration"})
public class HibernateModuleTest extends UnitilsJUnit3 {

    /**
     * Tested object
     */
    private HibernateModule hibernateModule;

    @LenientMock
    private Configuration mockHibernateConfiguration;

    @LenientMock
    private SessionFactory mockHibernateSessionFactory;

    @LenientMock
    private org.hibernate.classic.Session mockHibernateSession;

    @LenientMock
    private Connection mockConnection;

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

            @Override
            protected Connection getConnection() {
                return mockConnection;
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
        expect(mockHibernateSessionFactory.openSession(mockConnection)).andStubReturn(mockHibernateSession);
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
        expect(mockHibernateSession.isOpen()).andReturn(false);
        expect(mockHibernateSessionFactory.openSession(mockConnection)).andStubReturn(mockHibernateSession);
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
        expect(mockHibernateSessionFactory.openSession(mockConnection)).andStubReturn(mockHibernateSession);
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
