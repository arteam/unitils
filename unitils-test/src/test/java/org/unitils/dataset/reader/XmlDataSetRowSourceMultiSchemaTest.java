/*
 * Copyright Unitils.org
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
package org.unitils.dataset.reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.dataset.DataSetRow;
import org.unitils.dataset.core.dataset.DataSetSettings;
import org.unitils.dataset.rowsource.impl.XmlDataSetRowSource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Tests for reading an xml data set using multiple schemas
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetRowSourceMultiSchemaTest extends UnitilsJUnit4 {

    /* Tested object */
    private XmlDataSetRowSource xmlDataSetStaxReader;

    private DataSetSettings defaultDataSetSettings;

    @Before
    public void setUp() throws Exception {
        defaultDataSetSettings = new DataSetSettings('=', '$', false);
    }

    @After
    public void cleanUp() throws Exception {
        xmlDataSetStaxReader.close();
    }


    @Test
    public void schemaDisDefaultDataSetInXml_overridesDefaultSchemaA() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("MultiSchemaDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_D", row1.getSchemaName());

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row2.getSchemaName());

        DataSetRow row3 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_D", row3.getSchemaName());

        DataSetRow row4 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_B", row4.getSchemaName());
    }

    @Test
    public void noDefaultSchemaInDataSetXml_schemaAisDefault() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("MultiSchemaNoDefaultDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row1.getSchemaName());

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_B", row2.getSchemaName());

        DataSetRow row3 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_A", row3.getSchemaName());

        DataSetRow row4 = xmlDataSetStaxReader.getNextDataSetRow();
        assertEquals("SCHEMA_C", row4.getSchemaName());
    }


    private File getDataSetFile(String dataSetFileNames) throws Exception {
        return toFile(getClass().getResource(dataSetFileNames));
    }

}