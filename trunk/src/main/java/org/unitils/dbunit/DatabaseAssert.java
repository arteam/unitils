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
package org.unitils.dbunit;

import org.unitils.core.TestContext;
import org.unitils.core.Unitils;
import org.unitils.hibernate.HibernateModule;

/**
 * @author Filip Neven
 */
public class DatabaseAssert {

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     */
    public static void assertDBContentAsExpected() throws Exception {

        Unitils unitils = Unitils.getInstance();
        TestContext testContext = unitils.getTestContext();

        HibernateModule hibernateModule = unitils.getModulesRepository().getFirstModule(HibernateModule.class);
        if (hibernateModule != null) { // If Hibernate support is not activated in the Unitils configuration, the Hibernate module will be null
            if (hibernateModule.isHibernateTest(testContext.getTestClass())) {  //todo check null
                hibernateModule.flushDatabaseUpdates();
            }
        }
        DbUnitModule dbUnitModule = unitils.getModulesRepository().getFirstModule(DbUnitModule.class);
        dbUnitModule.assertDBContentAsExpected(testContext.getTestObject(), testContext.getTestMethod().getName());
    }

}
