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
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.DataSetRow;
import org.unitils.dataset.core.DataSetSettings;
import org.unitils.dataset.factory.impl.XmlDataSetRowSource;

import java.io.File;

import static org.junit.Assert.*;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.toFile;

/**
 * Tests for reading an xml data set
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetStaxReaderDataSetSettingsTest extends UnitilsJUnit4 {

    /* Tested object */
    private XmlDataSetRowSource xmlDataSetStaxReader;

    private DataSetSettings defaultDataSetSettings;


    @Before
    public void initialize() throws Exception {
        defaultDataSetSettings = new DataSetSettings('=', '$', false);
    }

    @After
    public void cleanUp() throws Exception {
        xmlDataSetStaxReader.close();
    }


    @Test
    public void defaultDataSetAttributes() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("LessColumnsLastDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        DataSetSettings dataSetSettings1 = row1.getDataSetSettings();
        assertEquals('=', dataSetSettings1.getLiteralToken());
        assertEquals('$', dataSetSettings1.getVariableToken());
        assertFalse(dataSetSettings1.isCaseSensitive());

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        DataSetSettings dataSetSettings2 = row2.getDataSetSettings();
        assertSame(dataSetSettings1, dataSetSettings2);
    }

    @Test
    public void dataSetSettingsReusedForEveryRow() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("LessColumnsLastDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();

        DataSetRow row1 = xmlDataSetStaxReader.getNextDataSetRow();
        DataSetSettings dataSetSettings1 = row1.getDataSetSettings();

        DataSetRow row2 = xmlDataSetStaxReader.getNextDataSetRow();
        DataSetSettings dataSetSettings2 = row2.getDataSetSettings();
        assertSame(dataSetSettings1, dataSetSettings2);
    }

    @Test
    public void overridingDataSetAttributes() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("OverridingAttributesDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();

        DataSetRow row = xmlDataSetStaxReader.getNextDataSetRow();
        DataSetSettings dataSetSettings = row.getDataSetSettings();
        assertEquals('%', dataSetSettings.getLiteralToken());
        assertEquals(':', dataSetSettings.getVariableToken());
        assertTrue(dataSetSettings.isCaseSensitive());
    }

    @Test(expected = UnitilsException.class)
    public void invalidCaseSensitiveValue() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("InvalidCaseSensitiveValueDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();
        xmlDataSetStaxReader.getNextDataSetRow();
    }

    @Test(expected = UnitilsException.class)
    public void invalidLiteralTokenValue() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("InvalidLiteralTokenValueDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();
        xmlDataSetStaxReader.getNextDataSetRow();
    }

    @Test(expected = UnitilsException.class)
    public void invalidVariableTokenValue() throws Exception {
        xmlDataSetStaxReader = new XmlDataSetRowSource(getDataSetFile("InvalidVariableTokenValueDataSet.xml"), "SCHEMA_A", defaultDataSetSettings);
        xmlDataSetStaxReader.open();
        xmlDataSetStaxReader.getNextDataSetRow();
    }


    private File getDataSetFile(String dataSetFileNames) throws Exception {
        return toFile(getClass().getResource(dataSetFileNames));
    }

}