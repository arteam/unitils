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
package org.unitils.dbunit.util;

import static org.apache.commons.lang.StringUtils.isEmpty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.unitils.core.UnitilsException;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A reader for DbUnit xml datasets that creates a new ITable instance for each element (row).
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
public class MultiSchemaXmlDataSetReader {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(MultiSchemaXmlDataSetReader.class);

    /* The schema name to use when none is specified */
    private String defaultSchemaName;


    /**
     * Creates a data set reader.
     *
     * @param defaultSchemaName The schema name to use when none is specified, not null
     */
    public MultiSchemaXmlDataSetReader(String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
    }


    /**
     * Parses the datasets from the given files.
     * Each schema is given its own dataset and each row is given its own table.
     *
     * @param dataSetFiles The dataset files, not null
     * @return The read data set, not null
     */
    public MultiSchemaDataSet readDataSetXml(File... dataSetFiles) {
        try {
            DataSetContentHandler dataSetContentHandler = new DataSetContentHandler(defaultSchemaName);
            XMLReader xmlReader = createXMLReader();
            xmlReader.setContentHandler(dataSetContentHandler);
            xmlReader.setErrorHandler(dataSetContentHandler);

            for (File dataSetFile : dataSetFiles) {
                InputStream dataSetInputStream = null;
                try {
                    dataSetInputStream = new FileInputStream(dataSetFile);
                    xmlReader.parse(new InputSource(dataSetInputStream));
                } finally {
                    closeQuietly(dataSetInputStream);
                }
            }
            return dataSetContentHandler.getMultiSchemaDataSet();

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


    /**
     * The xml content handler that is going to create the data sets.
     */
    protected static class DataSetContentHandler extends DefaultHandler {

        /* The schema name to use when none is specified */
        private String defaultSchemaName;

        /* All created datasets per schema */
        private Map<String, CachedDataSet> dataSets = new HashMap<String, CachedDataSet>();


        /**
         * Creates a data set SAX content handler
         *
         * @param defaultSchemaName The schema name to use when none is specified, not null
         */
        public DataSetContentHandler(String defaultSchemaName) {
            this.defaultSchemaName = defaultSchemaName;
        }


        /**
         * Gets the result data set.
         *
         * @return the data set, not null
         */
        public MultiSchemaDataSet getMultiSchemaDataSet() throws DataSetException {
            // finalize all data sets
            for (CachedDataSet dataSet : dataSets.values()) {
                dataSet.endDataSet();
            }

            MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();
            for (String schemaName : dataSets.keySet()) {
                CachedDataSet cachedDataSet = dataSets.get(schemaName);

                // wrap datasets in replacement datasets, and replace [null] tokens by the null reference
                ReplacementDataSet replacementDataSet = new ReplacementDataSet(cachedDataSet);
                replacementDataSet.addReplacementObject("[null]", null);
                multiSchemaDataSet.setDataSetForSchema(schemaName, replacementDataSet);
            }
            return multiSchemaDataSet;
        }


        /**
         * Processes an xml element. A new table is started for each element.
         *
         * @param uri        the xml namespace uri (= schema name)
         * @param localName  the local xml name
         * @param qName      the element name (should be table name for table rows)
         * @param attributes the attributes (should be table columns for table rows)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            try {
                // begin element of data set, if default namespace set, it will override the default schema
                if ("dataset".equals(localName)) {
                    if (!isEmpty(uri)) {
                        defaultSchemaName = uri;
                    }
                    return;
                }

                // Begin new table for row
                String schemaName = defaultSchemaName;
                if (!isEmpty(uri)) {
                    schemaName = uri;
                }

                CachedDataSet dataSet = dataSets.get(schemaName);
                if (dataSet == null) {
                    dataSet = new CachedDataSet();
                    dataSet.startDataSet();
                    dataSets.put(schemaName, dataSet);
                }

                ITableMetaData tableMetaData = createTableMetaData(localName, attributes);
                dataSet.startTable(tableMetaData);

                // add row values if there are any
                String[] rowValues = getRowValues(tableMetaData.getColumns(), attributes);
                if (rowValues != null) {
                    dataSet.row(rowValues);
                }

                // end table for row
                dataSet.endTable();

            } catch (DataSetException e) {
                throw new SAXException(e);
            }
        }


        /**
         * Creates meta data for a table with the given name containing columns for each of the attributes.
         *
         * @param tableName  the table name, not null
         * @param attributes the attributes, not null
         * @return the meta data, not null
         */
        protected ITableMetaData createTableMetaData(String tableName, Attributes attributes) {
            Column[] columns = new Column[attributes.getLength()];
            for (int i = 0; i < attributes.getLength(); i++) {
                columns[i] = new Column(attributes.getQName(i), DataType.UNKNOWN);
            }
            return new DefaultTableMetaData(tableName, columns);
        }


        /**
         * Gets the attribute values corresponding to each of the given columns.
         *
         * @param columns    the columns, not null
         * @param attributes the attributes, not null
         * @return the values, null if no values
         */
        protected String[] getRowValues(Column[] columns, Attributes attributes) {
            if (columns.length == 0 || attributes.getLength() == 0) {
                return null;
            }

            String[] rowValues = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                Column column = columns[i];
                rowValues[i] = attributes.getValue(column.getColumnName());
            }
            return rowValues;
        }


        /**
         * Overriden to rethrow exception.
         *
         * @param e The exception
         */
        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
    }
}
