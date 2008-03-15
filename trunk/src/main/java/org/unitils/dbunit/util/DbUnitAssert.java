/*
 * Copyright 2006-2007,  Unitils.org
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

import junit.framework.AssertionFailedError;
import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.unitils.core.UnitilsException;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.SQLException;
import java.util.*;

/**
 * Assert class that offers assert methods for testing things that are specific to DbUnit.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitAssert {


    // todo javadoc
    public static void assertDbContentAsExpected(IDataSet expectedDataSet, DbUnitDatabaseConnection dbUnitDatabaseConnection) {
        if (expectedDataSet == null) {
            // no data set should be compared
            return;
        }
        try {
            // get the actual data set
            IDataSet actualDataSet = dbUnitDatabaseConnection.createDataSet();
            IDataSet filteredActualDataSet = new FilteredDataSet(new IncludeTableFilter(expectedDataSet.getTableNames()), actualDataSet);

            Map<String, List<Map<String, String>>> expectedDataSetContents = getDataSetContents(expectedDataSet);
            Map<String, List<Map<String, String>>> actualDataSetContents = getDataSetContents(filteredActualDataSet);
            ReflectionAssert.assertLenEquals(expectedDataSetContents, actualDataSetContents);

        } catch (SQLException e) {
            throw new UnitilsException("Unable to assert whether db content is as expected.", e);
        } catch (DataSetException e) {
            throw new UnitilsException("Unable to assert whether db content is as expected", e);
        }
    }


    public static Map<String, List<Map<String, String>>> getDataSetContents(IDataSet dataSet) {
        Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();
        try {
            ITableIterator tables = dataSet.iterator();
            while (tables.next()) {
                ITable table = tables.getTable();
                String tableName = table.getTableMetaData().getTableName();

                List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
                result.put(tableName, rows);

                Column[] columns = table.getTableMetaData().getColumns();
                for (int i = 0; i < table.getRowCount(); i++) {
                    Map<String, String> row = new HashMap<String, String>();
                    rows.add(row);

                    for (Column column : columns) {
                        String columnName = column.getColumnName();
                        Object value = table.getValue(i, columnName);
                        row.put(columnName, DataType.asString(value));
                    }
                }
            }
        } catch (DataSetException e) {
            throw new UnitilsException("Unable to assert whether data sets are equal.", e);
        }
        return result;
    }


    // todo javadoc
    public static void assertEqualsDataSet(IDataSet expectedDataSet, IDataSet actualDataSet) {
        try {
            Map<String, SortedSet<Integer>> result = new HashMap<String, SortedSet<Integer>>();

            ITableIterator tables = expectedDataSet.iterator();
            int currentIndex = 1;
            // compare expected and actual data set
            while (tables.next()) {
                ITable expectedTable = tables.getTable();
                String expectedTableName = expectedTable.getTableMetaData().getTableName();
                SortedSet<Integer> foundIndices = result.get(expectedTableName);
                if (foundIndices == null) {
                    foundIndices = new TreeSet<Integer>();
                    result.put(expectedTableName, foundIndices);
                }
                int rowCount = expectedTable.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    if (!containsRow(i, expectedTable, actualDataSet, foundIndices)) {
                        throw new AssertionFailedError(formatErrorMessage(expectedTable, i, currentIndex));
                    }
                    currentIndex++;
                }
            }
        } catch (DataSetException e) {
            throw new UnitilsException("Unable to assert whether data sets are equal.", e);
        }
    }


    //todo javadoc
    private static boolean containsRow(int rowIndex, ITable expectedTable, IDataSet filteredActualDataSet, SortedSet<Integer> foundIndices) throws DataSetException {
        int actualRowIndex = 0;
        String expectedTableName = expectedTable.getTableMetaData().getTableName();
        ITableIterator actualTables = filteredActualDataSet.iterator();
        while (actualTables.next()) {
            ITable actualTable = actualTables.getTable();
            if (!expectedTableName.equalsIgnoreCase(actualTable.getTableMetaData().getTableName())) {
                continue;
            }
            Column[] columns = expectedTable.getTableMetaData().getColumns();
            int rowCount = actualTable.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                boolean equal = true;
                for (Column column : columns) {
                    String columnName = column.getColumnName();
                    Object expectedValue = expectedTable.getValue(rowIndex, columnName);
                    Object actualValue = actualTable.getValue(i, columnName);
                    String expectedValueString = DataType.asString(expectedValue);
                    String actualValueString = DataType.asString(actualValue);
                    if (expectedValueString == null && actualValueString == null) {
                        continue;
                    }
                    if (expectedValueString != null && expectedValueString.equals(actualValueString)) {
                        continue;
                    }
                    equal = false;
                    break;
                }
                if (equal) {
                    if (foundIndices.add(i)) {
                        return true;
                    }
                }
                actualRowIndex++;
            }
        }
        return false;
    }


    //todo javadoc
    private static String formatErrorMessage(ITable table, int rowIndex, int dataSetIndex) throws DataSetException {
        StringBuffer message = new StringBuffer("Difference found in expected data set at line " + dataSetIndex);
        ITableMetaData tableMetaData = table.getTableMetaData();
        message.append(", Table: " + tableMetaData.getTableName() + ", values: ");
        Column[] columns = tableMetaData.getColumns();
        for (Column column : columns) {
            String columnName = column.getColumnName();
            Object expectedValue = table.getValue(rowIndex, columnName);
            message.append("\n   " + columnName + " = " + DataType.asString(expectedValue));
        }
        return message.toString();
    }

}
