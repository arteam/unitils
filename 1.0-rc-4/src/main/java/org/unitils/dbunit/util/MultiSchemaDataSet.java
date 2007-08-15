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

import org.dbunit.dataset.IDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a DbUnit dataset that may contain data for multiple database schema's. For each schema, a DbUnit
 * <code>IDataSet</code> object can be registered using {@link #addSchemaDataSet(String, org.dbunit.dataset.IDataSet)}.
 * A schema's <code>IDataSet</code> can later be retrieved using {@link #getDataSetsForSchema(String)}. Getting all the
 * schema names for which a <code>IDataSet</code> exists can be done with {@link #getSchemaNames()}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiSchemaDataSet {

    /* Maps schemanames on dbunit datasets */
    public Map<String, List<IDataSet>> schemaDataSetMap = new HashMap<String, List<IDataSet>>();


    /**
     * Adds a new dbunit <code>IDataSet</code> for the given schema.
     * @param schema The schema name
     * @param dataSet The dbunit dataset
     */
    public void addSchemaDataSet(String schema, IDataSet dataSet) {
    	List<IDataSet> schemaDataSets = schemaDataSetMap.get(schema);
    	if (schemaDataSets == null) {
    		schemaDataSets = new ArrayList<IDataSet>();
    		schemaDataSetMap.put(schema, schemaDataSets);
    	}
    	schemaDataSets.add(dataSet);
    }


    /**
     * @return The names of all schema's for which a dbunit dataset exists
     */
    public Set<String> getSchemaNames() {
        return schemaDataSetMap.keySet();
    }


    /**
     * Returns the dbunit <code>IDataSet</code> for the given schema name, if any
     * @param schemaName The schema name
     * @return The dbunit dataset, or null if none registered for the given schema name
     */
    public List<IDataSet> getDataSetsForSchema(String schemaName) {
        return schemaDataSetMap.get(schemaName);
    }
}
