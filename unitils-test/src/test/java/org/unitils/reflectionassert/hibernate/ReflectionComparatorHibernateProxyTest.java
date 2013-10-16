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
package org.unitils.reflectionassert.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotation.TestDataSource;
import org.unitils.database.annotation.Transactional;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.difference.Difference;

import javax.sql.DataSource;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Transactional(COMMIT)
@ContextConfiguration
public class ReflectionComparatorHibernateProxyTest extends UnitilsJUnit4 {

    private static Log logger = LogFactory.getLog(ReflectionComparatorHibernateProxyTest.class);

    private ReflectionComparator reflectionComparator;

    /* A test hibernate entity, with a link to a lazily loaded parent class */
    private Child testChild;
    @TestDataSource
    protected DataSource dataSource;
    @Autowired
    protected SessionFactory sessionFactory;
    /* True if current test is not for the current dialect */
    private boolean disabled;


    @Before
    public void initialize() throws Exception {
        testChild = new Child(1L, new Parent(1L));
        testChild.getParent().setChildren(asList(testChild));

        reflectionComparator = createRefectionComparator();
        dropTestTables();
        createTestTables();
    }


    @After
    public void cleanUp() throws Exception {
        if (disabled) {
            return;
        }
        dropTestTables();
    }


    @Test
    public void nullWhenEqualsAndRightSideIsHibernateProxy() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Child childWithParentProxy = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);
        Difference result = reflectionComparator.getDifference(testChild, childWithParentProxy);
        assertNull(result);
    }

    @Test
    public void nullWhenEqualsAndLeftSideIsHibernateProxy() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Child childWithParentProxy = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);
        Difference result = reflectionComparator.getDifference(childWithParentProxy, testChild);

        ReflectionAssert.assertLenientEquals(childWithParentProxy, testChild);
        assertNull(result);
    }

    @Test
    public void nullWhenBothSidesAreHibernateProxiesAndIdentifiersAreEqual() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        // open 2 session two avoid the values being taken from the cache
        Child childWithParentProxy1 = (Child) sessionFactory.openSession().get(Child.class, 1L);
        Child childWithParentProxy2 = (Child) sessionFactory.openSession().get(Child.class, 1L);
        Difference result = reflectionComparator.getDifference(childWithParentProxy1, childWithParentProxy2);
        assertNull(result);
    }

    @Test
    public void differenceWhenBothSidesAreHibernateProxiesAndDifferentIdentifiers() {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        // open 2 session two avoid the values being taken from the cache
        Child childWithParentProxy1 = (Child) sessionFactory.openSession().get(Child.class, 1L);
        Child childWithParentProxy2 = (Child) sessionFactory.openSession().get(Child.class, 2L);
        Difference result = reflectionComparator.getDifference(childWithParentProxy1, childWithParentProxy2);
        assertNotNull(result);
    }


    private void createTestTables() {
        executeUpdate("create table PARENT (id bigint not null, primary key (id))");
        executeUpdate("create table CHILD (id bigint not null, parent_id bigint not null, primary key (id))");
        executeUpdate("alter table CHILD add constraint CHILDTOPARENT foreign key (parent_id) references PARENT");
        executeUpdate("insert into PARENT (id) values (1)");
        executeUpdate("insert into PARENT (id) values (2)");
        executeUpdate("insert into CHILD (id, parent_id) values (1, 1)");
        executeUpdate("insert into CHILD (id, parent_id) values (2, 2)");
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table CHILD");
        executeUpdateQuietly("drop table PARENT");
    }
}