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

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * The xml content handler that is going to create the data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetSaxContentHandler extends DefaultHandler {

    /* The schema name to use when none is specified */
    private String defaultSchemaName;
    /* The case-sensitivity to use when none is specified */
    private boolean defaultCaseSensitive;
    /* The literal token to use when none is specified */
    private char defaultLiteralToken;
    /*  The variable toke to use when none is specified */
    private char defaultVariableToken;

    /* The resulting data set */
    private DataSet dataSet;

    private boolean caseSensitive;

    private char literalToken;

    private char variableToken;

    private Set<String> deleteTableOrder;


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
        // begin element of data set, if default namespace set, it will override the default schema
        if ("dataset".equals(localName)) {
            if (!isEmpty(uri)) {
                defaultSchemaName = uri;
            }
            dataSet = createDataSet(attributes);
            caseSensitive = getCaseSensitive(attributes);
            literalToken = getLiteralToken(attributes);
            variableToken = getVariableToken(attributes);
            deleteTableOrder = getDeleteTableOrder(attributes);
            return;
        }
        String schemaName = getSchemaName(uri);
        addSchema(schemaName, localName, attributes, dataSet);
    }

    protected DataSet createDataSet(Attributes attributes) {
        return new DataSet();
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

    protected Set<String> getDeleteTableOrder(Attributes attributes) {
        String deleteTableOrderAttribute = attributes.getValue("deleteTableOrder");
        if (deleteTableOrderAttribute == null) {
            return new HashSet<String>();
        }
        try {
            Set<String> tableNames = new HashSet<String>();
            for (String tableName : attributes.getValue("deleteTableOrder").split(",")) {
                String trimmedTableName = tableName.trim();
                if (trimmedTableName.length() != 0) {
                    tableNames.add(trimmedTableName);
                }
            }
            return tableNames;
        } catch (Exception e) {
            throw new UnitilsException("Invalid deleteTableOrder attribute value " + deleteTableOrderAttribute + ". The value should be a comma-separated list of table names.");
        }
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


    protected void addSchema(String schemaName, String tableName, Attributes attributes, DataSet schemaCollection) {
        Schema schema = schemaCollection.getSchema(schemaName);
        if (schema == null) {
            schema = new Schema(schemaName, caseSensitive, deleteTableOrder);
            schemaCollection.addSchema(schema);
        }
        addTable(tableName, schema, attributes);
    }

    protected void addTable(String tableName, Schema schema, Attributes attributes) {
        Table table = schema.getTable(tableName);
        if (table == null) {
            table = new Table(tableName, caseSensitive);
            schema.addTable(table);
        }
        addRows(attributes, table);
    }

    protected void addRows(Attributes attributes, Table table) {
        Row row = new Row();
        for (int i = 0; i < attributes.getLength(); i++) {
            Column column = new Column(attributes.getQName(i), attributes.getValue(i), caseSensitive, literalToken, variableToken);
            row.addColumn(column);
        }
        table.addRow(row);
    }

    protected String getSchemaName(String uri) {
        if (isEmpty(uri)) {
            return defaultSchemaName;
        }
        return uri;
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