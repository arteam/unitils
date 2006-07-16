package be.ordina.unitils.testing.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.dbunit.operation.DatabaseOperation;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import junit.framework.Assert;

/**
 *
 */
public class HibernateAssert {

    public static void assertMappingToDatabase() {
        Configuration configuration = HibernateSessionManager.getConfiguration();
        Session session = HibernateSessionManager.getSession();

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
     * @return String[] array of DDL statements that were needed to keep the database in sync with the mapping file
     * @param configuration
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
     * @return Dialect
     * @param configuration
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
