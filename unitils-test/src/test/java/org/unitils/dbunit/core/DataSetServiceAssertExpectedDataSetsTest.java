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
import org.unitils.core.UnitilsException;
import org.unitils.core.context.Context;
import org.unitils.dbunit.connection.DbUnitConnection;
import org.unitils.dbunit.connection.DbUnitConnectionManager;
import org.unitils.dbunit.dataset.Schema;
import org.unitils.dbunit.dataset.SchemaFactory;
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.DataSetResolvingStrategy;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 */
public class DataSetServiceAssertExpectedDataSetsTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetService dataSetService;

    private Mock<DataSetResolvingStrategy> dataSetResolverMock;
    private Mock<DbUnitConnectionManager> dbUnitDatabaseConnectionManagerMock;
    private Mock<DataSetAssert> dataSetAssertMock;
    private Mock<SchemaFactory> schemaFactoryMock;
    private Mock<Context> contextMock;

    private Mock<DataSetFactory> dataSetFactoryMock;
    private Mock<DataSetFactory> defaultDataSetFactoryMock;
    private Mock<MultiSchemaDataSet> multiSchemaDataSetMock;
    private Mock<DbUnitConnection> dbUnitDatabaseConnectionMock1;
    private Mock<DbUnitConnection> dbUnitDatabaseConnectionMock2;

    private Method testMethod;
    @Dummy
    private IDataSet expectedDataSet1;
    @Dummy
    private IDataSet expectedDataSet2;
    @Dummy
    private IDataSet actualDataSet1;
    @Dummy
    private IDataSet actualDataSet2;

    private Schema expectedSchema1;
    private Schema expectedSchema2;
    private Schema actualSchema1;
    private Schema actualSchema2;

    private File testFile1;
    private File testFile2;


    @Before
    public void initialize() throws Exception {
        dataSetService = new DataSetService(dataSetResolverMock.getMock(), dataSetAssertMock.getMock(), schemaFactoryMock.getMock(),
                dbUnitDatabaseConnectionManagerMock.getMock(), contextMock.getMock());

        testMethod = MyClass.class.getMethod("testMethod");
        testFile1 = new File("file1");
        testFile2 = new File("file1");
        expectedSchema1 = new Schema("schema1");
        expectedSchema1.addTable(new Table("table1"));
        expectedSchema1.addTable(new Table("table2"));
        expectedSchema2 = new Schema("schema2");
        expectedSchema2.addTable(new Table("table3"));
        actualSchema1 = new Schema("schema1");
        actualSchema2 = new Schema("schema2");

        contextMock.returns(defaultDataSetFactoryMock).getInstanceOfType(DataSetFactory.class);

        multiSchemaDataSetMock.returns(asSet("schema1", "schema2")).getSchemaNames();
        dbUnitDatabaseConnectionManagerMock.returns(dbUnitDatabaseConnectionMock1).getDbUnitConnection("schema1");
        dbUnitDatabaseConnectionManagerMock.returns(dbUnitDatabaseConnectionMock2).getDbUnitConnection("schema2");
        dbUnitDatabaseConnectionMock1.returns(actualDataSet1).createDataSet();
        dbUnitDatabaseConnectionMock2.returns(actualDataSet2).createDataSet();
        multiSchemaDataSetMock.returns(expectedDataSet1).getDataSetForSchema("schema1");
        multiSchemaDataSetMock.returns(expectedDataSet2).getDataSetForSchema("schema2");
        schemaFactoryMock.returns(expectedSchema1).createSchemaForDbUnitDataSet("schema1", expectedDataSet1);
        schemaFactoryMock.returns(expectedSchema2).createSchemaForDbUnitDataSet("schema2", expectedDataSet2);
        schemaFactoryMock.returns(actualSchema1).createSchemaForDbUnitDataSet("schema1", actualDataSet1, asList("table1", "table2"));
        schemaFactoryMock.returns(actualSchema2).createSchemaForDbUnitDataSet("schema2", actualDataSet2, asList("table3"));
    }


    @Test
    public void assertExpectedDataSets() {
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        dataSetResolverMock.returns(testFile2).resolve(MyClass.class, "dataSet2.xml");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1, testFile2));

        dataSetService.assertExpectedDataSets(asList("dataSet1.xml", "dataSet2.xml"), testMethod, MyClass.class, null);

        dataSetAssertMock.assertInvoked().assertEqualSchemas(expectedSchema1, actualSchema1);
        dbUnitDatabaseConnectionMock1.assertInvoked().closeJdbcConnection();
        dataSetAssertMock.assertInvoked().assertEqualSchemas(expectedSchema2, actualSchema2);
        dbUnitDatabaseConnectionMock2.assertInvoked().closeJdbcConnection();
    }

    @Test
    public void useDefaultFileNameWhenNullFileNames() {
        defaultDataSetFactoryMock.returns("txt").getDataSetFileExtension();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "DataSetServiceAssertExpectedDataSetsTest$MyClass.testMethod-result.txt");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.assertExpectedDataSets(null, testMethod, MyClass.class, null);

        dataSetAssertMock.assertInvoked().assertEqualSchemas(expectedSchema1, actualSchema1);
    }

    @Test
    public void useDefaultFileNameWhenEmptyFileNames() {
        defaultDataSetFactoryMock.returns("txt").getDataSetFileExtension();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "DataSetServiceAssertExpectedDataSetsTest$MyClass.testMethod-result.txt");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.assertExpectedDataSets(Collections.<String>emptyList(), testMethod, MyClass.class, null);

        dataSetAssertMock.assertInvoked().assertEqualSchemas(expectedSchema1, actualSchema1);
    }

    @Test
    public void useDefaultFileWithoutMethodNameWhenMethodIsNull() {
        defaultDataSetFactoryMock.returns("txt").getDataSetFileExtension();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "DataSetServiceAssertExpectedDataSetsTest$MyClass-result.txt");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.assertExpectedDataSets(null, null, MyClass.class, null);

        dataSetAssertMock.assertInvoked().assertEqualSchemas(expectedSchema1, actualSchema1);
    }

    @Test
    public void customDataSetFactory() {
        contextMock.returns(dataSetFactoryMock).getInstanceOfType(MyDataSetFactory.class);
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        dataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.assertExpectedDataSets(asList("dataSet1.xml"), testMethod, MyClass.class, MyDataSetFactory.class);

        dataSetAssertMock.assertInvoked().assertEqualSchemas(expectedSchema1, actualSchema1);
    }

    @Test
    public void emptyMultiSchemaDataSet() {
        multiSchemaDataSetMock.onceReturns(emptySet()).getSchemaNames();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        dataSetService.assertExpectedDataSets(asList("dataSet1.xml"), testMethod, MyClass.class, null);

        dataSetAssertMock.assertNotInvoked().assertEqualSchemas(null, null);
    }

    @Test
    public void exceptionWhenDbUnitFailsToCreateDataSet() throws Exception {
        dbUnitDatabaseConnectionMock1.onceRaises(new NullPointerException("expected")).createDataSet();
        dataSetResolverMock.returns(testFile1).resolve(MyClass.class, "dataSet1.xml");
        defaultDataSetFactoryMock.returns(multiSchemaDataSetMock).createDataSet(asList(testFile1));

        try {
            dataSetService.assertExpectedDataSets(asList("dataSet1.xml"), testMethod, MyClass.class, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to assert expected data set for schema schema1\nReason: NullPointerException: expected", e.getMessage());
            dbUnitDatabaseConnectionMock1.assertInvoked().closeJdbcConnection();
        }
    }

    private class MyClass {

        public void testMethod() {
        }
    }

    private static interface MyDataSetFactory extends DataSetFactory {
    }
}
