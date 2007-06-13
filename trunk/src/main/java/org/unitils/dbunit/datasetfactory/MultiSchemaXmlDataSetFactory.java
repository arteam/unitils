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
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReader;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.InputStream;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiSchemaXmlDataSetFactory implements DataSetFactory {

    private String defaultSchemaName;

    public void init(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }

    public MultiSchemaDataSet createDataSet(String dataSetFileName, InputStream dataSetFileContents) {
        try {
            MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader(defaultSchemaName);
            return multiSchemaXmlDataSetReader.readDataSetXml(dataSetFileContents);

        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for input stream.", e);
        } finally {
            closeQuietly(dataSetFileContents);
        }
    }

    public String getDataSetFileExtension() {
        return "xml";
    }
}
