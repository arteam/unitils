package org.unitils.integrationtest.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.reflectionassert.ReflectionAssert;

public class HibernateTest extends UnitilsJUnit4 {

	@HibernateSessionFactory({"org/unitils/integrationtest/dao/hibernate/hibernate-test.cfg.xml"})
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
