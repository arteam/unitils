package org.untils.sample.eshop;

import org.unitils.hibernate.annotation.HibernateTest;
import org.unitils.hibernate.HibernateAssert;

/**
 * 
 */
@HibernateTest
public class HibernateMappingTest extends BaseHibernateDaoTest {

    public void testMappingToDatabase() {
        HibernateAssert.assertMappingToDatabase();
    }

}
