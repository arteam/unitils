package org.unitils.integrationtest.dao.hibernate;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.integrationtest.sampleproject.model.Person;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.spring.annotation.SpringApplicationContext;

//@Transactional(TransactionMode.ROLLBACK)
public class HibernateSpringTest extends UnitilsJUnit4 {

	@SpringApplicationContext({"org/unitils/integrationtest/dao/hibernate/spring-test.xml"})
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
    	Person userFromDb = (Person) SessionFactoryUtils.getSession(sessionFactory, true).get(Person.class, 1L);
    	ReflectionAssert.assertLenEquals(person, userFromDb);
    }

    @Test
    @DataSet("../datasets/NoPersons.xml")
    @ExpectedDataSet("../datasets/SinglePerson-result.xml")
    public void testPersist() {
    	SessionFactoryUtils.getSession(sessionFactory, true).persist(person);
    }

	
}
