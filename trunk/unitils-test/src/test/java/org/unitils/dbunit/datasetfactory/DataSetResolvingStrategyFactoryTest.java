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

import org.junit.Test;

import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class DataSetResolvingStrategyFactoryTest {

    @Test
    public void defaultSetup() {
        DataSetResolvingStrategyFactory dataSetResolvingStrategyFactory = new DataSetResolvingStrategyFactory("true", "prefix");

        DataSetResolvingStrategy dataSetResolvingStrategy = dataSetResolvingStrategyFactory.create();
        assertPropertyReflectionEquals("fileResolver.pathPrefix", "prefix", dataSetResolvingStrategy);
        assertPropertyReflectionEquals("fileResolver.prefixWithPackageName", true, dataSetResolvingStrategy);
    }

    @Test
    public void emptyPathPrefix() {
        DataSetResolvingStrategyFactory dataSetResolvingStrategyFactory = new DataSetResolvingStrategyFactory("true", "");

        DataSetResolvingStrategy dataSetResolvingStrategy = dataSetResolvingStrategyFactory.create();
        assertPropertyReflectionEquals("fileResolver.pathPrefix", "", dataSetResolvingStrategy);
    }
}
