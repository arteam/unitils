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

import org.dbunit.dataset.Column;
import org.dbunit.dataset.ReplacementDataSet;
import org.unitils.core.UnitilsException;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.MultiSchemaDataSet;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.dbunit.dataset.ITable.NO_VALUE;
import static org.dbunit.dataset.datatype.DataType.UNKNOWN;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * A factory for DbUnit xml data sets.
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
 * The 'first_table' table has no namespace and is therefore linked to SCHEMA_A. The 'second_table' table is prefixed
 * with namespace b which is linked to SCHEMA_B. If no default namespace is defined, the schema that is
 * passed as constructor argument is taken as default schema.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaXmlDataSetFactory implements DataSetFactory {

    /* The schema name to use when none is specified */
    protected String defaultSchemaName;
    protected SAXParserFactory saxParserFactory;


    /**
     * Creates a data set factory.
     *
     * @param defaultSchemaName The schema name to use when none is specified, not null
     */
    public MultiSchemaXmlDataSetFactory(String defaultSchemaName, SAXParserFactory saxParserFactory) {
        this.defaultSchemaName = defaultSchemaName;
        this.saxParserFactory = saxParserFactory;
    }


    /**
     * @return The extension that files which can be interpreted by this factory must have
     */
    public String getDataSetFileExtension() {
        return "xml";
    }

    /**
     * Parses the data sets from the given files.
     * Each schema is given its own data set and each row is given its own table.
     *
     * @param dataSetFiles The data set files, not null
     * @return The read data set, not null
     */
    public MultiSchemaDataSet createDataSet(List<File> dataSetFiles) {
        DataSetContentHandler dataSetContentHandler = new DataSetContentHandler(defaultSchemaName);
        XMLReader xmlReader = createXMLReader();
        xmlReader.setContentHandler(dataSetContentHandler);
        xmlReader.setErrorHandler(dataSetContentHandler);

        for (File dataSetFile : dataSetFiles) {
            readDataSetFile(xmlReader, dataSetFile);
        }
        return dataSetContentHandler.getMultiSchemaDataSet();
    }

    protected void readDataSetFile(XMLReader xmlReader, File dataSetFile) {
        InputStream dataSetInputStream = null;
        try {
            dataSetInputStream = new FileInputStream(dataSetFile);
            xmlReader.parse(new InputSource(dataSetInputStream));

        } catch (Exception e) {
            throw new UnitilsException("Unable to read data set file " + dataSetFile.getName(), e);
        } finally {
            closeQuietly(dataSetInputStream);
        }
    }

    /**
     * Factory method for creating the SAX xml reader.
     *
     * @return the XML reader, not null
     */
    protected XMLReader createXMLReader() {
        try {
            return saxParserFactory.newSAXParser().getXMLReader();

        } catch (Exception e) {
            throw new UnitilsException("Unable to create SAX parser to read data set xml.", e);
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
