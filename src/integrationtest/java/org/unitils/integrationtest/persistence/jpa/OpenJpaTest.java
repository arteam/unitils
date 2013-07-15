/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.integrationtest.persistence.jpa;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import static junit.framework.Assert.assertEquals;

public class OpenJpaTest extends UnitilsJUnit4 {

    @JpaEntityManagerFactory(persistenceUnit = "test", configFile = "org/unitils/integrationtest/persistence/jpa/openjpa-persistence-test.xml")
    EntityManagerFactory entityManagerFactory;

    @PersistenceContext
    EntityManager entityManager;

    Person person;

    @Before
    public void initializeFixture() {
        person = new Person(1L, "johnDoe");
    }

    @Test
    @DataSet("../datasets/SinglePerson.xml")
    public void testFindById() {
        Person userFromDb = entityManager.find(Person.class, 1L);
        assertEquals("johnDoe", userFromDb.getName());
    }

    @Test
    @DataSet("../datasets/NoPersons.xml")
    @ExpectedDataSet("../datasets/SinglePerson-result.xml")
    public void testPersist() {
        entityManager.persist(person);
    }


}
