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
package org.unitils.dataset.factory.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.DataSetColumn;
import org.unitils.dataset.core.DataSetRow;
import org.unitils.dataset.core.DataSetSettings;
import org.unitils.dataset.factory.DataSetRowSource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Stack;

import static javax.xml.stream.XMLInputFactory.*;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Data set row source that reads out a data set from an xml file.
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
public class XmlDataSetRowSource implements DataSetRowSource {

    /* The input stream that contains the data set */
    protected InputStream dataSetInputStream;
    /* The stream reader that reads out the input stream */
    protected XMLStreamReader xmlStreamReader;
    /* The schema name to use when none is specified */
    protected String defaultSchemaName;
    /* The default settings of the data set */
    protected DataSetSettings defaultDataSetSettings;

    /* The actual settings of the data set, possibly overridden in the xml */
    protected DataSetSettings dataSetSettings;

    /* True if the unitils namespace is declared */
    protected boolean unitilsDataSetNamespaceDeclared;
    /* True if we're currently in an not exists element */
    protected boolean notExists;

    /* The current stack rows for tracking parent-child nesting */
    protected Stack<DataSetRow> parentDataSetRows = new Stack<DataSetRow>();


    /**
     * Initializes this DataSetFactory
     *
     * @param defaultSchemaName      The schema name to use when none is specified, not null
     * @param defaultDataSetSettings The default settings, not null
     */
    public void init(String defaultSchemaName, DataSetSettings defaultDataSetSettings) {
        this.defaultSchemaName = defaultSchemaName;
        this.defaultDataSetSettings = defaultDataSetSettings;
    }


    /**
     * Opens the given data set file.
     * Don't forget to call close afterwards.
     *
     * @param dataSetFile The data set file, not null
     */
    public void open(File dataSetFile) {
        InputStream dataSetInputStream = null;
        try {
            dataSetInputStream = new BufferedInputStream(new FileInputStream(dataSetFile));
            this.xmlStreamReader = createXMLStreamReader(dataSetInputStream);
        } catch (IOException e) {
            closeQuietly(dataSetInputStream);
            throw new UnitilsException("Unable to open data set file " + dataSetFile.getName(), e);
        }
    }


    /**
     * @return the next row from the data set, null if the end of the data set is reached.
     */
    public DataSetRow getNextDataSetRow() {
        try {
            while (xmlStreamReader.hasNext()) {
                int event = xmlStreamReader.next();
                if (START_ELEMENT == event) {
                    if (isDataSetElement()) {
                        readDataSetSettings();
                        continue;
                    }
                    if (isNotExistsElement()) {
                        notExists = true;
                        continue;
                    }
                    return readDataSetRow();

                } else if (END_ELEMENT == event) {
                    if (!parentDataSetRows.isEmpty()) {
                        parentDataSetRows.pop();
                    }
                    if (isNotExistsElement()) {
                        notExists = false;
                    }
                }
            }
            return null;

        } catch (XMLStreamException e) {
            throw new UnitilsException("Unable to parse data set xml.", e);
        }
    }


    /**
     * Closes the data set file.
     */
    public void close() {
        closeQuietly(dataSetInputStream);
    }


    protected void readDataSetSettings() {
        unitilsDataSetNamespaceDeclared = isUnitilsDataSetNamespaceDeclared();
        dataSetSettings = getDataSetSettings();
    }

    protected DataSetRow readDataSetRow() {
        String schemaName = getSchemaName();
        String tableName = xmlStreamReader.getLocalName();

        DataSetRow parentRow = getParentDataSetRow();
        DataSetRow dataSetRow = new DataSetRow(schemaName, tableName, parentRow, notExists, dataSetSettings);
        for (int i = 0; i < xmlStreamReader.getAttributeCount(); i++) {
            DataSetColumn dataSetColumn = new DataSetColumn(xmlStreamReader.getAttributeLocalName(i), xmlStreamReader.getAttributeValue(i));
            dataSetRow.addDataSetColumn(dataSetColumn);
        }
        parentDataSetRows.push(dataSetRow);
        return dataSetRow;
    }

    protected String getSchemaName() {
        String uri = xmlStreamReader.getNamespaceURI();
        if (isEmpty(uri)) {
            return defaultSchemaName;
        }
        return uri;
    }


    protected boolean isNotExistsElement() {
        String uri = xmlStreamReader.getNamespaceURI();
        String localName = xmlStreamReader.getLocalName();
        return isUnitilsDataSetNamespace(uri) && "notExists".equals(localName);
    }

    protected boolean isDataSetElement() {
        String localName = xmlStreamReader.getLocalName();
        return "dataset".equals(localName);
    }

    protected boolean isUnitilsDataSetNamespaceDeclared() {
        String uri = xmlStreamReader.getNamespaceURI();
        return "unitils-dataset".equals(uri);
    }

    protected boolean isUnitilsDataSetNamespace(String uri) {
        return "unitils-dataset".equals(uri) || !unitilsDataSetNamespaceDeclared;
    }

    protected DataSetRow getParentDataSetRow() {
        if (parentDataSetRows.isEmpty()) {
            return null;
        }
        return parentDataSetRows.peek();
    }

    protected DataSetSettings getDataSetSettings() {
        char literalToken = getLiteralToken();
        char variableToken = getVariableToken();
        boolean caseSensitive = getCaseSensitive();
        return new DataSetSettings(literalToken, variableToken, caseSensitive);
    }

    protected boolean getCaseSensitive() {
        String caseSensitiveAttribute = xmlStreamReader.getAttributeValue(null, "caseSensitive");
        if (caseSensitiveAttribute == null) {
            return defaultDataSetSettings.isCaseSensitive();
        }
        if ("true".equalsIgnoreCase(caseSensitiveAttribute)) {
            return true;
        }
        if ("false".equalsIgnoreCase(caseSensitiveAttribute)) {
            return false;
        }
        throw new UnitilsException("Invalid case sensitive attribute value " + caseSensitiveAttribute + ". The value should be 'true' or 'false'.");
    }

    protected char getLiteralToken() {
        String literalTokenAttribute = xmlStreamReader.getAttributeValue(null, "literalToken");
        if (literalTokenAttribute == null) {
            return defaultDataSetSettings.getLiteralToken();
        }
        if (literalTokenAttribute.length() != 1) {
            throw new UnitilsException("Invalid literal token attribute value " + literalTokenAttribute + ". The value should be a single character.");
        }
        return literalTokenAttribute.charAt(0);
    }

    protected char getVariableToken() {
        String variableTokenAttribute = xmlStreamReader.getAttributeValue(null, "variableToken");
        if (variableTokenAttribute == null) {
            return defaultDataSetSettings.getVariableToken();
        }
        if (variableTokenAttribute.length() != 1) {
            throw new UnitilsException("Invalid variable token attribute value " + variableTokenAttribute + ". The value should be a single character.");
        }
        return variableTokenAttribute.charAt(0);
    }


    /**
     * Factory method for creating the STAX xml reader.
     *
     * @param dataSetInputStream The stream containing the data set, not null
     * @return the XML stream reader, not null
     */
    protected XMLStreamReader createXMLStreamReader(InputStream dataSetInputStream) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setProperty(IS_NAMESPACE_AWARE, true);
            inputFactory.setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            inputFactory.setProperty(IS_VALIDATING, false);
            inputFactory.setProperty(IS_REPLACING_ENTITY_REFERENCES, false);
            return inputFactory.createXMLStreamReader(new BufferedInputStream(dataSetInputStream));

        } catch (XMLStreamException e) {
            throw new UnitilsException("Unable to create STAX parser to read data set xml.", e);
        }
    }

}