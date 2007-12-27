package org.unitils.integrationtest.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.jpa.annotation.JpaEntityManagerFactory;
import org.unitils.reflectionassert.ReflectionAssert;

public class HibernateJpaTest extends UnitilsJUnit4 {

	@JpaEntityManagerFactory(persistenceUnit = "test", configFiles = {"org/unitils/integrationtest/dao/jpa/persistence-test.xml"})
	EntityManagerFactory entityManagerFactory;
	
	EntityManager entityManager;
	
	Person person;
	
    @Before
    public void initializeFixture() {
    	person = new Person(1L, "johnDoe");
        entityManager = entityManagerFactory.createEntityManager();
    }
    
    @Test
    @DataSet("../datasets/SinglePerson.xml")
    public void testFindById() {
    	Person userFromDb = entityManager.find(Person.class, 1L);
    	ReflectionAssert.assertLenEquals(person, userFromDb);
    }

    @Test
    @DataSet("../datasets/NoPersons.xml")
    @ExpectedDataSet("../datasets/SinglePerson-result.xml")
    public void testPersist() {
    	entityManager.persist(person);
    }
}
