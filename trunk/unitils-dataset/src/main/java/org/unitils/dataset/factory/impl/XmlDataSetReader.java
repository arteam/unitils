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
package org.unitils.dataset.factory.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.DataSet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * A reader for xml datasets.
 * <p/>
 * Following format is expected:
 * <code><pre>
 * &lt;dataset&gt;
 *      &lt;first_table  myColumn1="value1" myColumn2="value2" /&gt;
 *      &lt;second_table myColumnA="A" /&gt;
 *      &lt;first_table  myColumn2="other value2" /&gt;
 *      &lt;empty_table /&gt;
 * &lt;/dataset&gt;
 * </pre></code>
 * <p/>
 * Elements for a table may occur more than once and anywhere in the data set. If multiple elements
 * exist, they may specify different attributes (columns). Missing attributes (columns) will be treated as null values.
 * <p/>
 * Namespaces can be used to specify tables from different database schemas. The namespace URI should contain the name
 * of the database schema:
 * <code><pre>
 * &lt;dataset xmlns="SCHEMA_A" xmlns:b="SCHEMA_B"&gt;
 *      &lt;first_table  myColumn1="value1" myColumn2="value2" /&gt;
 *      &lt;b:second_table myColumnA="A" /&gt;
 *      &lt;first_table  myColumn2="other value2" /&gt;
 *      &lt;empty_table /&gt;
 * &lt;/dataset&gt;
 * </pre></code>
 * <p/>
 * This example defines 2 schemas: SCHEMA_A and SCHEMA_B. The first schema is set as default schema (=default namespace).
 * The 'first_table' table has no namespce and is therefore linked to SCHEMA_A. The 'second_table' table is prefixed
 * with namespace b which is linked to SCHEMA_B. If no default namespace is defined, the schema that is
 * passed as constructor argument is taken as default schema.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetReader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(XmlDataSetReader.class);

    /* The schema name to use when none is specified */
    private String defaultSchemaName;
    /* The case-sensitivity to use when none is specified */
    private boolean defaultCaseSensitive;
    /* The literal token to use when none is specified */
    private char defaultLiteralToken;
    /*  The variable toke to use when none is specified */
    private char defaultVariableToken;


    /**
     * Creates a data set reader.
     *
     * @param defaultSchemaName    The schema name to use when none is specified, not null
     * @param defaultCaseSensitive The case-sensitivity to use when none is specified
     * @param defaultLiteralToken  The literal token to use when none is specified
     * @param defaultVariableToken The variable toke to use when none is specified
     */
    public XmlDataSetReader(String defaultSchemaName, boolean defaultCaseSensitive, char defaultLiteralToken, char defaultVariableToken) {
        this.defaultSchemaName = defaultSchemaName;
        this.defaultCaseSensitive = defaultCaseSensitive;
        this.defaultLiteralToken = defaultLiteralToken;
        this.defaultVariableToken = defaultVariableToken;
    }


    /**
     * Parses the data sets from the given files.
     *
     * @param dataSetFile The data set file, not null
     * @return The read schema collection, not null
     */
    public DataSet readDataSetXml(File dataSetFile) {
        try {
            XmlDataSetSaxContentHandler dataSetContentHandler = new XmlDataSetSaxContentHandler(defaultSchemaName, defaultCaseSensitive, defaultLiteralToken, defaultVariableToken);
            XMLReader xmlReader = createXMLReader();
            xmlReader.setContentHandler(dataSetContentHandler);
            xmlReader.setErrorHandler(dataSetContentHandler);

            InputStream dataSetInputStream = null;
            try {
                dataSetInputStream = new FileInputStream(dataSetFile);
                xmlReader.parse(new InputSource(dataSetInputStream));
            } finally {
                closeQuietly(dataSetInputStream);
            }
            return dataSetContentHandler.getDataSet();

        } catch (Exception e) {
            throw new UnitilsException("Unable to parse data set xml.", e);
        }
    }


    /**
     * Factory method for creating the SAX xml reader.
     *
     * @return the XML reader, not null
     */
    protected XMLReader createXMLReader() {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);

            // disable validation, so dataset can still be used when a DTD or XSD is missing
            disableValidation(saxParserFactory);
            return saxParserFactory.newSAXParser().getXMLReader();

        } catch (Exception e) {
            throw new UnitilsException("Unable to create SAX parser to read data set xml.", e);
        }
    }


    /**
     * Disables validation on the given sax parser factory.
     *
     * @param saxParserFactory The factory, not null
     */
    protected void disableValidation(SAXParserFactory saxParserFactory) {
        saxParserFactory.setValidating(false);
        try {
            saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception e) {
            logger.debug("Unable to set http://xml.org/sax/features/external-parameter-entities feature on SAX parser factory to false. Igoring exception: " + e.getMessage());
        }
        try {
            saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception e) {
            logger.debug("Unable to set http://apache.org/xml/features/nonvalidating/load-external-dtd feature on SAX parser factory to false. Igoring exception: " + e.getMessage());
        }
    }


}