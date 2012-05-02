/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.integrationtest.persistence.jdbc;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.unitilsnew.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.integrationtest.sampleproject.dao.PersonDao;
import org.unitils.integrationtest.sampleproject.dao.impl.JdbcPersonDao;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.reflectionassert.ReflectionAssert;

public class JdbcTest extends UnitilsJUnit4 {

	@TestDataSource
	DataSource dataSource;
	
	PersonDao personDao;
	
	Person person;
	
    @Before
    public void initializeFixture() {
    	personDao = new JdbcPersonDao(dataSource);
    	
    	person = new Person(1L, "johnDoe");
    }
    
    @Test
    @DataSet("../datasets/SinglePerson.xml")
    public void testFindById() {
    	Person userFromDb = personDao.findById(1L);
    	ReflectionAssert.assertLenientEquals(person, userFromDb);
    }

    @Test
    @DataSet("../datasets/NoPersons.xml")
    @ExpectedDataSet("../datasets/SinglePerson-result.xml")
    public void testPersist() {
    	personDao.persist(person);
    }
	
}
