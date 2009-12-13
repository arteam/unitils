/*
 * Copyright 2009,  Unitils.org
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
package org.unitils.dataset.core;

import java.util.*;

/**
 * A data set containing a collection of schema's.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSet {

    /* Maps schema names on schema's */
    private Map<String, Schema> schemas = new HashMap<String, Schema>();


    /**
     * Returns the schema for the given name, if any
     *
     * @param schemaName The schema name, not null
     * @return The schema, or null if none registered for the given schema name
     */
    public Schema getSchema(String schemaName) {
        return schemas.get(schemaName);
    }

    /**
     * Adds a schema. If a schema with the same name already exists, it will replace the old one.
     *
     * @param schema The schema, not null
     */
    public void addSchema(Schema schema) {
        schemas.put(schema.getName(), schema);
    }

    /**
     * @return The names of all schema's, not null
     */
    public Set<String> getSchemaNames() {
        return schemas.keySet();
    }

    /**
     * @return The schema's, not null
     */
    public List<Schema> getSchemas() {
        return new ArrayList<Schema>(schemas.values());
    }

}