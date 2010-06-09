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
package org.unitils.dataset.rowsource.impl;

import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.util.PropertyUtils;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseDataSetRowSourceFactory {

    public static String DEFAULT_CASE_SENSITIVE_PROPERTY = "dataset.casesensitive.default";
    public static String DEFAULT_LITERAL_TOKEN_PROPERTY = "dataset.literaltoken.default";
    public static String DEFAULT_VARIABLE_TOKEN_PROPERTY = "dataset.variabletoken.default";

    /* The schema name to use when none is specified */
    protected String defaultSchemaName;
    /* The default settings of the data set */
    protected DataSetSettings defaultDataSetSettings;


    /**
     * @param configuration     The unitils configuration, not null
     * @param defaultSchemaName The schema name to use when none is specified, not null
     */
    public void init(Properties configuration, String defaultSchemaName) {
        this.defaultSchemaName = defaultSchemaName;
        this.defaultDataSetSettings = createDefaultDataSetSettings(configuration);
    }

    protected DataSetSettings createDefaultDataSetSettings(Properties configuration) {
        char defaultLiteralToken = PropertyUtils.getString(DEFAULT_LITERAL_TOKEN_PROPERTY, configuration).charAt(0);
        char defaultVariableToken = PropertyUtils.getString(DEFAULT_VARIABLE_TOKEN_PROPERTY, configuration).charAt(0);
        boolean defaultCaseSensitive = PropertyUtils.getBoolean(DEFAULT_CASE_SENSITIVE_PROPERTY, configuration);
        return new DataSetSettings(defaultLiteralToken, defaultVariableToken, defaultCaseSensitive);
    }
}