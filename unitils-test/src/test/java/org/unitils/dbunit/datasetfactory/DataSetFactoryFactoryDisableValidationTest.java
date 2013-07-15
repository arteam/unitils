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
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import javax.xml.parsers.SAXParserFactory;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetFactoryFactoryDisableValidationTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetFactoryFactory dataSetFactoryFactory;

    private Mock<SAXParserFactory> saxParserFactoryMock;


    @Before
    public void initialize() {
        dataSetFactoryFactory = new DataSetFactoryFactory(null);
    }


    @Test
    public void disableValidation() throws Exception {
        dataSetFactoryFactory.disableValidation(saxParserFactoryMock.getMock());

        saxParserFactoryMock.assertInvoked().setValidating(false);
        saxParserFactoryMock.assertInvoked().setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        saxParserFactoryMock.assertInvoked().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    }

    @Test
    public void ignoreDisableEntitiesFailures() throws Exception {
        saxParserFactoryMock.raises(new NullPointerException()).setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        dataSetFactoryFactory.disableValidation(saxParserFactoryMock.getMock());

        saxParserFactoryMock.assertInvoked().setValidating(false);
        saxParserFactoryMock.assertInvoked().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    }

    @Test
    public void ignoreDisableExternalDtdFailures() throws Exception {
        saxParserFactoryMock.raises(new NullPointerException()).setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        dataSetFactoryFactory.disableValidation(saxParserFactoryMock.getMock());

        saxParserFactoryMock.assertInvoked().setValidating(false);
        saxParserFactoryMock.assertInvoked().setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    }
}
