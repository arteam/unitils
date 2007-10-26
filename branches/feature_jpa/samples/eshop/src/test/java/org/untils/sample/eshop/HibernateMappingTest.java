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
package org.untils.sample.eshop;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.hibernate.HibernateUnitils;
import org.unitils.hibernate.annotation.HibernateSessionFactory;

/**
 * Verfies if the mapping of domain objects is consistent with the database
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class HibernateMappingTest {

	@HibernateSessionFactory({"hibernate.cfg.xml", "hibernate.cfg.xml"})
	private SessionFactory sessionFactory;
	
	@Test
    public void testMappingWithDatabase() {
        HibernateUnitils.assertMappingWithDatabaseConsistent();
    }

}
