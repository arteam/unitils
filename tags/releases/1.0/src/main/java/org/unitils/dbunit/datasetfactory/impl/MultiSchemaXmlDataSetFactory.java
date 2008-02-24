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
package org.unitils.dbunit.datasetfactory.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReader;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

/**
 * A data set factory that can handle data set definitions for multiple database schemas.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiSchemaXmlDataSetFactory implements DataSetFactory {

    /**
     * The schema name to use when no name was explicitly specified.
     */
    protected String defaultSchemaName;


    /**
     * Initializes this DataSetFactory
     *
     * @param configuration     The configuration, not null
     * @param defaultSchemaName The name of the default schema of the test database, not null
     */
    public void init(Properties configuration, String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }


    /**
     * Creates a {@link MultiSchemaDataSet} using the given file.
     *
     * @param dataSetFiles The dataset files, not null
     * @return A {@link MultiSchemaDataSet} containing the datasets per schema, not null
     */
    public MultiSchemaDataSet createDataSet(File... dataSetFiles) {
        try {
            MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader(defaultSchemaName);
            return multiSchemaXmlDataSetReader.readDataSetXml(dataSetFiles);
        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for data set files: " + Arrays.toString(dataSetFiles), e);
        }
    }


    /**
     * @return The extension that files which can be interpreted by this factory must have
     */
    public String getDataSetFileExtension() {
        return "xml";
    }

}
