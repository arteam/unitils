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
package org.unitils.core.util;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.unitils.UnitilsJUnit4;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;
import static org.junit.Assert.assertEquals;
import static org.unitils.database.DatabaseUnitils.commitTransaction;
import static org.unitils.database.DatabaseUnitils.startTransaction;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
@ContextConfiguration
public class ObjectFormatterFormatHibernateProxyTest extends UnitilsJUnit4 {

    private ObjectFormatter objectFormatter;

    @Autowired
    protected SessionFactory sessionFactory;


    @Before
    public void initialize() {
        objectFormatter = new ObjectFormatter();

        dropTestTables();
        createTestTables();
        startTransaction();
    }

    @After
    public void cleanUp() throws Exception {
        dropTestTables();
    }


    @Test
    public void nullValue() {
        String result = objectFormatter.format(null);
        assertEquals("null", result);
        commitTransaction();
    }

    @Test
    public void attachedEntity() {
        Child childWithParentProxy = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);

        String result = objectFormatter.format(childWithParentProxy);
        assertEquals("ObjectFormatterFormatHibernateProxyTest.Child<id=1, value=\"111\", parent=ObjectFormatterFormatHibernateProxyTest.Parent<id=1, value=\"11\">>", result);
        commitTransaction();
    }

    @Test
    public void detachedEntity() {
        Child childWithParentProxy = (Child) sessionFactory.getCurrentSession().get(Child.class, 1L);
        commitTransaction();

        String result = objectFormatter.format(childWithParentProxy);
        assertEquals("ObjectFormatterFormatHibernateProxyTest.Child<id=1, value=\"111\", parent=Proxy<ObjectFormatterFormatHibernateProxyTest.Parent>>", result);
    }


    private void createTestTables() {
        executeUpdate("create table PARENT (id bigint not null, value varchar, primary key (id))");
        executeUpdate("create table CHILD (id bigint not null, parent_id bigint not null, value varchar, primary key (id))");
        executeUpdate("alter table CHILD add constraint CHILDTOPARENT foreign key (parent_id) references PARENT");
        executeUpdate("insert into PARENT (id, value) values (1, '11')");
        executeUpdate("insert into CHILD (id, parent_id, value) values (1, 1, '111')");
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table CHILD");
        executeUpdateQuietly("drop table PARENT");
    }


    @Entity(name = "PARENT")
    public static class Parent {

        @Id
        private Long id;
        @Column
        private String value;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Entity(name = "CHILD")
    public static class Child {

        @Id
        private Long id;
        @Column
        private String value;
        @ManyToOne(fetch = LAZY, optional = true)
        private Parent parent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Parent getParent() {
            return parent;
        }

        public void setParent(Parent parent) {
            this.parent = parent;
        }
    }
}
