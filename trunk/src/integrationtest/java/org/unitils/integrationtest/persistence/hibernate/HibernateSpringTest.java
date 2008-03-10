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
package org.unitils.integrationtest.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.spring.annotation.SpringApplicationContext;

//@Transactional(TransactionMode.ROLLBACK)
public class HibernateSpringTest extends UnitilsJUnit4 {

	@SpringApplicationContext({"org/unitils/integrationtest/persistence/hibernate/hibernateSpringTest-spring.xml"})
	ApplicationContext applicationContext;
	
	@HibernateSessionFactory
	SessionFactory sessionFactory;
	
	Person person;
	
    @Before
    public void initializeFixture() {
    	person = new Person(1L, "johnDoe");
    }
    
    @Test
    @DataSet("../datasets/SinglePerson.xml")
    public void testFindById() {
    	Person userFromDb = (Person) sessionFactory.getCurrentSession().get(Person.class, 1L);
    	ReflectionAssert.assertLenEquals(person, userFromDb);
    }

    @Test
    @DataSet("../datasets/NoPersons.xml")
    @ExpectedDataSet("../datasets/SinglePerson-result.xml")
    public void testPersist() {
    	sessionFactory.getCurrentSession().persist(person);
    }

	
}
