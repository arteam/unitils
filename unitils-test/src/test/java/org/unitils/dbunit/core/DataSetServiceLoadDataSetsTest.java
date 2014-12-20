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
package org.unitils.dbunit.core;

import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.context.Context;
import org.unitils.dbunit.connection.DbUnitConnection;
import org.unitils.dbunit.connection.DbUnitConnectionManager;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.DataSetResolvingStrategy;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.io.File;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.unitils.core.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 */
public class DataSetServiceLoadDataSetsTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetService dataSetService;

    private Mock<DataSetResolvingStrategy> dataSetResolverMock;
    private Mock<DbUnitConnectionManager> dbUnitDatabaseConnectionManagerMock;
    private Mock<Context> contextMock;

    private Mock<DataSetFactory> dataSetFactoryMock;
    private Mock<DataSetFactory> defaultDataSetFactoryMock;
    private Mock<DataSetLoadStrategy> dataSetLoadStrategyMock;
    private Mock<DataSetLoadStrategy> defaultDataSetLoadStrategyMock;
    private Mock<MultiSchemaDataSet> multiSchemaDataSetMock;
    private Mock<DbUnitConnection> dbUnitDatabaseConnectionMock1;
    private Mock<DbUnitConnection> dbUnitDatabaseConnectionMock2;

    @Dummy
    private IDataSet dataSet1;
    @Dummy
    private IDataSet dataSet2;
    private File testFile1;
    private File testFile2;


    @Before
    public void initialize() {
        dataSetService = new DataSetService(dataSetResolverMock.getMock(), null, null, dbUnitDatabaseConnectionManagerMock.getMock(), contextMock.getMock());

        testFile1 = new File("file1");
        testFile2 = new File("file1");

        contextMock.returns(defaultDataSetLoadStrategyMock).getInstanceOfType(DataSetLoadStrategy.class);
        contextMock.returns(defaultDataSetFactoryMock).getInstanceOfType(DataSetFactory.class);

        multiSchemaDataSetMock.returns(asSet("schema1", "schema2")).getSchemaNames();
        dbUnitDatabaseConnectionManagerMock.returns(dbUnitDatabaseConnectionMock1).getDbUnitConnection("schema1");
        dbUnitDatabaseConnectionManagerMock.returns(dbUnitDatabaseConnectionMock2).getDbUnitConnection("schema2");
        multiSchemaDataSetMock.returns(dataSet1).getDataSetForSchema("schema1");
        multiSchemaDataSetMock.returns(dataSet2).getDataSetForSchema("schema2");
    }


    @Test
    public void loadDataSets() {
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        dataSetResolverMock.returns(testFile2).resolve(MyClass.class, "dataSet2.xml");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1, testFile2));

        dataSetService.loadDataSets(asList("dataSet1.xml", "dataSet2.xml"), MyClass.class, null, null);

        defaultDataSetLoadStrategyMock.assertInvoked().loadDataSet(dbUnitDatabaseConnectionMock1.getMock(), dataSet1);
        dbUnitDatabaseConnectionMock1.assertInvoked().closeJdbcConnection();
        defaultDataSetLoadStrategyMock.assertInvoked().loadDataSet(dbUnitDatabaseConnectionMock2.getMock(), dataSet2);
        dbUnitDatabaseConnectionMock2.assertInvoked().closeJdbcConnection();
    }

    @Test
    public void useDefaultFileNameWhenNullFileNames() {
        defaultDataSetFactoryMock.returns("txt").getDataSetFileExtension();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "DataSetServiceLoadDataSetsTest$MyClass.txt");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.loadDataSets(null, MyClass.class, null, null);

        defaultDataSetLoadStrategyMock.assertInvoked().loadDataSet(dbUnitDatabaseConnectionMock1.getMock(), dataSet1);
    }

    @Test
    public void useDefaultFileNameWhenEmptyFileNames() {
        defaultDataSetFactoryMock.returns("txt").getDataSetFileExtension();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "DataSetServiceLoadDataSetsTest$MyClass.txt");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.loadDataSets(Collections.<String>emptyList(), MyClass.class, null, null);

        defaultDataSetLoadStrategyMock.assertInvoked().loadDataSet(dbUnitDatabaseConnectionMock1.getMock(), dataSet1);
    }

    @Test
    public void customDataSetLoadStrategy() {
        contextMock.returns(dataSetLoadStrategyMock).getInstanceOfType(MyDataSetLoadStrategy.class);
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.loadDataSets(asList("dataSet1.xml"), MyClass.class, MyDataSetLoadStrategy.class, null);

        dataSetLoadStrategyMock.assertInvoked().loadDataSet(dbUnitDatabaseConnectionMock1.getMock(), dataSet1);
    }

    @Test
    public void customDataSetFactory() {
        contextMock.returns(dataSetFactoryMock).getInstanceOfType(MyDataSetFactory.class);
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        dataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.loadDataSets(asList("dataSet1.xml"), MyClass.class, null, MyDataSetFactory.class);

        defaultDataSetLoadStrategyMock.assertInvoked().loadDataSet(dbUnitDatabaseConnectionMock1.getMock(), dataSet1);
    }

    @Test
    public void emptyMultiSchemaDataSet() {
        multiSchemaDataSetMock.onceReturnsAll().getSchemaNames();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.loadDataSets(asList("dataSet1.xml"), MyClass.class, null, null);

        defaultDataSetLoadStrategyMock.assertNotInvoked().loadDataSet(null, null);
    }

    private class MyClass {
    }

    private static interface MyDataSetFactory extends DataSetFactory {
    }

    private static interface MyDataSetLoadStrategy extends DataSetLoadStrategy {
    }
}
