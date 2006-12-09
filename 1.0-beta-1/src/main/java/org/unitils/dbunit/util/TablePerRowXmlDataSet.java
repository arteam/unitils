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
package org.unitils.dbunit.util;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.InputStream;

/**
 * A DbUnit xml dataset that creates a new ITable instance for each element (row).
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
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TablePerRowXmlDataSet extends CachedDataSet {


    /**
     * Creates a data set.
     *
     * @param in the xml content stream, not null
     */
    public TablePerRowXmlDataSet(InputStream in) throws DataSetException {

        TablePerRowXmlProducer producer = new TablePerRowXmlProducer(in);
        producer.setConsumer(this);
        producer.produce();
    }


    /**
     * The xml content handler that is going to fill the enclosed dataset.
     */
    public class TablePerRowXmlProducer extends FlatXmlProducer {


        /**
         * Creates a producer
         *
         * @param in the xml content stream, not null
         */
        public TablePerRowXmlProducer(InputStream in) {
            super(new InputSource(in), false);
        }


        /**
         * Processes an xml element. A new table is started for each element.
         *
         * @param uri        the xml namespace uri
         * @param localName  the local xml name
         * @param qName      the element name (should be table name for table rows)
         * @param attributes the attributes (should be table columns for table rows)
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            try {

                // begin element of data set
                if ("dataset".equals(qName)) {
                    startDataSet();
                    return;
                }

                // Begin new table for row
                ITableMetaData tableMetaData = createTableMetaData(qName, attributes);
                startTable(tableMetaData);

                // add row values if there are any
                String[] rowValues = getRowValues(tableMetaData.getColumns(), attributes);
                if (rowValues != null) {
                    row(rowValues);
                }

                // end table for row
                endTable();
            }

            catch (DataSetException e) {
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
        protected ITableMetaData createTableMetaData(String tableName, Attributes attributes) throws DataSetException {

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

    }
}
