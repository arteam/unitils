package org.unitils.dataset.core;

import org.unitils.dataset.core.comparison.ColumnDifference;


/**
 * A column in a data set row
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Column {

    /* The name of the data set column */
    private String name;

    /* The value */
    private String value;


    /**
     * Creates a value
     *
     * @param name  The name of the data set column, not null
     * @param value The value, can be null
     */
    public Column(String name, String value) {
        this.name = name;
        this.value = value;
    }


    /**
     * @return The name of the data set column, not null
     */
    public String getName() {
        return name;
    }


    /**
     * @return The value, can be null
     */
    public String getValue() {
        return value;
    }


    /**
     * Compares the column with the given actual column. If the actual column has a different type, the value
     * will be casted to this type.
     *
     * @param actualColumn The column to compare with, not null
     * @return The difference, null if none found
     */
    public ColumnDifference compare(Column actualColumn) {
        if (value == null && actualColumn.getValue() != null) {
            return new ColumnDifference(this, actualColumn);
        }
        if (!value.equals(actualColumn.getValue())) {
            return new ColumnDifference(this, actualColumn);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name);
        stringBuilder.append(": ");
        stringBuilder.append(value);
        return stringBuilder.toString();
    }

}