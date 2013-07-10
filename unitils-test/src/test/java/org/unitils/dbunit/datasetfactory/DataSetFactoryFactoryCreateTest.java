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
package org.unitils.dbunit.datasetfactory;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.core.DataSourceService;
import org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetFactory;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.assertTrue;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DataSetFactoryFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetFactoryFactory dataSetFactoryFactory;

    private Mock<DataSourceService> dataSourceServiceMock;


    @Before
    public void initialize() {
        dataSetFactoryFactory = new DataSetFactoryFactory(dataSourceServiceMock.getMock());

        dataSourceServiceMock.returns("schema").getDataSourceWrapper(isNull(String.class)).getDatabaseConfiguration().getDefaultSchemaName();
    }


    @Test
    public void create() throws Exception {
        DataSetFactory result = dataSetFactoryFactory.create();
        assertTrue(result instanceof MultiSchemaXmlDataSetFactory);
    }
}