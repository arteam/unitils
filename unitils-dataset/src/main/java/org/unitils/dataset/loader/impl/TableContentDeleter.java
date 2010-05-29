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
package org.unitils.dataset.loader.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.database.Value;
import org.unitils.dataset.core.dataset.DataSetRow;
import org.unitils.dataset.database.DatabaseAccessor;
import org.unitils.dataset.rowsource.DataSetRowSource;

import java.util.*;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableContentDeleter {

    protected DatabaseAccessor databaseAccessor;
    protected IdentifierNameProcessor identifierNameProcessor;


    public void init(IdentifierNameProcessor identifierNameProcessor, DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
        this.identifierNameProcessor = identifierNameProcessor;
    }


    public void deleteDataFromTablesInReverseOrder(DataSetRowSource dataSetRowSource) {
        List<String> qualifiedTableNames = getQualifiedTableNames(dataSetRowSource);
        Collections.reverse(qualifiedTableNames);
        for (String qualifiedTableName : qualifiedTableNames) {
            try {
                deleteTableContent(qualifiedTableName);
            } catch (Exception e) {
                throw new UnitilsException("Unable to delete data from table " + qualifiedTableName, e);
            }
        }
    }


    protected List<String> getQualifiedTableNames(DataSetRowSource dataSetRowSource) {
        List<String> qualifiedTableNames = new ArrayList<String>();
        Map<String, String> qualifiedTableNameMap = new LinkedHashMap<String, String>();

        DataSetRow dataSetRow;
        while ((dataSetRow = dataSetRowSource.getNextDataSetRow()) != null) {
            String qualifiedTableName = identifierNameProcessor.getQualifiedTableName(dataSetRow);
            if (!qualifiedTableNameMap.containsKey(qualifiedTableName)) {
                qualifiedTableNameMap.put(qualifiedTableName, qualifiedTableName);
                qualifiedTableNames.add(qualifiedTableName);
            }
        }
        return qualifiedTableNames;
    }


    protected void deleteTableContent(String qualifiedTableName) throws Exception {
        String sql = createStatement(qualifiedTableName);
        databaseAccessor.executeUpdate(sql, new ArrayList<Value>());
    }

    protected String createStatement(String qualifiedTableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ");
        stringBuilder.append(qualifiedTableName);
        return stringBuilder.toString();
    }

}