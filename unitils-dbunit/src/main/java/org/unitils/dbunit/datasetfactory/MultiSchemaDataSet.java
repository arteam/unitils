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
package org.unitils.dbunit.datasetfactory;

import org.dbunit.dataset.IDataSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a DbUnit data set that may contain data for multiple database schema's. For each schema, a DbUnit
 * <code>IDataSet</code> object can be registered using {@link #setDataSetForSchema(String, org.dbunit.dataset.IDataSet)}.
 * A schema's <code>IDataSet</code> can later be retrieved using {@link #getDataSetForSchema(String)}. Getting all the
 * schema names for which a <code>IDataSet</code> exists can be done with {@link #getSchemaNames()}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MultiSchemaDataSet {

    /* Maps schema names on DbUnit data sets */
    protected Map<String, IDataSet> schemaDataSetMap = new HashMap<String, IDataSet>();


    /**
     * Returns the dbunit <code>IDataSet</code> for the given schema name, if any
     *
     * @param schemaName The schema name
     * @return The dbunit data set, or null if none registered for the given schema name
     */
    public IDataSet getDataSetForSchema(String schemaName) {
        return schemaDataSetMap.get(schemaName);
    }

    /**
     * Sets a dbunit <code>IDataSet</code> for the given schema. If a data set already existed for this schema,
     * the old one is returned.
     *
     * @param schema  The schema name
     * @param dataSet The dbunit data set
     * @return The replaced data set, null if none replaced
     */
    public IDataSet setDataSetForSchema(String schema, IDataSet dataSet) {
        return schemaDataSetMap.put(schema, dataSet);
    }

    /**
     * @return The names of all schema's for which a dbunit data set exists
     */
    public Set<String> getSchemaNames() {
        return schemaDataSetMap.keySet();
    }
}
