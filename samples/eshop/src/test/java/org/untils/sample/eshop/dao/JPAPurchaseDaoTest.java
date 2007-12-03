package org.untils.sample.eshop.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.inject.util.PropertyAccess;
import org.unitils.jpa.JpaUnitils;
import org.unitils.jpa.annotation.JpaEntityManagerFactory;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.sample.eshop.dao.JPAPurchaseDao;
import org.unitils.sample.eshop.model.Purchase;
import org.unitils.sample.eshop.model.User;

@DataSet("PurchaseDaoTest.xml")
public class JPAPurchaseDaoTest extends UnitilsJUnit4 {

	@JpaEntityManagerFactory("test")
	private EntityManagerFactory entityManagerFactory;
	
	private EntityManager entityManager;
	
    private JPAPurchaseDao purchaseDao;

    /* Test user */
    private User testUser;

    @Before
    public void initializeFixture() {
    	purchaseDao = new JPAPurchaseDao();
        testUser = new User(1L, null, 0);
        
        entityManager = entityManagerFactory.createEntityManager();
        InjectionUtils.autoInject(entityManager, EntityManager.class, purchaseDao, PropertyAccess.FIELD);
        
        entityManager.getTransaction().begin();
    }
    
    @After
    public void cleanUp() {
    	entityManager.getTransaction().commit();
    }
    
    @Test
    @ExpectedDataSet("PurchaseDaoTest.xml")
    public void testFindById() {
    	Purchase p = purchaseDao.findById(1L);
    	Assert.assertNotNull(p);
    }

    @Test
    public void testCalculateTotalPurchaseAmount() {
        Long totalAmount = purchaseDao.calculateTotalPurchaseAmount(testUser);
        JpaUnitils.flushDatabaseUpdates();
        ReflectionAssert.assertLenEquals(30, totalAmount);
    }
    
    @Test
    public void testCalculateTotalPurchaseAmount1() {
        Long totalAmount = purchaseDao.calculateTotalPurchaseAmount(testUser);
        ReflectionAssert.assertLenEquals(30, totalAmount);
    }
    
    @Test
    public void testMappingWithDatabase() {
    	JpaUnitils.assertMappingWithDatabaseConsistent();
    }
}
