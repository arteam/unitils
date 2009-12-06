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
package org.unitils.dataset.factory;

import org.unitils.dataset.core.DataSet;

import java.io.File;
import java.util.Properties;

/**
 * Factory for creating data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface DataSetFactory {


    /**
     * Initializes this DataSetFactory
     *
     * @param configuration     The configuration, not null
     * @param defaultSchemaName The name of the default schema of the test database, not null
     */
    void init(Properties configuration, String defaultSchemaName);


    /**
     * @param dataSetFile The data set file, not null
     * @return A {@link org.unitils.dataset.core.DataSet} containing the schema's in the given data set file, not null
     */
    DataSet createDataSet(File dataSetFile);


    /**
     * @return The extension that files which can be interpreted by this factory must have (should not start with a '.')
     */
    String getDataSetFileExtension();
}