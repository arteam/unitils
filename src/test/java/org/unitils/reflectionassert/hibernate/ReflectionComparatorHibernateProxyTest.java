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
package org.unitils.reflectionassert.hibernate;

import org.hibernate.SessionFactory;
import org.junit.After;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.ReflectionAssert;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import static java.util.Arrays.asList;
import java.util.Properties;


/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionComparator}.
 * <p/>
 * Currently this is only implemented for HsqlDb.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorHibernateProxyTest extends UnitilsJUnit4 {

    private Child testChild;

    @TestDataSource
    protected DataSource dataSource;

    @HibernateSessionFactory("org/unitils/reflectionassert/hibernate/hibernate.cfg.xml")
    protected SessionFactory sessionFactory;

    /* Class under test */
    private ReflectionComparator reflectionComparator;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        testChild = new Child(1L, new Parent(1L));
        testChild.getParent().setChildren(asList(testChild));

        reflectionComparator = createRefectionComparator();
        dropTestTables();
        createTestTables();
    }


    /**
     * Removes the test database tables from the test database, to avoid inference with other tests
     */
    @After
    public void tearDown() throws Exception {
        if (disabled) {
            return;
        }
        dropTestTables();
    }


    /**
     * todo javadoc
     */
    @Test
    public void testGetDifference_proxy() {
        //Child childWithParentProxy1 = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);
        //Child childWithParentProxy2 = (Child) sessionFactory.getCurrentSession().get(Child.class, 2L);
        //Difference result = reflectionComparator.getDifference(childWithParentProxy1, childWithParentProxy2);

        //ReflectionAssert.assertLenEquals(childWithParentProxy1, childWithParentProxy2);

       // assertNull(result);

        //todo remove
        //ReflectionAssert.assertLenEquals(asList(new Child(1L, new Parent(1L)), new Child(2L, new Parent(2L)), 3), asList(new Child(1L, new Parent(2L)), new Child(2L, new Parent(1L))));
        ReflectionAssert.assertLenEquals(asList(1, null), asList(3, 4));
        //ReflectionAssert.assertLenEquals(1, 2);
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() {
        executeUpdate("create table PARENT (id bigint not null, primary key (id))", dataSource);
        executeUpdate("create table CHILD (id bigint not null, parent_id bigint not null, primary key (id))", dataSource);
        executeUpdate("alter table CHILD add constraint CHILDTOPARENT foreign key (parent_id) references PARENT", dataSource);
        executeUpdate("insert into PARENT (id) values (1)", dataSource);
        executeUpdate("insert into PARENT (id) values (2)", dataSource);
        executeUpdate("insert into CHILD (id, parent_id) values (1, 1)", dataSource);
        executeUpdate("insert into CHILD (id, parent_id) values (2, 2)", dataSource);
    }


    /**
     * Removes the test tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table CHILD", dataSource);
        executeUpdateQuietly("drop table PARENT", dataSource);
    }
}