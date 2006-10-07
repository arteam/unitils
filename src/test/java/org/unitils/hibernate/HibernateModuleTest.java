package org.unitils.hibernate;

import org.unitils.UnitilsJUnit3;
import org.unitils.easymock.annotation.Mock;
import org.unitils.hibernate.annotation.HibernateTest;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.AfterCreateHibernateSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Interceptor;

import static org.easymock.EasyMock.*;
import static org.unitils.easymock.EasyMockModule.*;

import java.sql.Connection;

/**
 */
public class HibernateModuleTest extends UnitilsJUnit3 {

    private HibernateModule hibernateModule;

    @Mock
    private Configuration mockHibernateConfiguration;

    @Mock
    private SessionFactory mockHibernateSessionFactory;

    @Mock
    private org.hibernate.classic.Session mockHibernateSession;

    @Mock
    private Connection mockConnection;

    protected HbnTest hbnTest;

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

        expect(mockHibernateConfiguration.buildSessionFactory()).andStubReturn(mockHibernateSessionFactory);
        expect(mockHibernateSessionFactory.openSession(mockConnection)).andStubReturn(mockHibernateSession);
        expect(mockHibernateSessionFactory.openSession(null, (Interceptor)notNull()));

        hbnTest = new HbnTest();
    }

    public void testIsHibernateTest() {
        assertTrue(hibernateModule.isHibernateTest(HbnTest.class));
    }

    public void testConfigureHibernate() {
        replay();

        hibernateModule.configureHibernate(hbnTest);

        assertSame(mockHibernateConfiguration, hbnTest.getConfiguration());
    }

    public void testCreateSession() {
        replay();

        hibernateModule.configureHibernate(hbnTest);
        hibernateModule.createHibernateSession(hbnTest);

        assertSame(mockHibernateSession, hbnTest.getSession());
    }

    @HibernateTest
    public static class HbnTest {

        private Configuration configuration;

        private Session session;

        @HibernateConfiguration
        public void configureHibernate(Configuration configuration) {
            this.configuration = configuration;
        }

        @AfterCreateHibernateSession
        public void afterCreateHibernateSession(Session session) {
            this.session = session;
        }

        public Configuration getConfiguration() {
            return configuration;
        }

        public Session getSession() {
            return session;
        }
    }

}
