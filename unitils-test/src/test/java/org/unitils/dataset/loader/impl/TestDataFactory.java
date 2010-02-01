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
package org.unitils.dataset.loader.impl;

import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestDataFactory {


    public static Table createTable(boolean caseSensitive) {
        Schema schema = new Schema("my_schema", caseSensitive);
        Table table = new Table("table_a", caseSensitive);
        schema.addTable(table);
        return table;
    }

    public static Row createRow(Column... columns) {
        Row row = new Row();
        for (Column column : columns) {
            row.addColumn(column);
        }
        Table table = createTable(false);
        table.addRow(row);
        return row;
    }

    public static Row createRow() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        row.addColumn(createColumn("column_2", "2"));
        row.addColumn(createColumn("pk1", "3"));
        row.addColumn(createColumn("pk2", "4"));

        Table table = createTable(false);
        table.addRow(row);
        return row;
    }

    public static Row createCaseSensitiveRow() {
        Row row = new Row();
        row.addColumn(createCaseSensitiveColumn("column_1", "1"));
        row.addColumn(createCaseSensitiveColumn("column_2", "2"));
        row.addColumn(createCaseSensitiveColumn("PK1", "3"));
        row.addColumn(createCaseSensitiveColumn("Pk2", "4"));

        Table table = createTable(true);
        table.addRow(row);
        return row;
    }

    public static Row createRowWithLiteralValues() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "=literal1"));
        row.addColumn(createColumn("column_2", "=literal2"));
        row.addColumn(createColumn("pk1", "=3"));
        row.addColumn(createColumn("pk2", "=4"));

        Table table = createTable(false);
        table.addRow(row);
        return row;
    }

    public static Column createColumn(String name, String value) {
        return new Column(name, value, false);
    }

    public static Column createCaseSensitiveColumn(String name, String value) {
        return new Column(name, value, true);
    }
}