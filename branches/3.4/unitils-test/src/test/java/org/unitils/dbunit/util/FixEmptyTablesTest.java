package org.unitils.dbunit.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.OrderedTableNameMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.dataset.Table;
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReader.DataSetContentHandler;
import org.unitils.util.ReflectionUtils;


/**
 * Test the empty tables fix.
 * 
 * @author wiw
 * 
 * @since 3.4.1
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FixEmptyTablesTest {

    private DataSetContentHandler sut;
    private CachedDataSet dataSet;
    private String tableName;
    private Column[] oldColumns;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Map<String, Table> tables = new HashMap<String, Table>();
        sut = new DataSetContentHandler("PUBLIC", tables);
        dataSet = new CachedDataSet();
        tableName = "person";
        oldColumns = new Column[0];
    }

    @Test
    public void testEmptyTable() throws SecurityException, NoSuchFieldException {
        sut.setCachedDataSetColumnsWithCorrectTables(dataSet, tableName, oldColumns);
        Assert.assertEquals(0, getOrdererdTableNameMap(dataSet).getTableNames().length);
    }

    @Test
    public void testTableValueNotNull() throws Exception {
        DefaultDataSet defaultDataSet = new DefaultDataSet();
        defaultDataSet.addTable(new DefaultTable(tableName));
        dataSet = new CachedDataSet(defaultDataSet);
        OrderedTableNameMap tableNameMap = getOrdererdTableNameMap(dataSet);
        setOrderedTableNameMap(dataSet, tableNameMap);
        sut.setCachedDataSetColumnsWithCorrectTables(dataSet, tableName, oldColumns);
        Assert.assertNotNull(getOrdererdTableNameMap(dataSet));;
    }


    private OrderedTableNameMap getOrdererdTableNameMap(CachedDataSet dataSet) throws SecurityException, NoSuchFieldException {
        Field tablesField = CachedDataSet.class.getDeclaredField("_tables");
        tablesField.setAccessible(true);
        return ReflectionUtils.getFieldValue(dataSet, tablesField);

    }
    private void setOrderedTableNameMap(CachedDataSet dataSet, OrderedTableNameMap tableNameMap) throws SecurityException, NoSuchFieldException {        
        ReflectionUtils.setFieldValue(dataSet, "_tables", tableNameMap);
    }
}
