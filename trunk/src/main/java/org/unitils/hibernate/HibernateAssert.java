package org.unitils.hibernate;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.unitils.core.Unitils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Assert class that offers assert methods for testing things that are specific to Hibernate.
 */
public class HibernateAssert {

    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct. This method assumes
     * that the <code>HibernateSessionManager</code> is correctly configured. This is automatically the case when the
     * <code>BaseHibernateTestCase</code> is used as test superclass.
     */
    public static void assertMappingToDatabase() {
        HibernateModule hibernateModule = Unitils.getModulesRepository().getFirstModule(HibernateModule.class);
        Configuration configuration = hibernateModule.getHibernateConfiguration();
        Session session = hibernateModule.getCurrentSession();

        assertMappingToDatabase(configuration, session);
    }

    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct. This method does the
     * same as <code>assertMappingToDatabase</code> without parameters, but can also be used if the
     * <code>HibernateSessionManager</code> is not used for unit test session management.
     *
     * @param configuration
     * @param session
     */
    public static void assertMappingToDatabase(Configuration configuration, Session session) {
        String[] script = generateScript(configuration, session);

        List<String> differences = new ArrayList<String>();
        for (String line : script) {
            // ignore constraints
            if (line.indexOf("add constraint") == -1) {
                differences.add(line);
            }
        }
        Assert.assertTrue("Found mismatches between Java objects and database tables. " +
                "Applying following DDL statements to the database should resolve the problem: \n" +
                formatMessage(differences), differences.isEmpty());
    }

    /**
     * Formats the given list of messages.
     *
     * @param messageParts The different parts of the message
     * @return A formatted message, containing the different message parts.
     */
    private static String formatMessage(List<String> messageParts) {
        StringBuffer message = new StringBuffer();
        for (String messagePart : messageParts) {
            message.append(messagePart);
            message.append(";\n");
        }
        return message.toString();
    }

    /**
     * Generates a <code>String</code> array with DML statements based on the Hibernate mapping files.
     *
     * @param configuration
     * @return String[] array of DDL statements that were needed to keep the database in sync with the mapping file
     */
    private static String[] generateScript(Configuration configuration, Session session) {
        try {
            Dialect dialect = getDatabaseDialect(configuration);
            DatabaseMetadata dbm = new DatabaseMetadata(session.connection(), dialect);
            return configuration.generateSchemaUpdateScript(dialect, dbm);
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve database metadata", e);
        }
    }

    /**
     * Gets the database dialect from the Hibernate <code>Configuration</code.
     *
     * @param configuration
     * @return Dialect
     */
    private static Dialect getDatabaseDialect(Configuration configuration) {
        String dialectClassName = configuration.getProperty("hibernate.dialect");
        if (StringUtils.isEmpty(dialectClassName)) {
            throw new IllegalArgumentException("Property hibernate.dialect not specified");
        }
        try {
            return (Dialect) Class.forName(dialectClassName).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not instantiate dialect class " + dialectClassName, e);
        }
    }

}
