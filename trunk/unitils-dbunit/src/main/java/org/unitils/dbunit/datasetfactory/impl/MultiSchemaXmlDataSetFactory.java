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
package org.unitils.dbunit.datasetfactory.impl;

import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseUnitils;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;

import java.io.File;
import java.util.List;

/**
 * A data set factory that can handle data set definitions for multiple database schemas.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaXmlDataSetFactory implements DataSetFactory {

    /**
     * Creates a {@link MultiSchemaDataSet} using the given file.
     *
     * @param dataSetFiles The data set files, not null
     * @return A {@link MultiSchemaDataSet} containing the data sets per schema, not null
     */
    public MultiSchemaDataSet createDataSet(List<File> dataSetFiles) {
        try {
            String defaultSchemaName = DatabaseUnitils.getDataSourceWrapper().getDatabaseConfiguration().getDefaultSchemaName();
            MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader(defaultSchemaName);
            return multiSchemaXmlDataSetReader.readDataSetXml(dataSetFiles);
        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit data set for data set files: " + dataSetFiles, e);
        }
    }

    /**
     * @return The extension that files which can be interpreted by this factory must have
     */
    public String getDataSetFileExtension() {
        return "xml";
    }
}
