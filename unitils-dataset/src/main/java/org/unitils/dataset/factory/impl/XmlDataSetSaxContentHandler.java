/*
 * Copyright 2006-2009,  Unitils.org
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
import org.unitils.dataset.core.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * The xml content handler that is going to create the data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetSaxContentHandler extends DefaultHandler {

    /* The schema name to use when none is specified */
    protected String defaultSchemaName;
    /* The case-sensitivity to use when none is specified */
    protected boolean defaultCaseSensitive;
    /* The literal token to use when none is specified */
    protected char defaultLiteralToken;
    /*  The variable toke to use when none is specified */
    protected char defaultVariableToken;

    /* The resulting data set */
    protected DataSet dataSet;

    protected boolean unitilsDataSetNamespaceDeclared;

    protected boolean notExists;

    protected boolean caseSensitive;

    protected char literalToken;

    protected char variableToken;

    protected Stack<Row> parentRows = new Stack<Row>();


    /**
     * Creates a data set SAX content handler
     *
     * @param defaultSchemaName    The schema name to use when none is specified, not null
     * @param defaultCaseSensitive The case-sensitivity to use when none is specified
     * @param defaultLiteralToken  The literal token to use when none is specified
     * @param defaultVariableToken The variable toke to use when none is specified
     */
    public XmlDataSetSaxContentHandler(String defaultSchemaName, boolean defaultCaseSensitive, char defaultLiteralToken, char defaultVariableToken) {
        this.defaultSchemaName = defaultSchemaName;
        this.defaultCaseSensitive = defaultCaseSensitive;
        this.defaultLiteralToken = defaultLiteralToken;
        this.defaultVariableToken = defaultVariableToken;
    }


    /**
     * @return the result schema collection, not null
     */
    public DataSet getDataSet() {
        return dataSet;
    }


    /**
     * Processes an xml element.
     *
     * @param uri        the xml namespace uri (= schema name)
     * @param localName  the local xml name
     * @param qName      the element name (should be table name for table rows)
     * @param attributes the attributes (should be table columns for table rows)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("dataset".equals(localName)) {
            handleDataSetElement(uri, attributes);
            return;
        }
        if (isUnitilsDataSetNamespace(uri) && "notExists".equals(localName)) {
            handleNotExistsStartElement();
            return;
        }
        handleRowElement(uri, localName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isUnitilsDataSetNamespace(uri) && "notExists".equals(localName)) {
            handleNotExistsEndElement();
            return;
        }
        if (!parentRows.isEmpty()) {
            parentRows.pop();
        }
    }


    protected void handleDataSetElement(String uri, Attributes attributes) {
        unitilsDataSetNamespaceDeclared = "unitils-dataset".equals(uri);
        caseSensitive = getCaseSensitive(attributes);
        dataSet = createDataSet(attributes);
    }

    protected void handleNotExistsStartElement() {
        notExists = true;
    }

    protected void handleNotExistsEndElement() {
        notExists = false;
    }

    protected void handleRowElement(String uri, String localName, Attributes attributes) {
        String schemaName = getSchemaName(uri);
        addRow(schemaName, localName, attributes);
    }


    protected boolean isUnitilsDataSetNamespace(String uri) {
        return "unitils-dataset".equals(uri) || !unitilsDataSetNamespaceDeclared;
    }


    protected boolean getCaseSensitive(Attributes attributes) {
        String caseSensitiveAttribute = attributes.getValue("caseSensitive");
        if (caseSensitiveAttribute == null) {
            return defaultCaseSensitive;
        }
        if ("true".equalsIgnoreCase(caseSensitiveAttribute)) {
            return true;
        }
        if ("false".equalsIgnoreCase(caseSensitiveAttribute)) {
            return false;
        }
        throw new UnitilsException("Invalid case sensitive attribute value " + caseSensitiveAttribute + ". The value should be 'true' or 'false'.");
    }

    protected char getLiteralToken(Attributes attributes) {
        String literalTokenAttribute = attributes.getValue("literalToken");
        if (literalTokenAttribute == null) {
            return defaultLiteralToken;
        }
        if (literalTokenAttribute.length() != 1) {
            throw new UnitilsException("Invalid literal token attribute value " + literalTokenAttribute + ". The value should be a single character.");
        }
        return literalTokenAttribute.charAt(0);
    }

    protected char getVariableToken(Attributes attributes) {
        String variableTokenAttribute = attributes.getValue("variableToken");
        if (variableTokenAttribute == null) {
            return defaultVariableToken;
        }
        if (variableTokenAttribute.length() != 1) {
            throw new UnitilsException("Invalid variable token attribute value " + variableTokenAttribute + ". The value should be a single character.");
        }
        return variableTokenAttribute.charAt(0);
    }


    protected void addRow(String schemaName, String tableName, Attributes attributes) {
        Row parentRow = getParentRow();
        Row row = new Row(parentRow, notExists);
        for (int i = 0; i < attributes.getLength(); i++) {
            Column column = new Column(attributes.getQName(i), attributes.getValue(i), caseSensitive);
            row.addColumn(column);
        }
        Table table = getTable(schemaName, tableName);
        table.addRow(row);
        parentRows.push(row);
    }

    protected Schema getSchema(String schemaName) {
        Schema schema = dataSet.getSchema(schemaName);
        if (schema == null) {
            schema = new Schema(schemaName, caseSensitive);
            dataSet.addSchema(schema);
        }
        return schema;
    }

    protected Table getTable(String schemaName, String tableName) {
        Schema schema = getSchema(schemaName);
        Table table = schema.getTable(tableName);
        if (table == null) {
            table = new Table(tableName, caseSensitive);
            schema.addTable(table);
        }
        return table;
    }


    protected Row getParentRow() {
        if (parentRows.isEmpty()) {
            return null;
        }
        return parentRows.peek();
    }

    protected String getSchemaName(String uri) {
        if (isEmpty(uri)) {
            return defaultSchemaName;
        }
        return uri;
    }


    protected DataSet createDataSet(Attributes attributes) {
        char literalToken = getLiteralToken(attributes);
        char variableToken = getVariableToken(attributes);
        return new DataSet(literalToken, variableToken);
    }


    /**
     * Overridden to re-throw exceptions.
     *
     * @param e The exception
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }
}