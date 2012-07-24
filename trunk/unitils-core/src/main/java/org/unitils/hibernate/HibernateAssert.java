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
package org.unitils.hibernate;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.unitils.core.UnitilsException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * Assert class that offers assert methods for testing things that are specific to Hibernate.
 *
 * @author Timmy Maris
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateAssert {


    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct.
     *
     * @param configuration   The hibernate config, not null
     * @param session         The hibernate session, not null
     * @param databaseDialect The database dialect, not null
     */
    public static void assertMappingWithDatabaseConsistent(Configuration configuration, Session session, Dialect databaseDialect) {
        String[] script = generateDatabaseUpdateScript(configuration, session, databaseDialect);

        List<String> differences = new ArrayList<String>();
        for (String line : script) {
            // ignore constraints
            if (!line.contains("add constraint")) {
                differences.add(line);
            }
        }
        assertTrue("Found mismatches between Java objects and database tables. Applying following DDL statements to the " +
                "database should resolve the problem: \n" + formatErrorMessage(differences), differences.isEmpty());
    }


    /**
     * Generates a <code>String</code> array with DML statements based on the Hibernate mapping files.
     *
     * @param configuration   The hibernate config, not null
     * @param session         The hibernate session, not null
     * @param databaseDialect The database dialect, not null
     * @return String[] array of DDL statements that were needed to keep the database in sync with the mapping file
     */
    private static String[] generateDatabaseUpdateScript(Configuration configuration, Session session, Dialect databaseDialect) {
        try {
            DatabaseMetadata dbm = new DatabaseMetadata(session.connection(), databaseDialect);
            return configuration.generateSchemaUpdateScript(databaseDialect, dbm);
        } catch (SQLException e) {
            throw new UnitilsException("Could not retrieve database metadata", e);
        }
    }


    /**
     * Formats the given list of messages.
     *
     * @param messageParts The different parts of the message
     * @return A formatted message, containing the different message parts.
     */
    private static String formatErrorMessage(List<String> messageParts) {
        StringBuilder message = new StringBuilder();
        for (String messagePart : messageParts) {
            message.append(messagePart);
            message.append(";\n");
        }
        return message.toString();
    }

}
