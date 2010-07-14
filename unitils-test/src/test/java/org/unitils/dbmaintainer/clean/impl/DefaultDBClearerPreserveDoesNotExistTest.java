/*
 * Copyright Unitils.org
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
import org.dbmaintain.dbsupport.DbSupport;
import org.dbmaintain.util.DbMaintainException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;

import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.fail;
import static org.unitils.database.DatabaseUnitils.clearDatabase;
import static org.unitils.database.DatabaseUnitils.getDbSupports;
import static org.unitils.testutil.TestUnitilsConfiguration.reinitializeUnitils;
import static org.unitils.testutil.TestUnitilsConfiguration.resetUnitils;

/**
 * Test class for clearing the database with preserve items configured, but some items do not exist.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBClearerPreserveDoesNotExistTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerPreserveDoesNotExistTest.class);

    private DbSupport defaultDbSupport;
    private Properties configuration;


    /**
     * Configures the tested object.
     * <p/>
     * todo Test_trigger_Preserve Test_CASE_Trigger_Preserve
     */
    @Before
    public void setUp() throws Exception {
        configuration = new ConfigurationLoader().loadConfiguration();
        defaultDbSupport = getDbSupports().getDefaultDbSupport();
    }

    @After
    public void tearDown() throws Exception {
        resetUnitils();
    }


    /**
     * Test for schemas to preserve that do not exist.
     */
    @Test(expected = DbMaintainException.class)
    public void testClearDatabase_schemasToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPERTY_PRESERVE_SCHEMAS, "unexisting_schema1, unexisting_schema2");
        reinitializeUnitils(configuration);

        clearDatabase();
    }


    /**
     * Test for tables to preserve that do not exist.
     */
    @Test(expected = DbMaintainException.class)
    public void testClearDatabase_tablesToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPERTY_PRESERVE_TABLES, "unexisting_table1, unexisting_table2");
        reinitializeUnitils(configuration);

        clearDatabase();
    }


    /**
     * Test for views to preserve that do not exist.
     */
    @Test(expected = DbMaintainException.class)
    public void testClearDatabase_viewsToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPERTY_PRESERVE_VIEWS, "unexisting_view1, unexisting_view2");
        reinitializeUnitils(configuration);

        clearDatabase();
    }


    /**
     * Test for materialized views to preserve that do not exist.
     */
    @Test(expected = DbMaintainException.class)
    public void testClearDatabase_materializedViewsToPreserveDoNotExist() throws Exception {
        configuration.setProperty(PROPERTY_PRESERVE_MATERIALIZED_VIEWS, "unexisting_materializedView1, unexisting_materializedView2");
        reinitializeUnitils(configuration);

        clearDatabase();
    }


    /**
     * Test for sequences to preserve that do not exist.
     */
    @Test
    public void testClearDatabase_sequencesToPreserveDoNotExist() throws Exception {
        if (!defaultDbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        try {
            configuration.setProperty(PROPERTY_PRESERVE_SEQUENCES, "unexisting_sequence1, unexisting_sequence2");
            reinitializeUnitils(configuration);

            clearDatabase();
            fail("DbMaintainException expected.");
        } catch (DbMaintainException e) {
            // expected
        }
    }


    /**
     * Test for synonyms to preserve that do not exist.
     */
    @Test
    public void testClearDatabase_synonymsToPreserveDoNotExist() throws Exception {
        if (!defaultDbSupport.supportsSynonyms()) {
            logger.warn("Current dialect does not support synonyms. Skipping test.");
            return;
        }
        try {
            configuration.setProperty(PROPERTY_PRESERVE_SYNONYMS, "unexisting_synonym1, unexisting_synonym2");
            reinitializeUnitils(configuration);

            clearDatabase();
            fail("DbMaintainException expected.");
        } catch (DbMaintainException e) {
            // expected
        }

    }
}
