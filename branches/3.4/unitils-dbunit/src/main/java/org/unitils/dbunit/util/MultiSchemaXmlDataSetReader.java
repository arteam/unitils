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
package org.unitils.dbunit.util;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.dbunit.dataset.datatype.DataType.UNKNOWN;
import static org.dbunit.dataset.ITable.NO_VALUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.OrderedTableNameMap;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.datatype.DataType;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.datasetfactory.impl.DbUnitDataSet;
import org.unitils.dbunit.datasetfactory.impl.DbUnitTable;
import org.unitils.util.ReflectionUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
        protected String defaultSchemaName;

        /* All created data sets per schema */
        protected Map<String, DbUnitDataSet> dbUnitDataSetsPerSchemaName = new LinkedHashMap<String, DbUnitDataSet>();


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
        public MultiSchemaDataSet getMultiSchemaDataSet() {
            MultiSchemaDataSet multiSchemaDataSet = new MultiSchemaDataSet();
            for (String schemaName : dbUnitDataSetsPerSchemaName.keySet()) {
                DbUnitDataSet dataSet = dbUnitDataSetsPerSchemaName.get(schemaName);

                // wrap data sets in replacement data sets, and replace [null] tokens by the null reference
                ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
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

            DbUnitDataSet dbUnitDataSet = dbUnitDataSetsPerSchemaName.get(schemaName);
            if (dbUnitDataSet == null) {
                dbUnitDataSet = new DbUnitDataSet();
                dbUnitDataSetsPerSchemaName.put(schemaName, dbUnitDataSet);
            }

            DbUnitTable table = dbUnitDataSet.getDbUnitTable(localName);
            if (table == null) {
                table = new DbUnitTable(localName);
                dbUnitDataSet.addTable(table);
            }
            addRow(attributes, table);
        }

        /**
         * Gets column names and row values from the given attribute and adds a new row to the given table.
         *
         * @param table      The table to add the row to, not null
         * @param attributes the attributes, not null
         */
        protected void addRow(Attributes attributes, DbUnitTable table) {
            if (attributes.getLength() == 0) {
                return;
            }
            for (int i = 0; i < attributes.getLength(); i++) {
                Column column = new Column(attributes.getQName(i), UNKNOWN);
                table.addColumn(column);
            }
            List<Object> row = new ArrayList<Object>(10);
            for (String columnName : table.getColumnNames()) {
                Object value = NO_VALUE;
                if (attributes.getIndex(columnName) != -1) {
                    value = attributes.getValue(columnName);
                }
                row.add(value);
            }
            table.addRow(row);
        }

        /**
         * Overridden to rethrow exception.
         *
         * @param e The exception
         */
        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
    }
}
