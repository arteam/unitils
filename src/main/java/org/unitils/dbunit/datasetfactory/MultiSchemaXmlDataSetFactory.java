/*
 * Copyright 2006 the original author or authors.
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

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReader;

import java.io.InputStream;

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
     * Initializes the factory.
     *
     * @param defaultSchemaName The default schema name to use, not null
     */
    public void init(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }


    /**
     * Creates a {@link MultiSchemaDataSet} using the given file. The file's contents are provided.
     *
     * @param dataSetInputStreams The contents of the dataset files
     * @return A {@link MultiSchemaDataSet} that represents the dataset
     */
    public MultiSchemaDataSet createDataSet(InputStream... dataSetInputStreams) {
        try {
            MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader(defaultSchemaName);
            return multiSchemaXmlDataSetReader.readDataSetXml(dataSetInputStreams);

        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for input streams.", e);
        }
    }


    /**
     * @return The extension that files which can be interpreted by this factory must have
     */
    public String getDataSetFileExtension() {
        return "xml";
    }

}
