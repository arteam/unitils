package be.ordina.unitils.hibernate;

import org.hibernate.classic.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Calls the Hibernate API to retrieve the definition for the O/R mapping.
 */
public abstract class HibernateMappingTestCase extends AbstractHibernateTestCase {

    /**
     * Object that represents a persistable Hibernate entity.
     */
    private PersistentClass persistentClass;

    protected void setUp() throws Exception {
        super.setUp();

        // clear persistable entity class
        this.persistentClass = null;
    }

    /**
     * Asserts if the given class is correctly mapped to a database table.
     *
     * @param clazz
     * @param tableName
     */
    protected final void assertClassMapping(Class clazz, String tableName) {
        persistentClass = getConfiguration().getClassMapping(clazz.getName());
        assertEquals("Table name not properly mapped to mapping file", tableName, persistentClass.getTable().getName());
    }

    /**
     * Asserts if the given field exist in the Hibernate Mapping.
     *
     * @param field
     */
    protected final void assertField(String field) {
        Property property = persistentClass.getProperty(field);
        assertEquals("Field is not defined", "property", property.getPropertyAccessorName());
    }

    /**
     * Asserts if all fields for a given class are correctly mapped in the Hibernate Mapping.
     *
     * @param clazz that needs to be represented by a Hibernate Mapping
     */
    protected final void assertAllFieldsMapped(Class clazz) {
        persistentClass = getConfiguration().getClassMapping(clazz.getName());
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (!Modifier.isStatic(field.getModifiers())) {

                    Property idProperty = persistentClass.getIdentifierProperty();
                    String idName = idProperty.getName();

                    boolean matchFound = false;
                    Iterator propertyIterator = persistentClass.getPropertyIterator();
                    while (propertyIterator.hasNext()) {
                        Property property = (Property) propertyIterator.next();
                        String name = property.getName();

                        matchFound = field.getName().equals(idName) || field.getName().equals(name);
                        if (matchFound) {
                            break;
                        }
                    }
                    assertTrue("Field '" + field.getName() + "' not properly mapped.", matchFound);
                }
            } catch (Exception e) {
                // Ignore field if exception is thrown, eg: SecurityException
            }
        }
    }

    /**
     * Asserts the differences between the mapping documents and the current database schema.
     *
     * @throws Exception
     */
    protected final void assertMappingToDatabase() throws Exception {
        String[] script = generateScript();

        List<String> differences = new ArrayList<String>();
        for (String line : script) {
            // ignore constraints
            if (line.indexOf("add constraint") == -1) {
                differences.add(line);
            }
        }

        assertTrue(differences.toString(), differences.isEmpty());
    }

    /**
     * Generates a <code>String</code> array with DML statements based on the Hibernate mapping files.
     *
     * @return String[] array of DDL statements that were needed to keep the database in sync with the mapping file
     * @throws Exception
     */
    private String[] generateScript() throws Exception {
        Session session = getSessionFactory().openSession();
        try {
            Dialect dialect = getDatabaseDialect();
            DatabaseMetadata dbm = new DatabaseMetadata(session.connection(), dialect);

            return getConfiguration().generateSchemaUpdateScript(dialect, dbm);
        } finally {
            session.close();
        }

    }
}
