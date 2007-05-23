package org.unitils.dbunit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dbunit.dataset.IDataSet;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MultiSchemaDataSet {

    public Map<String, IDataSet> schemaDataSetMap = new HashMap<String, IDataSet>();

    public void addSchemaDataSet(String schema, IDataSet dataSet) {
        schemaDataSetMap.put(schema, dataSet);
    }

    public Set<String> getSchemaNames() {
        return schemaDataSetMap.keySet();
    }

    public IDataSet getDataSetForSchema(String schemaName) {
        return schemaDataSetMap.get(schemaName);
    }
}
