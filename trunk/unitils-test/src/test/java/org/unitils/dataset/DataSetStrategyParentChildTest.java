/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dataset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.core.InsertDataSetStrategy;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetStrategyParentChildTest extends DataSetStrategyTestBase {

    /* Tested object */
    protected InsertDataSetStrategy insertDataSetStrategy = new InsertDataSetStrategy();

    @TestDataSource
    private DataSource dataSource;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        insertDataSetStrategy.init(configuration, createDatabase(configuration));
    }

    @Before
    public void createTestTables() {
        dropTestTables();
        executeUpdate("create table parent (pk1 varchar(100) not null, pk2 integer not null, a_column varchar(100), primary key (pk1, pk2))", dataSource);
        executeUpdate("create table child (pk1 varchar(100) not null primary key, fk1 varchar(100), fk2 integer, foreign key (fk1, fk2) references parent(pk1, pk2))", dataSource);
    }

    @After
    public void dropTestTable() {
        executeUpdateQuietly("drop table child", dataSource);
        executeUpdateQuietly("drop table parent", dataSource);
    }


    @Test
    public void insertDataSet() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleParentChildTest.xml"), emptyVariables, getClass());

        assertValueInTable("parent", "pk1", "1");
        assertValueInTable("parent", "pk2", "2");
        assertValueInTable("parent", "a_column", "3");
        assertValueInTable("child", "pk1", "4");
        assertValueInTable("child", "fk1", "1");
        assertValueInTable("child", "fk2", "2");
    }

    @Test
    public void childForeignKeyValuesAreOverriddenByActualParentValues() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleParentChildTest-tryingToOverride.xml"), emptyVariables, getClass());

        assertValueInTable("parent", "pk1", "1");
        assertValueInTable("parent", "pk2", "2");
        assertValueInTable("parent", "a_column", "3");
        assertValueInTable("child", "pk1", "4");
        assertValueInTable("child", "fk1", "1");
        assertValueInTable("child", "fk2", "2");
    }

    @Test(expected = UnitilsException.class)
    public void notAParent() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleParentChildTest-notAParent.xml"), emptyVariables, getClass());
    }

    @Test(expected = UnitilsException.class)
    public void missingParentValueForForeignKey() throws Exception {
        insertDataSetStrategy.perform(asList("DataSetModuleParentChildTest-missingParentValueForForeignKey.xml"), emptyVariables, getClass());
    }
}