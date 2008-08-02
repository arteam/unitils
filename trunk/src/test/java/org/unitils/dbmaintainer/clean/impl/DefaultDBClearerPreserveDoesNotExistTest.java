/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.dbmaintainer.clean.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.DBClearer;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.*;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Test class for the {@link DBClearer} with preserve items configured, but some items do not exist.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBClearerPreserveDoesNotExistTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerPreserveDoesNotExistTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DefaultDBClearer defaultDbClearer;

    /* The unitils configuration */
    private Properties configuration;

    /* The sql statement handler */
    private SQLHandler sqlHandler;


    /**
     * Configures the tested object.
     * <p/>
     * todo Test_trigger_Preserve Test_CASE_Trigger_Preserve
     */
    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationLoader().loadConfiguration();
        sqlHandler = new DefaultSQLHandler(dataSource);
        defaultDbClearer = new DefaultDBClearer();
    }


    /**
     * Test for schemas to preserve that do not exist.
     */
    @Test(expected = UnitilsException.class)
    public void testClearSchemas_schemasToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPKEY_PRESERVE_SCHEMAS, "unexisting_schema1, unexisting_schema2");
        defaultDbClearer.init(configuration, sqlHandler);
    }


    /**
     * Test for tables to preserve that do not exist.
     */
    @Test(expected = UnitilsException.class)
    public void testClearSchemas_tablesToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPKEY_PRESERVE_TABLES, "unexisting_table1, unexisting_table2");
        defaultDbClearer.init(configuration, sqlHandler);
    }


    /**
     * Test for views to preserve that do not exist.
     */
    @Test(expected = UnitilsException.class)
    public void testClearSchemas_viewsToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPKEY_PRESERVE_VIEWS, "unexisting_view1, unexisting_view2");
        defaultDbClearer.init(configuration, sqlHandler);
    }


    /**
     * Test for materialized views to preserve that do not exist.
     */
    @Test(expected = UnitilsException.class)
    public void testClearSchemas_materializedViewsToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPKEY_PRESERVE_MATERIALIZED_VIEWS, "unexisting_materializedView1, unexisting_materializedView2");
        defaultDbClearer.init(configuration, sqlHandler);
    }


    /**
     * Test for sequences to preserve that do not exist.
     */
    @Test
    public void testClearSchemas_sequencesToPreserveDoNotExist() throws Exception {
        if (!getDefaultDbSupport(configuration, sqlHandler).supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        try {
            configuration.setProperty(PROPKEY_PRESERVE_SEQUENCES, "unexisting_sequence1, unexisting_sequence2");
            defaultDbClearer.init(configuration, sqlHandler);
            fail("UnitilsException expected.");
        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test for synonyms to preserve that do not exist.
     */
    @Test
    public void testClearSchemas_synonymsToPreserveDoNotExist() throws Exception {
        if (!getDefaultDbSupport(configuration, sqlHandler).supportsSynonyms()) {
            logger.warn("Current dialect does not support synonyms. Skipping test.");
            return;
        }
        try {
            configuration.setProperty(PROPKEY_PRESERVE_SYNONYMS, "unexisting_synonym1, unexisting_synonym2");
            defaultDbClearer.init(configuration, sqlHandler);
            fail("UnitilsException expected.");
        } catch (UnitilsException e) {
            // expected
        }

    }
}
