package org.unitils.hibernate;

import static org.easymock.EasyMock.expect;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.LenientMock;
import org.unitils.hibernate.annotation.HibernateSession;
import org.unitils.hibernate.annotation.HibernateConfiguration;
import org.unitils.hibernate.annotation.HibernateTest;

import java.sql.Connection;

/**
 * todo javadoc
 */
@SuppressWarnings({"UnusedDeclaration"})
public class HibernateModuleTest extends UnitilsJUnit3 {

    private HibernateModule hibernateModule;

    @LenientMock
    private Configuration mockHibernateConfiguration;

    @LenientMock
    private SessionFactory mockHibernateSessionFactory;

    @LenientMock
    private org.hibernate.classic.Session mockHibernateSession;

    @LenientMock
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

        hbnTest = new HbnTest();
    }


    public void testIsHibernateTest() {

        assertTrue(hibernateModule.isHibernateTest(HbnTest.class));
    }


    public void testConfigureHibernate() {

        expect(mockHibernateConfiguration.buildSessionFactory()).andStubReturn(mockHibernateSessionFactory);
        expect(mockHibernateSessionFactory.openSession(mockConnection)).andStubReturn(mockHibernateSession);
        replay();

        hibernateModule.configureHibernate(hbnTest);

        assertSame(mockHibernateConfiguration, hbnTest.getConfiguration());
    }


    public void testCreateSession() {

        expect(mockHibernateConfiguration.buildSessionFactory()).andStubReturn(mockHibernateSessionFactory);
        expect(mockHibernateSessionFactory.openSession(mockConnection)).andStubReturn(mockHibernateSession);
        replay();

        hibernateModule.configureHibernate(hbnTest);
        hibernateModule.injectHibernateSession(hbnTest);

        assertSame(mockHibernateSession, hbnTest.getSession());
    }


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


    @HibernateTest
    public static class HbnTest {

        private Configuration configuration;

        private Session session;

        @HibernateConfiguration
        public void configureHibernate(Configuration configuration) {
            this.configuration = configuration;
        }

        @HibernateSession
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
