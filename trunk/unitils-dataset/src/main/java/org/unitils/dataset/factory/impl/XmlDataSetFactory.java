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

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.factory.DataSetFactory;
import org.unitils.util.PropertyUtils;

import java.io.File;
import java.util.Properties;

/**
 * A data set factory that can handle data set definitions for multiple database schema's.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class XmlDataSetFactory implements DataSetFactory {

    public static String DEFAULT_CASE_SENSITIVE_PROPERTY = "dataset.casesensitive.default";
    public static String DEFAULT_LITERAL_TOKEN_PROPERTY = "dataset.literaltoken.default";
    public static String DEFAULT_VARIABLE_TOKEN_PROPERTY = "dataset.variabletoken.default";

    /* The schema name to use when no name was explicitly specified. */
    protected String defaultSchemaName;
    /* The case-sensitivity to use when none is specified */
    protected boolean defaultCaseSensitive;
    /* The literal token to use when none is specified */
    protected char defaultLiteralToken;
    /*  The variable toke to use when none is specified */
    protected char defaultVariableToken;


    /**
     * Initializes this DataSetFactory
     *
     * @param configuration     The configuration, not null
     * @param defaultSchemaName The name of the default schema of the test database, not null
     */
    public void init(Properties configuration, String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
        this.defaultCaseSensitive = PropertyUtils.getBoolean(DEFAULT_CASE_SENSITIVE_PROPERTY, configuration);
        this.defaultLiteralToken = PropertyUtils.getString(DEFAULT_LITERAL_TOKEN_PROPERTY, configuration).charAt(0);
        this.defaultVariableToken = PropertyUtils.getString(DEFAULT_VARIABLE_TOKEN_PROPERTY, configuration).charAt(0);
    }


    /**
     * Creates a {@link org.unitils.dataset.core.DataSet} using the given file.
     *
     * @param dataSetFile The data set file, not null
     * @return A {@link org.unitils.dataset.core.DataSet} containing the data sets per schema, not null
     */
    public DataSet createDataSet(File dataSetFile) {
        try {
            XmlDataSetReader xmlDataSetReader = new XmlDataSetReader(defaultSchemaName, defaultCaseSensitive, defaultLiteralToken, defaultVariableToken);
            return xmlDataSetReader.readDataSetXml(dataSetFile);
        } catch (Exception e) {
            throw new UnitilsException("Unable to create data set for data set file: " + dataSetFile, e);
        }
    }


    /**
     * @return The extension that files which can be interpreted by this factory must have
     */
    public String getDataSetFileExtension() {
        return "xml";
    }

}