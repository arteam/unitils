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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Factory;
import org.unitils.database.core.DataSourceService;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.dbunit.datasetfactory.impl.MultiSchemaXmlDataSetFactory;

import javax.xml.parsers.SAXParserFactory;

/**
 * @author Tim Ducheyne
 */
public class DataSetFactoryFactory implements Factory<DataSetFactory> {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DataSetFactoryFactory.class);

    protected DataSourceService dataSourceService;


    public DataSetFactoryFactory(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }


    public DataSetFactory create() {
        DataSourceWrapper dataSourceWrapper = dataSourceService.getDataSourceWrapper(null);
        String defaultSchemaName = dataSourceWrapper.getDatabaseConfiguration().getDefaultSchemaName();
        SAXParserFactory saxParserFactory = createSAXParserFactory();
        return new MultiSchemaXmlDataSetFactory(defaultSchemaName, saxParserFactory);
    }


    /**
     * Creates a namespace aware sax parser factory.
     * All validation is disabled so data sets can still be loaded when a DTD or XSD is missing
     *
     * @return the factory, not null
     */
    protected SAXParserFactory createSAXParserFactory() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        disableValidation(saxParserFactory);
        return saxParserFactory;
    }

    protected void disableValidation(SAXParserFactory saxParserFactory) {
        saxParserFactory.setValidating(false);
        try {
            saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception e) {
            logger.debug("Unable to set http://xml.org/sax/features/external-parameter-entities feature on SAX parser factory to false. Ignoring exception: " + e.getMessage());
        }
        try {
            saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception e) {
            logger.debug("Unable to set http://apache.org/xml/features/nonvalidating/load-external-dtd feature on SAX parser factory to false. Ignoring exception: " + e.getMessage());
        }
    }
}
